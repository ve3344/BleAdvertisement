package com.imitee.bleadv.lib.advertise;

import com.github.hypfvieh.DbusHelper;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.imitee.bleadv.lib.base.AdvertiseException;
import com.imitee.bleadv.lib.base.BleConstants;
import com.imitee.bleadv.lib.base.BleCore;
import com.imitee.bleadv.lib.base.BleDevice;
import com.imitee.bleadv.lib.base.BleDiscoveryFilter;
import com.imitee.bleadv.lib.base.BusConnector;
import com.imitee.bleadv.lib.base.ConnectionListener;

import org.bluez.Device1;
import org.bluez.GattManager1;
import org.bluez.LEAdvertisingManager1;
import org.bluez.exceptions.BluezAlreadyExistsException;
import org.bluez.exceptions.BluezDoesNotExistException;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInvalidArgumentsException;
import org.bluez.exceptions.BluezInvalidLengthException;
import org.bluez.exceptions.BluezNotPermittedException;
import org.bluez.exceptions.BluezNotReadyException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusSigHandler;
import org.freedesktop.dbus.interfaces.ObjectManager;
import org.freedesktop.dbus.types.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: luo
 * @create: 2020-05-08 16:56
 **/
public class BleAdvertiser implements ObjectManager {
    //服务列表
    private final List<BleService> services;

    private final BluetoothAdapter adapter;
    private final BusConnector connector;
    private final String objectPath;

    private BleAdvertisement advertisement;
    private AdvertiseType advertiseType = AdvertiseType.BROADCAST;
    private String advertiseName = "";
    private ConnectionListener connectionListener;


    private final GattManager1 gattManager;
    private final LEAdvertisingManager1 advertisingManager;

    private DBusSigHandler<InterfacesAdded> addedHandler;
    private DBusSigHandler<InterfacesRemoved> removedHandler;
    private ObjectManager objectManager;

    public BleAdvertiser(String path) {
        BluetoothAdapter adapter = BleCore.requireAdapter();
        adapter.setPowered(true);
        adapter.setDiscoverable(true);
        adapter.setDiscoverableTimeout(0);
        this.adapter = adapter;
        this.connector = new BusConnector(adapter.getDbusConnection());

        this.objectPath = path;
        this.services = new ArrayList<>();

        gattManager = connector.requireObject(adapter.getDbusPath(), GattManager1.class);
        advertisingManager = connector.requireObject(adapter.getDbusPath(), LEAdvertisingManager1.class);
    }

    public BusConnector getConnector() {
        return connector;
    }

    public void setAdvertiseName(String advertiseName) {
        this.advertiseName = advertiseName;
    }

    public void setAdvertiseType(AdvertiseType advertiseType) {
        this.advertiseType = advertiseType;
    }

    public AdvertiseType getAdvertiseType() {
        return advertiseType;
    }

    public String getAdvertiseName() {
        return advertiseName;
    }

    public List<BleService> getServices() {
        return services;
    }

    public Set<String> getConnectedDeviceNodes() {
        return DbusHelper.findNodes(connector.getConnection(), adapter.getDbusPath());
    }

    public void disconnectAllDevices() {
        Set<String> deviceNodes = getConnectedDeviceNodes();
        String adapterPath = adapter.getDbusPath();
        for (String deviceNode : deviceNodes) {
            String devicePath = adapterPath + "/" + deviceNode;
            disconnectDevice(devicePath);
        }
    }

    public boolean disconnectDevice(String path) {
        try {
            adapter.removeDeviceByPath(path);
        } catch (BluezInvalidArgumentsException | BluezFailedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void destroy() {
        stopAdvertise();
        connector.destroy();
    }

    public void addService(BleService service) {
        services.add(service);
    }


    public boolean isAdvertising() {
        return advertisement != null;
    }

    public void startAdvertise() throws AdvertiseException {
        if (isAdvertising()) {
            System.out.println("already start");
            return;
        }

        if (services.isEmpty()) {
            throw new AdvertiseException("Services is empty");
        }

        initSignalHandler();

        advertisement = new BleAdvertisement(objectPath + "/advertisement", services, advertiseType, advertiseName);

        try {
            exportAll();
        } catch (DBusException e) {
            throw new AdvertiseException("Export objects fail", e);
        }
        try {
            gattManager.RegisterApplication(new DBusPath(objectPath), new HashMap<>());
        } catch (BluezInvalidArgumentsException | BluezAlreadyExistsException e) {
            throw new AdvertiseException("Add services fail", e);
        }

        try {
            advertisingManager.RegisterAdvertisement(new DBusPath(advertisement.getObjectPath()), new HashMap<>());
        } catch (BluezInvalidArgumentsException | BluezAlreadyExistsException | BluezInvalidLengthException
                | BluezNotPermittedException e) {
            throw new AdvertiseException("Register advertise fail", e);

        }
    }


    private void initSignalHandler() {

        addedHandler = added -> {
            if (connectionListener == null) {
                return;
            }
            Map<String, Variant<?>> device = added.getInterfaces().get(BleConstants.TYPE_DEVICE);
            if (device != null) {
                Device1 object = connector.requireObject(added.getSignalSource(), Device1.class);
                connectionListener.onDeviceDiscovered(object, device);
            }
        };

        removedHandler = removed -> {
            if (connectionListener == null) {
                return;
            }

            for (String inters : removed.getInterfaces()) {
                if (inters.equals(BleConstants.TYPE_DEVICE)) {
                    Device1 object = connector.requireObject(removed.getSignalSource(), Device1.class);
                    connectionListener.onDeviceRemoved(object);
                }
            }
        };

        objectManager = connector.requireObject(BleConstants.PATH_OBJ_MANAGER, ObjectManager.class);
        try {
            connector.getConnection().addSigHandler(ObjectManager.InterfacesAdded.class, objectManager, addedHandler);
            connector.getConnection().addSigHandler(ObjectManager.InterfacesRemoved.class, objectManager, removedHandler);
        } catch (DBusException e) {
            throw new RuntimeException("Set connection listener fail", e);
        }
    }

    private void removeSignalHandler() throws DBusException {
        connector.getConnection().removeSigHandler(ObjectManager.InterfacesAdded.class,objectManager, addedHandler);
        connector.getConnection().removeSigHandler(ObjectManager.InterfacesRemoved.class,objectManager, removedHandler);
    }

    public void stopAdvertise() {
        if (advertisement == null) {
            return;
        }
        disconnectAllDevices();

        try {
            advertisingManager.UnregisterAdvertisement(new DBusPath(advertisement.getObjectPath()));
        } catch (BluezInvalidArgumentsException | BluezDoesNotExistException e) {
            throw new AdvertiseException("Unregister advertisement fail", e);
        }
        try {
            gattManager.UnregisterApplication(new DBusPath(objectPath));
        } catch (BluezInvalidArgumentsException | BluezDoesNotExistException e) {
            throw new AdvertiseException("Unregister application fail", e);
        }
        unexportAll();

        try {
            removeSignalHandler();
        } catch (DBusException e) {
            throw new AdvertiseException("Remove signal handler fail", e);
        }


        advertisement = null;
    }


    private void exportAll() throws DBusException {
        connector.exportObject(this);
        connector.exportObject(advertisement);

        for (BleService service : services) {
            connector.exportObject(service);
            for (BleCharacteristic characteristic : service.getCharacteristics()) {
                connector.exportObject(characteristic);
                for (BleDescriptor descriptor : characteristic.getDescriptors()) {
                    connector.exportObject(descriptor);
                }
            }
        }

    }

    private void unexportAll() {

        for (BleService service : services) {
            for (BleCharacteristic characteristic : service.getCharacteristics()) {
                for (BleDescriptor descriptor : characteristic.getDescriptors()) {
                    connector.unexportObject(descriptor);
                }
                connector.unexportObject(characteristic);

            }
            connector.unexportObject(service);
        }
        connector.unexportObject(advertisement);
        connector.unexportObject(this);
    }


    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void setDiscoveryFilters(BleDiscoveryFilter... bleDiscoveryFilters) {
        try {
            adapter.setDiscoveryFilter(BleDiscoveryFilter.makeDiscoveryFilterMap(bleDiscoveryFilters));
        } catch (BluezNotReadyException | BluezNotSupportedException | BluezFailedException | BluezInvalidArgumentsException e) {
            throw new RuntimeException("Set discovery filter fail", e);
        }


    }


    private void clearDiscoveryFilter() {
        try {
            adapter.setDiscoveryFilter(new HashMap<>());
        } catch (BluezNotReadyException | BluezNotSupportedException | BluezFailedException | BluezInvalidArgumentsException e) {
            e.printStackTrace();
        }
    }


    public List<BleDevice> getConnectedDevices() {
        List<BleDevice> devices = new ArrayList<>();


        Set<String> deviceNodes = getConnectedDeviceNodes();
        String adapterPath = adapter.getDbusPath();
        for (String deviceNode : deviceNodes) {
            String devicePath = adapterPath + "/" + deviceNode;
            Device1 dev = connector.requireObject(devicePath, Device1.class);
            devices.add(new BleDevice(dev, connector));
        }

        return devices;
    }


    @Override
    public Map<DBusPath, Map<String, Map<String, Variant<?>>>> GetManagedObjects() {
        Map<DBusPath, Map<String, Map<String, Variant<?>>>> managedObjects = new HashMap<>();

        for (BleService service : services) {
            managedObjects.put(new DBusPath(service.getObjectPath()), service.getProperties());
            for (BleCharacteristic characteristic : service.getCharacteristics()) {
                managedObjects.put(new DBusPath(characteristic.getObjectPath()), characteristic.getProperties());
                for (BleDescriptor descriptor : characteristic.getDescriptors()) {
                    managedObjects.put(new DBusPath(descriptor.getObjectPath()), descriptor.getProperties());
                }
            }
        }

        return managedObjects;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getObjectPath() {
        return objectPath;
    }
}

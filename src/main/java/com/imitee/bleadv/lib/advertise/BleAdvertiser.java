package com.imitee.bleadv.lib.advertise;

import com.imitee.bleadv.lib.base.AdvertiseException;
import com.imitee.bleadv.lib.base.AdvertiseType;
import com.imitee.bleadv.lib.base.BleConstants;
import com.imitee.bleadv.lib.base.BleDiscoveryFilter;
import com.imitee.bleadv.lib.base.BusConnector;
import com.imitee.bleadv.lib.base.ConnectionListener;
import com.imitee.bleadv.lib.models.BleAdapter;

import org.bluez.Device1;
import org.bluez.GattManager1;
import org.bluez.LEAdvertisingManager1;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInvalidArgumentsException;
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

/**
 * @author: luo
 * @create: 2020-05-08 16:56
 **/
public class BleAdvertiser implements ObjectManager {
    //服务列表
    private final List<BleService> services;

    private final BleAdapter adapter;
    private final BusConnector connector;
    private final String objectPath;

    private BleAdvertisement advertisement;
    private AdvertiseType advertiseType = AdvertiseType.BROADCAST;
    private String advertiseName = "";
    private ConnectionListener connectionListener;


    private DBusSigHandler<InterfacesAdded> addedHandler;
    private DBusSigHandler<InterfacesRemoved> removedHandler;
    private ObjectManager objectManager;

    public BleAdvertiser(BleAdapter adapter, String path) {

        this.adapter = adapter;
        this.connector = BusConnector.getInstance();

        this.objectPath = path;
        this.services = new ArrayList<>();


    }

    public BleAdapter getAdapter() {
        return adapter;
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



    public void disconnectAllDevices() {
        connector.findDevices().forEach(path -> {
                    try {
                        adapter.removeDevice(new DBusPath(path));
                    } catch (BluezFailedException | BluezInvalidArgumentsException e) {
                        e.printStackTrace();
                    }
                });

    }

    public boolean disconnectDevice(String path) {
        try {
            adapter.removeDevice(new DBusPath(path));
        } catch (BluezInvalidArgumentsException | BluezFailedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isAdvertising() {
        return advertisement != null;
    }

    @SuppressWarnings("all")
    public void startAdvertise() throws AdvertiseException {
        if (isAdvertising()) {
            System.out.println("already start");
            return;
        }

        checkValid();

        initSignalHandler();

        BleService primaryService = services.stream()
                .filter(BleService::isPrimary)
                .findFirst()
                .get();
        //checked in checkValid() , primaryService must not be null
        List<BleService> primaryServiceList=new ArrayList<>();
        primaryServiceList.add(primaryService);

        advertisement = new BleAdvertisement(objectPath + "/advertisement", primaryServiceList, advertiseType, advertiseName);

        try {
            exportAll();
        } catch (DBusException e) {
            throw new AdvertiseException("Export objects fail", e);
        }
        try {
            GattManager1 gattManager = connector.requireObject(adapter.getObjectPath(), GattManager1.class);
            gattManager.RegisterApplication(new DBusPath(objectPath), new HashMap<>());
        } catch (DBusException e) {
            throw new AdvertiseException("Register application fail", e);
        }

        try {
            LEAdvertisingManager1 advertisingManager = connector.requireObject(adapter.getObjectPath(), LEAdvertisingManager1.class);
            advertisingManager.RegisterAdvertisement(new DBusPath(advertisement.getObjectPath()), new HashMap<>());
        } catch (DBusException e) {
            throw new AdvertiseException("Register advertise fail", e);
        }
    }

    private void checkValid() {
        if (services.isEmpty()) {
            throw new AdvertiseException("Services can not be empty!");
        }
        boolean hasPrimary = services.stream().anyMatch(BleService::isPrimary);

        if (!hasPrimary){
            throw new AdvertiseException("Primary service not found!");
        }

        /*for (BleService service : services) {
            List<BleCharacteristic> characteristics = service.getCharacteristics();
            for (BleCharacteristic characteristic : characteristics) {
                if (characteristic.getFlags().length == 0) {
                    throw new AdvertiseException("Characteristic(uuid=" + characteristic.getUUID() + ") must contains at least one flags ");
                }
            }

        }*/
    }

    private void initSignalHandler() {

        addedHandler = added -> {
            if (connectionListener == null) {
                return;
            }
            Map<String, Variant<?>> device = added.getInterfaces().get(BleConstants.TYPE_DEVICE);
            if (device != null) {
                Device1 object = connector.getObject(added.getSignalSource(), Device1.class);
                connectionListener.onDeviceConnected(object, device);
            }
        };

        removedHandler = removed -> {
            if (connectionListener == null) {
                return;
            }

            for (String inters : removed.getInterfaces()) {
                if (inters.equals(BleConstants.TYPE_DEVICE)) {
                    Device1 object = connector.getObject(removed.getSignalSource(), Device1.class);
                    connectionListener.onDeviceDisconnected(object);
                }
            }
        };

        try {
            objectManager = connector.requireObjectManager();
            connector.getConnection().addSigHandler(ObjectManager.InterfacesAdded.class, objectManager, addedHandler);
            connector.getConnection().addSigHandler(ObjectManager.InterfacesRemoved.class, objectManager, removedHandler);
        } catch (DBusException e) {
            throw new RuntimeException("Set connection listener fail", e);
        }
    }

    private void removeSignalHandler() throws DBusException {
        connector.getConnection().removeSigHandler(ObjectManager.InterfacesAdded.class, objectManager, addedHandler);
        connector.getConnection().removeSigHandler(ObjectManager.InterfacesRemoved.class, objectManager, removedHandler);
    }

    public void stopAdvertise() {
        if (advertisement == null) {
            return;
        }
        disconnectAllDevices();


        try {
            LEAdvertisingManager1 advertisingManager = connector.requireObject(adapter.getObjectPath(), LEAdvertisingManager1.class);
            advertisingManager.UnregisterAdvertisement(new DBusPath(advertisement.getObjectPath()));
        } catch (DBusException e) {
            throw new AdvertiseException("Unregister advertisement fail", e);
        }
        try {
            GattManager1 gattManager = connector.requireObject(adapter.getObjectPath(), GattManager1.class);
            gattManager.UnregisterApplication(new DBusPath(objectPath));
        } catch (DBusException e) {
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
        } catch (BluezNotReadyException | BluezNotSupportedException | BluezFailedException e) {
            throw new RuntimeException("Set discovery filter fail", e);
        }


    }


    private void clearDiscoveryFilter() {
        try {
            adapter.setDiscoveryFilter(new HashMap<>());
        } catch (BluezNotReadyException | BluezNotSupportedException | BluezFailedException e) {
            e.printStackTrace();
        }
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


    @Override
    public String toString() {
        return "\n BleAdvertiser{" + "\n" +
                " services=" + services + "\n" +
                " objectPath='" + objectPath + '\'' + "\n" +
                " advertiseType=" + advertiseType + "\n" +
                " advertiseName='" + advertiseName + '\'' + "\n" +
                '}';
    }
}

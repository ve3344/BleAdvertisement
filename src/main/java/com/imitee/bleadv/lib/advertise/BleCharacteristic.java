package com.imitee.bleadv.lib.advertise;


import com.imitee.bleadv.lib.base.BleConstants;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.base.BusConnector;
import com.imitee.bleadv.lib.handlers.NotifyHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

import org.bluez.GattCharacteristic1;
import org.bluez.datatypes.TwoTuple;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInProgressException;
import org.bluez.exceptions.BluezInvalidOffsetException;
import org.bluez.exceptions.BluezInvalidValueLengthException;
import org.bluez.exceptions.BluezNotAuthorizedException;
import org.bluez.exceptions.BluezNotPermittedException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.UInt16;
import org.freedesktop.dbus.types.Variant;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Local BLE characteristic
 */
public class BleCharacteristic implements GattCharacteristic1 {
    public static final String THIS_INTERFACE = BleConstants.TYPE_GATT_CHARACTERISTIC;

    private final String objectPath;
    private final String uuid;
    private final String[] flags;

    private Map<String, Variant<?>> properties;
    private BleService parentService;
    private boolean notifying;
    private byte[] value;

    private List<BleDescriptor> descriptors;

    private ReadDataHandler readDataHandler;
    private WriteDataHandler writeDataHandler;
    private NotifyHandler notifyHandler;

    public BleCharacteristic(BleService parent, String path, String uuid, int flagsInt) {
        objectPath = path;
        this.parentService = parent;
        this.value = new byte[0];
        this.uuid = uuid;
        this.notifying = true;
        this.flags = CharacteristicFlag.makeArray(flagsInt);

        properties = new HashMap<>();
        properties.put("UUID", new Variant<>(uuid));
        properties.put("Service", new Variant<>(new DBusPath(parentService.getObjectPath())));
        properties.put("Value", new Variant<>(value));
        properties.put("Flags", new Variant<>(flags));

        if (Arrays.asList(flags).contains("notify")) {
            properties.put("Notifying", new Variant<>(true));
        }

        descriptors = new ArrayList<>();
    }

    public void setNotifyHandler(NotifyHandler notifyHandler) {
        this.notifyHandler = notifyHandler;
    }
    public void setReadDataHandler(ReadDataHandler msgListener) {
        readDataHandler = msgListener;
    }

    public void setWriteDataHandler(WriteDataHandler writeDataHandler) {
        this.writeDataHandler = writeDataHandler;
    }

    public Map<String, Map<String, Variant<?>>> getProperties() {
        Map<String, Map<String, Variant<?>>> outMap = new HashMap<>();
        outMap.put(THIS_INTERFACE, properties);

        return outMap;
    }

    public void sendNotify(BusConnector db, byte[] data) {
        try {
            Map<String, Variant<?>> val = new HashMap<>();
            val.put("Value", new Variant<>(data));

            Properties.PropertiesChanged signal = new Properties.PropertiesChanged(objectPath, THIS_INTERFACE, val, new ArrayList<String>());

            db.getConnection().sendMessage(signal);
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }


    private void setValue(byte[] val) {
        this.value = val;
        properties.put("Value", new Variant<>(value));
    }

    public boolean isRemote() {
        return false;
    }

    public String getObjectPath() {
        return objectPath;
    }

    public List<BleDescriptor> getDescriptors() {
        return descriptors;
    }


    public String getUUID() {
        return uuid;
    }

    public Map<String, Variant<?>> GetAll(String interface_name) {
        return properties;
    }

    public boolean isNotifying() {
        return notifying;
    }

    @Override
    public void StartNotify() throws BluezFailedException, BluezNotPermittedException,
            BluezInProgressException, BluezNotSupportedException {
        changeNotify(true);
    }


    @Override
    public void StopNotify() throws BluezFailedException {
        changeNotify(true);
    }

    private void changeNotify(boolean notify) {
        if (notifying == notify) {
            return;
        }
        notifying = notify;
        if (notifyHandler != null) {
            notifyHandler.onNotifyChange(notifying);
        }
    }

    @Override
    public byte[] ReadValue(Map<String, Variant<?>> options)
            throws BluezFailedException, BluezInProgressException, BluezNotPermittedException,
            BluezNotAuthorizedException, BluezInvalidOffsetException, BluezNotSupportedException {

        if (readDataHandler == null) {
            return new byte[0];
        }
        value = readDataHandler.onRead(this, options);
        setValue(value);
        return value;
    }

    @Override
    public void WriteValue(byte[] value, Map<String, Variant<?>> options)
            throws BluezFailedException, BluezInProgressException, BluezNotPermittedException,
            BluezInvalidValueLengthException, BluezNotAuthorizedException, BluezNotSupportedException {

        if (writeDataHandler == null) {
            return;
        }
        writeDataHandler.onWrite(this, value,options);
    }

    // TODO: might need to implement some of these?
    // If not, don't make them part of the interface when we add our own interface implementations
    public TwoTuple<FileDescriptor, UInt16> AcquireWrite(Map<String, Variant<?>> _options)
            throws BluezFailedException, BluezNotSupportedException {
        System.out.println("AcquireWrite called!");
        return null;
    }

    public TwoTuple<FileDescriptor, UInt16> AcquireNotify(Map<String, Variant<?>> _options)
            throws BluezFailedException, BluezNotSupportedException {
        System.out.println("AcquireNotify called!");
        return null;
    }

    public <A> A Get(String interface_name, String property_name) {
        System.out.println("Characteristic->Get() called!");
        return null;
    }

    public <A> void Set(String interface_name, String property_name, A value) {
        System.out.println("Characteristic->Set() called!");
    }

    // Server only -  message from client confirming that a value was received
    public void Confirm() throws BluezFailedException {
        System.out.println("Characteristic->confirm received!");
    }
}
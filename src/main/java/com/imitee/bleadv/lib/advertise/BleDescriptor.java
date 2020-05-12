package com.imitee.bleadv.lib.advertise;

import com.imitee.bleadv.lib.base.BleConstants;
import com.imitee.bleadv.lib.base.BleFlags;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

import org.bluez.GattDescriptor1;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInProgressException;
import org.bluez.exceptions.BluezInvalidValueLengthException;
import org.bluez.exceptions.BluezNotAuthorizedException;
import org.bluez.exceptions.BluezNotPermittedException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.Variant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BleDescriptor implements GattDescriptor1 {
    private static final String THIS_INTERFACE = BleConstants.TYPE_GATT_DESCRIPTOR;

    private final String objectPath;
    private final String uuid;

    private final String[] flags;

    private Map<String, Variant<?>> properties;

    private ReadDataHandler readDataHandler;
    private WriteDataHandler writeDataHandler;

    private BleCharacteristic characteristic;

    public BleDescriptor(BleCharacteristic characteristic, String path, String uuid, int flagsInt) {
        this.objectPath = path;
        this.uuid = uuid;
        this.flags = BleFlags.makeArray(flagsInt);

        this.characteristic = characteristic;


    }

    public void setData(byte[] data) {
        properties.put("Value", new Variant<>(data));
    }

    public Map<String, Map<String, Variant<?>>> getProperties() {
        properties = new HashMap<>();

        properties.put("UUID", new Variant<>(uuid));
        properties.put("Characteristic", new Variant<>(new DBusPath(characteristic.getObjectPath())));
        properties.put("Flags", new Variant<>(flags));
        Map<String, Map<String, Variant<?>>> outMap = new HashMap<>();
        outMap.put(THIS_INTERFACE, properties);
        return outMap;
    }


    public void setReadDataHandler(ReadDataHandler msgListener) {
        readDataHandler = msgListener;
    }

    public void setWriteDataHandler(WriteDataHandler writeDataHandler) {
        this.writeDataHandler = writeDataHandler;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getObjectPath() {
        return objectPath;
    }

    public Map<String, Variant<?>> GetAll(String interface_name) {
        return properties;
    }

    @Override
    public byte[] ReadValue(Map<String, Variant<?>> options) throws BluezFailedException, BluezInProgressException,
            BluezNotPermittedException, BluezNotAuthorizedException, BluezNotSupportedException {
        if (readDataHandler == null) {
            return new byte[0];
        }
        byte[] value = readDataHandler.onRead(this, options);
        setData(value);
        return value;
    }

    @Override
    public void WriteValue(byte[] value, Map<String, Variant<?>> options)
            throws BluezFailedException, BluezInProgressException, BluezNotPermittedException,
            BluezInvalidValueLengthException, BluezNotAuthorizedException, BluezNotSupportedException {
        if (writeDataHandler == null) {
            return;
        }
        writeDataHandler.onWrite(this, value, options);
    }

    public <A> A Get(String interface_name, String property_name) {
        System.out.println("Get");
        return null;
    }

    public <A> void Set(String interface_name, String property_name, A value) {
        System.out.println("Set");
    }

    @Override
    public String toString() {
        return "\n    BleDescriptor{" + "\n" +
                "    objectPath='" + objectPath + '\'' + "\n" +
                "    uuid='" + uuid + '\'' + "\n" +
                "    flags=" + Arrays.toString(flags) + "\n" +
                '}';
    }
}

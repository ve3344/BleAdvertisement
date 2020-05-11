package com.imitee.bleadv.lib.advertise;

import com.imitee.bleadv.lib.base.BleConstants;
import com.imitee.bleadv.lib.base.CharacteristicFlag;

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
    private byte[] data;

    private Map<String, Variant<?>> properties;

    public BleDescriptor(BleCharacteristic parent, String path, String uuid, int flagsInt) {
        this.objectPath = path;
        this.uuid = uuid;
        this.flags = CharacteristicFlag.makeArray(flagsInt);

        data = new byte[0];

        properties = new HashMap<>();

        properties.put("UUID", new Variant<>(uuid));
        properties.put("Characteristic", new Variant<>(new DBusPath(parent.getObjectPath())));
        properties.put("Value", new Variant<>(data));
        properties.put("Flags", new Variant<>(flags));
    }

    public void setData(byte[] data) {
        this.data = data;
        properties.put("Value", new Variant<>(data));
    }

    public Map<String, Map<String, Variant<?>>> getProperties() {
        Map<String, Map<String, Variant<?>>> outMap = new HashMap<>();
        outMap.put(THIS_INTERFACE, properties);
        return outMap;
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
    public byte[] ReadValue(Map<String, Variant<?>> _flags) throws BluezFailedException, BluezInProgressException,
            BluezNotPermittedException, BluezNotAuthorizedException, BluezNotSupportedException {
        return null;
    }

    @Override
    public void WriteValue(byte[] _value, Map<String, Variant<?>> _flags)
            throws BluezFailedException, BluezInProgressException, BluezNotPermittedException,
            BluezInvalidValueLengthException, BluezNotAuthorizedException, BluezNotSupportedException {
    }

    public <A> A Get(String interface_name, String property_name) {
        return null;
    }

    public <A> void Set(String interface_name, String property_name, A value) {
    }

    @Override
    public String toString() {
        return "\n    BleDescriptor{" +"\n"+
                "    objectPath='" + objectPath + '\'' +"\n"+
                "    uuid='" + uuid + '\'' +"\n"+
                "    flags=" + Arrays.toString(flags) +"\n"+
                '}';
    }
}

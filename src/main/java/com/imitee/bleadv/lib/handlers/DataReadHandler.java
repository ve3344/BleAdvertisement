package com.imitee.bleadv.lib.handlers;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.utils.OptionUtils;

import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.types.Variant;

import java.util.Arrays;
import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-09 09:52
 **/
public class DataReadHandler implements ReadDataHandler {
    public final static byte[] DATA_DEFAULT = new byte[1];
    private byte[] data = DATA_DEFAULT;
    private boolean dataChange = true;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (data==null){
            throw new IllegalArgumentException("data can not be null");
        }
        if (data.length==0){
            System.out.println("Warning:data length is zero,which may cause client errors!");
        }
        this.data = data;
        this.dataChange = true;
    }


    @Override
    public byte[] onRead(DBusInterface dBusInterface, Map<String, Variant<?>> options) {
        if (dataChange) {
            dataChange = false;
            return data;
        }

        int offset = OptionUtils.getUnit16(options, "offset", 0);

        return Arrays.copyOfRange(data, offset, data.length);
    }


}

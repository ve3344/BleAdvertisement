package com.imitee.bleadv.lib.base;

import org.bluez.Adapter1;
import org.bluez.Device1;
import org.bluez.GattCharacteristic1;
import org.bluez.GattDescriptor1;
import org.bluez.GattManager1;
import org.bluez.GattService1;
import org.bluez.LEAdvertisingManager1;

/**
 * @author: luo
 * @create: 2020-05-08 17:10
 **/
public class BleConstants {
    public static final String BUS_BLUEZ = "org.bluez";

    public static final String PATH_OBJ_MANAGER = "/";

    public static final String TYPE_GATT_CHARACTERISTIC = GattCharacteristic1.class.getName();
    public static final String TYPE_GATT_SERVICE = GattService1.class.getName();
    public static final String TYPE_GATT_DESCRIPTOR = GattDescriptor1.class.getName();


}

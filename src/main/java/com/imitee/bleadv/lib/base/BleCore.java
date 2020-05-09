package com.imitee.bleadv.lib.base;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;

import org.bluez.Adapter1;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.ObjectManager;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-08 17:15
 **/
public class BleCore {

    private static BluetoothAdapter adapter;
    private static DBusConnection busConnection;


    public static BluetoothAdapter requireAdapter() {
        if (adapter != null) {
            return adapter;
        }

        DBusConnection busConnection = requireSystemDBusConnection();
        String adapterPath = findAdapterPath(busConnection);
        if (adapterPath == null) {
            adapterPath = BleConstants.PATH_BLE_ADAPTER;
            System.out.println("Warning:can not find adapter path,use default (" + adapterPath + ")");
        }

        try {

            Adapter1 device1 = busConnection.getRemoteObject(BleConstants.BUS_BLUEZ, adapterPath, Adapter1.class);
            return adapter = new BluetoothAdapter(device1, adapterPath, busConnection);

        } catch (DBusException e) {
            throw new RuntimeException("Get adapter fail", e);

        }

    }

    private static DBusConnection requireSystemDBusConnection() {
        if (busConnection != null && busConnection.isConnected()) {
            return busConnection;
        }
        try {
            return busConnection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS);
        } catch (DBusException e) {
            throw new RuntimeException("Get dbus connection fail", e);
        }
    }

    public static void destroy() {
        busConnection.disconnect();
    }

    private static String findAdapterPath(DBusConnection busConnection) {
        ObjectManager objectManager = null;
        try {
            objectManager = busConnection.getRemoteObject(BleConstants.BUS_BLUEZ, BleConstants.PATH_OBJ_MANAGER, ObjectManager.class);
        } catch (DBusException e) {
            e.printStackTrace();
            return null;
        }
        if (objectManager == null) {
            return null;
        }
        Map<DBusPath, Map<String, Map<String, Variant<?>>>> pathMapMap = objectManager.GetManagedObjects();
        if (pathMapMap == null) {
            return null;
        }
        for (Map.Entry<DBusPath, Map<String, Map<String, Variant<?>>>> entry : pathMapMap.entrySet()) {
            DBusPath busPath = entry.getKey();
            Map<String, Map<String, Variant<?>>> value = entry.getValue();

            if (value.containsKey(BleConstants.TYPE_GATT_MANAGER) && value.containsKey(BleConstants.TYPE_LE_ADVERTISING_MANAGER)) {
                return busPath.getPath();
            }
        }

        return null;
    }
}

package com.imitee.bleadv.lib.base;


import org.bluez.Adapter1;
import org.bluez.Device1;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.ObjectManager;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: luo
 * @create: 2020-05-08 17:55
 **/
public class BusConnector {
    private static BusConnector instance = null;

    public static BusConnector getInstance() {
        if (instance == null) {
            throw new RuntimeException("Bus Connector have not been initialized,Call BusConnector.init() first!");
        }
        return instance;
    }

    public static void init() throws DBusException {
        DBusConnection connection = DBusConnection.getConnection(DBusConnection.DEFAULT_SYSTEM_BUS_ADDRESS);
        instance = new BusConnector(connection, BleConstants.BUS_BLUEZ);
    }

    public static void disconnect() {
        instance.connection.disconnect();
        instance = null;
    }

    private final DBusConnection connection;
    private final String busName;

    private BusConnector(DBusConnection connection, String busName) {
        this.connection = connection;
        this.busName = busName;
    }

    public DBusConnection getConnection() {
        return connection;
    }


    public <T extends DBusInterface> T requireObject(String path, Class<T> tClass) throws DBusException {
        return connection.getRemoteObject(busName, path, tClass);
    }

    public <T extends DBusInterface> T requireObject(DBusPath path, Class<T> tClass) throws DBusException {
        return requireObject(path.getPath(), tClass);
    }


    public <T extends DBusInterface> T getObject(String path, Class<T> tClass)  {
        try {
            return connection.getRemoteObject(busName, path, tClass);
        } catch (DBusException e) {
            return null;
        }
    }

    public <T extends DBusInterface> T getObject(DBusPath path, Class<T> tClass)  {
        return getObject(path.getPath(), tClass);
    }



    public void exportObject(DBusInterface obj) throws DBusException {
        connection.exportObject(obj.getObjectPath(), obj);
    }

    public void unexportObject(DBusInterface obj) {
        if (obj != null) {
            connection.unExportObject(obj.getObjectPath());
        }

    }


    @SuppressWarnings("unchecked")
    public <T> T requireProperty(String objPath, String interfaceName, String field) throws DBusException {
        Properties properties = requireObject(objPath, Properties.class);
        Object obj = properties.Get(interfaceName, field);
        return (T) obj;
    }

    public void setProperty(String objPath, String interfaceName, String field, Object value) throws DBusException {
        Properties props = requireObject(objPath, Properties.class);
        props.Set(interfaceName, field, value);

    }

    public ObjectManager requireObjectManager() throws DBusException {
        return requireObject(BleConstants.PATH_OBJ_MANAGER, ObjectManager.class);
    }
    public Map<DBusPath, Map<String, Map<String, Variant<?>>>> getManagedObjects() {
        try {
            ObjectManager objectManager = requireObjectManager();
            return objectManager.GetManagedObjects();
        } catch (DBusException e) {
            return new HashMap<>();
        }
    }



    public List<String> findAdapters() {
        return getManagedObjects()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().containsKey(Adapter1.class.getName()))
                .map(entry -> entry.getKey().getPath())
                .collect(Collectors.toList());
    }

    public List<String> findDevices() {
        return getManagedObjects()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().containsKey(Device1.class.getName()))
                .map(entry -> entry.getKey().getPath())
                .collect(Collectors.toList());
    }

}

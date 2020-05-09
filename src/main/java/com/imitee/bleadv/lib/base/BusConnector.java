package com.imitee.bleadv.lib.base;


import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Properties;

import java.io.IOException;

/**
 * @author: luo
 * @create: 2020-05-08 17:55
 **/
public class BusConnector {
    private final DBusConnection connection;
    private final String busName;

    public BusConnector(DBusConnection connection, String busName) {
        this.connection = connection;
        this.busName = busName;
    }

    public BusConnector(DBusConnection connection) {
        this.connection = connection;
        this.busName = BleConstants.BUS_BLUEZ;
    }


    public <T extends DBusInterface> T requireObject(String path, Class<T> tClass) {
        try {
            return connection.getRemoteObject(busName, path, tClass);
        } catch (DBusException e) {
            throw new RuntimeException("Get remote object fail", e);
        }
    }

    public <T extends DBusInterface> T requireObject(DBusPath path, Class<T> tClass) {
        return requireObject(path.getPath(), tClass);
    }


    public void exportObject(DBusInterface obj) throws DBusException {
            connection.exportObject(obj.getObjectPath(), obj);
    }
    public void unexportObject(DBusInterface obj) {
        if (obj != null) {
            connection.unExportObject(obj.getObjectPath());
        }

    }

    public DBusConnection getConnection() {
        return connection;
    }

    public void destroy() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @SuppressWarnings("unchecked")
    public <T> T getProperty(String objPath, String interfaceName, String field) {
        Properties properties = requireObject(objPath, Properties.class);
        Object obj = properties.Get(interfaceName, field);
        return (T) obj;
    }

    public void setProperty(String objPath, String interfaceName, String field, Object value) {
        Properties props = requireObject(objPath, Properties.class);
        props.Set(interfaceName, field, value);

    }



}

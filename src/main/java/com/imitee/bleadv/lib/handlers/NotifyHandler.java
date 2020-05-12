package com.imitee.bleadv.lib.handlers;

import org.freedesktop.dbus.interfaces.DBusInterface;

/**
 * @author: luo
 * @create: 2020-05-08 21:54
 **/
public interface NotifyHandler {
    void onNotifyChange(DBusInterface dBusInterface, boolean notify);
}

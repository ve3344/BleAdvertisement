package com.imitee.bleadv.lib.models;

import com.imitee.bleadv.dbus.annotation.DbusObject;
import com.imitee.bleadv.dbus.annotation.ObjectOperation;
import com.imitee.bleadv.dbus.annotation.PropertyOperation;

import org.bluez.Adapter1;
import org.bluez.exceptions.BluezAlreadyExistsException;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInvalidArgumentsException;
import org.bluez.exceptions.BluezNotAuthorizedException;
import org.bluez.exceptions.BluezNotReadyException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.Variant;

import java.util.List;
import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-10 10:41
 **/

@DbusObject(value = Adapter1.class)
public interface BleAdapter {
    @PropertyOperation()
    boolean isDiscoverable();

    @PropertyOperation()
    void setDiscoverable(boolean discoverable);

    @PropertyOperation()
    int getDiscoverableTimeout();

    @PropertyOperation()
    void setDiscoverableTimeout(int discoverableTimeout);

    @PropertyOperation()
    String getAddress();

    @PropertyOperation(name = "UUIDs")
    List<String> getUUIDs();

    @PropertyOperation()
    String getName();


    @PropertyOperation()
    boolean isPairable();

    @PropertyOperation()
    void setPairable(boolean pairable);

    @PropertyOperation()
    String getModalias();

    @PropertyOperation()
    String getAlias();

    @PropertyOperation()
    void setAlias(String alias);

    @PropertyOperation(name = "Class")
    int getClassX();

    @PropertyOperation()
    int getPairableTimeout();

    @PropertyOperation()
    void setPairableTimeout(int pairableTimeout);

    @PropertyOperation()
    String getAddressType();

    @PropertyOperation()
    boolean isDiscovering();

    @PropertyOperation()
    boolean isPowered();

    @PropertyOperation()
    void setPowered(boolean powered);

    @ObjectOperation(name = "StartDiscovery")
    void startDiscovery() throws BluezNotReadyException, BluezFailedException;

    @ObjectOperation(name = "StopDiscovery")
    void stopDiscovery() throws BluezNotReadyException, BluezFailedException, BluezNotAuthorizedException;


    @ObjectOperation(name = "SetDiscoveryFilter")
    void setDiscoveryFilter(Map<String, Variant<?>> filter) throws BluezNotReadyException, BluezNotSupportedException, BluezFailedException;

    @ObjectOperation(name = "GetDiscoveryFilters")
    String[] getDiscoveryFilters();

    @ObjectOperation(name = "ConnectDevice")
    DBusPath connectDevice(Map<String, Variant<?>> properties) throws BluezInvalidArgumentsException, BluezAlreadyExistsException, BluezNotSupportedException, BluezNotReadyException, BluezFailedException;

    @ObjectOperation(name = "RemoveDevice")
    void removeDevice(DBusPath device) throws BluezInvalidArgumentsException, BluezFailedException;

    @ObjectOperation()
    String getObjectPath();

}

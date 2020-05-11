package com.imitee.bleadv.lib.models;

import com.imitee.bleadv.dbus.annotation.DbusObject;
import com.imitee.bleadv.dbus.annotation.ObjectOperation;
import com.imitee.bleadv.dbus.annotation.PropertyOperation;

import org.bluez.Device1;
import org.bluez.exceptions.BluezAlreadyConnectedException;
import org.bluez.exceptions.BluezAlreadyExistsException;
import org.bluez.exceptions.BluezAuthenticationCanceledException;
import org.bluez.exceptions.BluezAuthenticationFailedException;
import org.bluez.exceptions.BluezAuthenticationRejectedException;
import org.bluez.exceptions.BluezAuthenticationTimeoutException;
import org.bluez.exceptions.BluezConnectionAttemptFailedException;
import org.bluez.exceptions.BluezDoesNotExistException;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInProgressException;
import org.bluez.exceptions.BluezInvalidArgumentsException;
import org.bluez.exceptions.BluezNotAvailableException;
import org.bluez.exceptions.BluezNotConnectedException;
import org.bluez.exceptions.BluezNotReadyException;
import org.bluez.exceptions.BluezNotSupportedException;

import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-10 21:39
 **/
@DbusObject(Device1.class)
public interface BleDevice {

    @PropertyOperation
    boolean isLegacyPairing();

    @PropertyOperation
    String getAddress();

    @PropertyOperation
    boolean isPaired();

    @PropertyOperation
    boolean isConnected();

    @PropertyOperation
    String getAlias();

    @PropertyOperation
    void setAlias(String alias);

    @PropertyOperation
    String getAdapter();

    @PropertyOperation
    boolean isServicesResolved();

    @PropertyOperation
    String getAddressType();

    @PropertyOperation
    boolean isTrusted();

    @PropertyOperation
    void setTrusted(boolean trusted);

    @PropertyOperation
    boolean isBlocked();

    @PropertyOperation
    void setBlocked(boolean blocked);

    @PropertyOperation
    List<String> getUUIDs();

    @ObjectOperation(name = "Connect")
    void connect() throws BluezNotReadyException, BluezFailedException, BluezInProgressException, BluezAlreadyConnectedException;

    @ObjectOperation(name = "Disconnect")
    void disconnect() throws BluezNotConnectedException;

    @ObjectOperation(name = "ConnectProfile")
    void connectProfile(String uuid) throws BluezFailedException, BluezInProgressException, BluezInvalidArgumentsException, BluezNotAvailableException, BluezNotReadyException;

    @ObjectOperation(name = "DisconnectProfile")
    void disconnectProfile(String uuid) throws BluezFailedException, BluezInProgressException, BluezInvalidArgumentsException, BluezNotSupportedException;

    @ObjectOperation(name = "Pair")
    void pair() throws BluezInvalidArgumentsException, BluezFailedException, BluezAlreadyExistsException, BluezAuthenticationCanceledException, BluezAuthenticationFailedException, BluezAuthenticationRejectedException, BluezAuthenticationTimeoutException, BluezConnectionAttemptFailedException;

    @ObjectOperation(name = "CancelPairing")
    void cancelPairing() throws BluezDoesNotExistException, BluezFailedException;

}


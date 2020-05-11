package com.imitee.bleadv.lib.base;

import org.bluez.Device1;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public interface ConnectionListener {
	void onDeviceConnected(Device1 dev, Map<String, Variant<?>> options);
	void onDeviceDisconnected(Device1 dev);
}

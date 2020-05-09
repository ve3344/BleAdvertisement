package com.imitee.bleadv.lib.handlers;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;

import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public interface ReadDataHandler {
    byte[] onRead(BleCharacteristic characteristic, Map<String, Variant<?>> options);

}
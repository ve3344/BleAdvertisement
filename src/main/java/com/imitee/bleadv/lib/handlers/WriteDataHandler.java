package com.imitee.bleadv.lib.handlers;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;

import org.freedesktop.dbus.types.Variant;

import java.util.Map;

public interface WriteDataHandler {

    void onWrite(BleCharacteristic characteristic,byte[] value,Map<String, Variant<?>> options);
}
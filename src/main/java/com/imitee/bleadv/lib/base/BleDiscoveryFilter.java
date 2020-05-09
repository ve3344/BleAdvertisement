package com.imitee.bleadv.lib.base;

import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInvalidArgumentsException;
import org.bluez.exceptions.BluezNotReadyException;
import org.bluez.exceptions.BluezNotSupportedException;
import org.freedesktop.dbus.types.Variant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: luo
 * @create: 2020-05-08 18:37
 **/
public class BleDiscoveryFilter {
    private final String type;
    private final Object value;

    private BleDiscoveryFilter(String type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public static BleDiscoveryFilter uuids(String... uuids) {
        return new BleDiscoveryFilter("UUIDs", uuids);
    }

    public static BleDiscoveryFilter rssi(short rssi) {
        return new BleDiscoveryFilter("RSSI", rssi);
    }

    public static BleDiscoveryFilter pathloss(int pathloss) {
        return new BleDiscoveryFilter("Pathloss", pathloss);
    }

    public static BleDiscoveryFilter transport(String transport) {
        return new BleDiscoveryFilter("Transport", transport);
    }

    public static BleDiscoveryFilter duplicateData(boolean duplicateData) {
        return new BleDiscoveryFilter("DuplicateData", duplicateData);
    }

    public static Map<String, Variant<?>> makeDiscoveryFilterMap(BleDiscoveryFilter... bleDiscoveryFilters) {

        Map<String, Variant<?>> filterMap = new HashMap<>();
        if (bleDiscoveryFilters == null) {
            return filterMap;
        }
        for (BleDiscoveryFilter bleDiscoveryFilter : bleDiscoveryFilters) {
            filterMap.put(bleDiscoveryFilter.getType(), new Variant<>(bleDiscoveryFilter.getValue()));
        }
        return  filterMap;


    }

}

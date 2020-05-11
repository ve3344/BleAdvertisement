package com.imitee.bleadv.lib.advertise;


import com.imitee.bleadv.lib.base.AdvertiseType;

import org.bluez.LEAdvertisement1;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BleAdvertisement implements LEAdvertisement1, Properties {
    private final AdvertiseType advertiseType;
    private final String advertiseName;
    private final String objectPath;

    private Map<String, Variant<?>> properties;

    private final List<BleService> services;

    public BleAdvertisement(String objectPath, List<BleService> services, AdvertiseType advertiseType, String advertiseName) {
        this.objectPath = objectPath;
        this.advertiseType = advertiseType;
        this.advertiseName = advertiseName;

        this.services = services;
        if (services.size()>1){
            throw new IllegalArgumentException("It may cause error,shout use the first primary service");
        }

    }


    @Override
    public String getObjectPath() {
        return objectPath;
    }

    @Override
    public Map<String, Variant<?>> GetAll(String interface_name) {

        this.properties = new HashMap<>();


        properties.put("ServiceUUIDs", new Variant<>(getServiceUuids()));
        properties.put("Type", new Variant<>(this.advertiseType.getValue()));
        properties.put("LocalName", new Variant<>(this.advertiseName));
        return properties;
    }

    private String[] getServiceUuids() {
        return services.stream()
                .map(BleService::getUUID)
                .toArray(String[]::new);
    }

    @Override
    public void Release() {

    }

    @Override
    public <A> A Get(String interface_name, String property_name) {
        return null;
    }

    @Override
    public <A> void Set(String interface_name, String property_name, A value) {

    }

    @Override
    public boolean isRemote() {
        return false;
    }
}

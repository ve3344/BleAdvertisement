package com.imitee.bleadv.lib.advertise;

public enum AdvertiseType {
    BROADCAST("broadcast"),
    PERIPHERAL("peripheral")
    ;
    private String value;

    public String getValue() {
        return value;
    }

    AdvertiseType(String value) {
        this.value = value;
    }
}

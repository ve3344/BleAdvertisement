package com.imitee.bleadv.lib.base;

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

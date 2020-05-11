package com.imitee.bleadv.lib.models;

/**
 * @author: luo
 * @create: 2020-05-10 21:39
 **/
public interface BleDevice {

    boolean isLegacyPairing();

    void setLegacyPairing(boolean LegacyPairing);

    String getAddress();

    void setAddress(String Address);

    boolean isPaired();

    void setPaired(boolean Paired);

    boolean isConnected() ;

    void setConnected(boolean Connected);

    String getAlias() ;

    void setAlias(String Alias);

    String getAdapter() ;

    void setAdapter(String Adapter);

    boolean isServicesResolved();

    void setServicesResolved(boolean ServicesResolved);

    String getAddressType() ;

    void setAddressType(String AddressType) ;

    boolean isTrusted();

    void setTrusted(boolean Trusted) ;

    boolean isBlocked() ;

    void setBlocked(boolean Blocked);

    String getUUIDs() ;

    void setUUIDs(String UUIDs);
}

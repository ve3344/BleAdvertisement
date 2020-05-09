package com.imitee.bleadv.lib.builder;

import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.imitee.bleadv.lib.advertise.AdvertiseType;
import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleService;
import com.imitee.bleadv.lib.base.ConnectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: luo
 * @create: 2020-05-09 12:09
 **/
public class AdvertiseBuilder {
    private final BleAdvertiser bleAdvertiser;
    private final String advertiserPath;
    private final List<ServiceBuilder> serviceBuilders;


    public AdvertiseBuilder( String advertiserPath) {
        this.advertiserPath = advertiserPath;
        this.bleAdvertiser = new BleAdvertiser( this.advertiserPath);
        this.serviceBuilders=new ArrayList<>();

    }

    public AdvertiseBuilder setAdvertiseType(AdvertiseType advertiseType) {
        bleAdvertiser.setAdvertiseType(advertiseType);
        return this;
    }

    public AdvertiseBuilder setConnectionListener(ConnectionListener connectionListener) {
        bleAdvertiser.setConnectionListener(connectionListener);
        return this;
    }

    public AdvertiseBuilder setAdvertiseBleName(String advertiseBleName) {
        bleAdvertiser.setAdvertiseName(advertiseBleName);
        return this;
    }


    public AdvertiseBuilder addService(ServiceBuilder serviceBuilder) {
        serviceBuilders.add(serviceBuilder);
        return this;
    }


    BleAdvertiser getBleAdvertiser() {
        return bleAdvertiser;
    }

    String getAdvertiserPath() {
        return advertiserPath;
    }

    public BleAdvertiser build() {
        List<BleService> services = bleAdvertiser.getServices();
        int index=0;
        for (ServiceBuilder serviceBuilder : serviceBuilders) {

            String servicePath=advertiserPath + "/service_"+index;
            BleService bleService = serviceBuilder.build(bleAdvertiser,servicePath);
            services.add(bleService);
            index++;
        }
        return bleAdvertiser;
    }
}

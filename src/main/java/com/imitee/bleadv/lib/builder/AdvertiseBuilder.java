package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.base.AdvertiseType;
import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleService;
import com.imitee.bleadv.lib.base.ConnectionListener;
import com.imitee.bleadv.lib.models.BleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-09 12:09
 **/
public class AdvertiseBuilder {
    private final BleAdvertiser bleAdvertiser;
    private final String advertiserPath;
    private final List<ServiceProvider> serviceProviders;


    public AdvertiseBuilder(BleAdapter adapter, String advertiserPath) {
        this.advertiserPath = advertiserPath;
        this.serviceProviders =new ArrayList<>();
        this.bleAdvertiser = new BleAdvertiser( adapter,this.advertiserPath);

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


    public AdvertiseBuilder addService(ServiceProvider serviceProvider) {
        serviceProviders.add(serviceProvider);
        return this;
    }


    public BleAdvertiser build() {
        List<BleService> services = bleAdvertiser.getServices();
        int index=0;
        for (ServiceProvider serviceProvider : serviceProviders) {
            BleService bleService = serviceProvider.provide(bleAdvertiser,index);

            services.add(bleService);
            index++;
        }
        return bleAdvertiser;
    }
}

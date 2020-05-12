package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-09 12:30
 **/
public class ServiceBuilder implements ServiceProvider {

    private final String uuid;
    private boolean isPrimary;
    private final List<CharacteristicProvider> characteristicProviders;

    public ServiceBuilder(String uuid) {
        this.uuid = uuid;
        characteristicProviders = new ArrayList<>();
        isPrimary = false;
    }

    public ServiceBuilder addCharacteristic(CharacteristicProvider characteristicProvider) {
        characteristicProviders.add(characteristicProvider);
        return this;
    }

    public ServiceBuilder setPrimary(boolean primary) {
        isPrimary = primary;
        return this;
    }

    @Override
    public BleService provide(BleAdvertiser advertiser, int serviceIndex) {
        String servicePath = advertiser.getObjectPath() + "/service_" + serviceIndex;
        BleService bleService = new BleService(servicePath, uuid, isPrimary);

        List<BleCharacteristic> characteristics = bleService.getCharacteristics();
        int index = 0;
        for (CharacteristicProvider characteristicProvider : characteristicProviders) {

            BleCharacteristic bleCharacteristic = characteristicProvider.provide(bleService, index);
            characteristics.add(bleCharacteristic);
            index++;
        }

        return bleService;
    }


}

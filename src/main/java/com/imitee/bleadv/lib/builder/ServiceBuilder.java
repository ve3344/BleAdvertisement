package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleService;
import com.imitee.bleadv.lib.advertise.CharacteristicFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: luo
 * @create: 2020-05-09 12:30
 **/
public class ServiceBuilder {

    private final String uuid;
    private final List<CharacteristicBuilder> characteristicBuilders;

    public ServiceBuilder(String uuid) {
        this.uuid = uuid;
        characteristicBuilders=new ArrayList<>();
    }
    public void addCharacteristic(CharacteristicBuilder characteristicBuilder){
        characteristicBuilders.add(characteristicBuilder);
    }

    public BleService build(BleAdvertiser advertiser, String servicePath) {
        BleService bleService = new BleService(servicePath, uuid);
        List<BleCharacteristic> characteristics = bleService.getCharacteristics();
        int index=0;
        for (CharacteristicBuilder characteristicBuilder : characteristicBuilders) {
            String characteristicsPath=servicePath+"/characteristic_"+index;
            BleCharacteristic bleCharacteristic = characteristicBuilder.build(bleService, characteristicsPath);
            characteristics.add(bleCharacteristic);
            index++;
        }

        return bleService;
    }

}

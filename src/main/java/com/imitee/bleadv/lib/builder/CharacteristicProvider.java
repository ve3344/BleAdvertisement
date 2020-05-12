package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleService;

/**
 * @author: luo
 * @create: 2020-05-12 12:11
 **/
public interface CharacteristicProvider {

    BleCharacteristic provide(BleService bleService, int characteristicsIndex);
}

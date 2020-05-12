package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleDescriptor;

/**
 * @author: luo
 * @create: 2020-05-12 12:17
 **/
public interface DescriptorProvider {
    BleDescriptor provide(BleCharacteristic bleCharacteristic, int descriptorIndex);
}

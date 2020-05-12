package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleService;

/**
 * @author: luo
 * @create: 2020-05-12 12:12
 **/
public interface ServiceProvider {
    BleService provide(BleAdvertiser advertiser, int serviceIndex);
}

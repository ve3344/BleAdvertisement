package com.imitee.bleadv.lib.handlers;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.base.BusConnector;

/**
 * @author: luo
 * @create: 2020-05-09 09:52
 **/
public class AutoDataReadHandler extends DataReadHandler {
   private final BleCharacteristic characteristic;


    public AutoDataReadHandler(BleCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public void setDataAndNotify(byte[] data) {
        super.setData(data);
        characteristic.sendNotify( data);
    }
}

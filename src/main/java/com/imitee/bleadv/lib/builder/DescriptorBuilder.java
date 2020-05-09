package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleDescriptor;
import com.imitee.bleadv.lib.advertise.CharacteristicFlag;
import com.imitee.bleadv.lib.handlers.NotifyHandler;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

/**
 * @author: luo
 * @create: 2020-05-09 16:18
 **/
public class DescriptorBuilder {

    private final String uuid;
    private int flagsInt;

    private NotifyHandler notifyHandler;
    private ReadDataHandler readDataHandler;
    private WriteDataHandler writeDataHandler;

    public DescriptorBuilder(String uuid) {
        this.uuid = uuid;
        this.flagsInt = CharacteristicFlag.NONE;
    }

    public DescriptorBuilder setNotifyHandler(NotifyHandler notifyHandler) {
        this.notifyHandler = notifyHandler;
        flagsInt |= CharacteristicFlag.NOTIFY;
        return this;
    }

    public DescriptorBuilder setReadDataHandler(ReadDataHandler readDataHandler) {
        this.readDataHandler = readDataHandler;
        flagsInt |= CharacteristicFlag.READ;
        return this;
    }

    public DescriptorBuilder setWriteDataHandler(WriteDataHandler writeDataHandler) {
        this.writeDataHandler = writeDataHandler;
        flagsInt |= CharacteristicFlag.WRITE;
        return this;
    }
    public BleDescriptor build(BleCharacteristic bleCharacteristic, String descriptorPath) {
        return new BleDescriptor(bleCharacteristic, descriptorPath,uuid, flagsInt);
    }
}

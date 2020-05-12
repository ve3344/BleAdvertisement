package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleDescriptor;
import com.imitee.bleadv.lib.base.CharacteristicFlag;
import com.imitee.bleadv.lib.handlers.NotifyHandler;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

import java.util.Observable;

/**
 * @author: luo
 * @create: 2020-05-09 16:18
 **/
public class DescriptorBuilder implements DescriptorProvider {

    private final String uuid;
    private int flagsInt;

    private ReadDataHandler readDataHandler;
    private WriteDataHandler writeDataHandler;

    public DescriptorBuilder(String uuid) {
        this.uuid = uuid;
        this.flagsInt = CharacteristicFlag.NONE;
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

    public DescriptorBuilder overrideFlags(int flagsInt) {
        this.flagsInt = flagsInt;
        return this;
    }

    @Override
    public BleDescriptor provide(BleCharacteristic bleCharacteristic, int descriptorIndex) {
        String descriptorPath = bleCharacteristic.getObjectPath() + "/descriptor_" + descriptorIndex;
        BleDescriptor bleDescriptor = new BleDescriptor(bleCharacteristic, descriptorPath, uuid, flagsInt);
        bleDescriptor.setReadDataHandler(readDataHandler);
        bleDescriptor.setWriteDataHandler(writeDataHandler);
        return bleDescriptor;

    }


}

package com.imitee.bleadv.lib.builder;

import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleDescriptor;
import com.imitee.bleadv.lib.advertise.BleService;
import com.imitee.bleadv.lib.base.CharacteristicFlag;
import com.imitee.bleadv.lib.handlers.NotifyHandler;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: luo
 * @create: 2020-05-09 13:13
 **/
public class CharacteristicBuilder implements CharacteristicProvider {

    private final String uuid;
    private final List<DescriptorProvider> descriptorProviders;
    private int flagsInt;

    private NotifyHandler notifyHandler;
    private ReadDataHandler readDataHandler;
    private WriteDataHandler writeDataHandler;


    public CharacteristicBuilder(String uuid) {
        this.uuid = uuid;
        this.descriptorProviders = new ArrayList<>();
        this.flagsInt = CharacteristicFlag.NONE;

    }

    public CharacteristicBuilder overrideFlags(int flagsInt) {
        this.flagsInt = flagsInt;
        return this;
    }

    public CharacteristicBuilder addDescriptor(DescriptorProvider descriptorProvider) {
        descriptorProviders.add(descriptorProvider);
        return this;
    }

    public CharacteristicBuilder setNotifyHandler(NotifyHandler notifyHandler) {
        this.notifyHandler = notifyHandler;
        flagsInt |= CharacteristicFlag.NOTIFY;
        return this;
    }

    public CharacteristicBuilder setReadDataHandler(ReadDataHandler readDataHandler) {
        this.readDataHandler = readDataHandler;
        flagsInt |= CharacteristicFlag.READ;
        return this;
    }

    public CharacteristicBuilder setWriteDataHandler(WriteDataHandler writeDataHandler) {
        this.writeDataHandler = writeDataHandler;
        flagsInt |= CharacteristicFlag.WRITE;
        return this;
    }

    @Override
    public BleCharacteristic provide(BleService bleService, int characteristicsIndex) {
        String characteristicsPath = bleService.getObjectPath() + "/characteristic_" + characteristicsIndex;
        BleCharacteristic bleCharacteristic = new BleCharacteristic(bleService, characteristicsPath, uuid, flagsInt);
        bleCharacteristic.setNotifyHandler(notifyHandler);
        bleCharacteristic.setWriteDataHandler(writeDataHandler);
        bleCharacteristic.setReadDataHandler(readDataHandler);
        List<BleDescriptor> descriptors = bleCharacteristic.getDescriptors();
        int index = 0;
        for (DescriptorProvider descriptorProvider : descriptorProviders) {

            BleDescriptor bleDescriptor = descriptorProvider.provide(bleCharacteristic, index);
            descriptors.add(bleDescriptor);
            index++;
        }

        return bleCharacteristic;
    }

}

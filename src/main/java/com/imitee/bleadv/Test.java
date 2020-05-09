package com.imitee.bleadv;


import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.advertise.BleCharacteristic;
import com.imitee.bleadv.lib.advertise.BleDescriptor;
import com.imitee.bleadv.lib.advertise.BleService;
import com.imitee.bleadv.lib.advertise.CharacteristicFlag;
import com.imitee.bleadv.lib.base.BleCore;
import com.imitee.bleadv.lib.base.ConnectionListener;
import com.imitee.bleadv.lib.base.OptionUtils;
import com.imitee.bleadv.lib.handlers.ReadDataHandler;
import com.imitee.bleadv.lib.handlers.WriteDataHandler;

import org.bluez.Device1;
import org.freedesktop.Hexdump;
import org.freedesktop.dbus.types.Variant;

import java.util.Map;
import java.util.Scanner;

/**
 * @author: luo
 * @create: 2020-05-08 14:19
 **/
public class Test {
    public static void main(String[] args) {

        BleAdvertiser bleAdvertiser = new BleAdvertiser(Constants.DBUS_OBJECT_BASE);

        bleAdvertiser.disconnectAllDevices();
        bleAdvertiser.setConnectionListener(new ConnectionListener() {
            @Override
            public void onDeviceDiscovered(Device1 dev, Map<String, Variant<?>> options) {
                System.out.println("onDeviceDiscovered______");
                OptionUtils.print(options);
            }

            @Override
            public void onDeviceRemoved(Device1 dev) {
            }
        });

        BleService service = getBleService(bleAdvertiser);

        bleAdvertiser.addService(service);
        bleAdvertiser.setAdvertiseName("wrqrieuwfhabcjasdgja");


/*
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (BleCharacteristic characteristic : service.getCharacteristics()) {
                    characteristic.sendNotify(bleAdvertiser.getConnector(), new byte[]{1, 2, 3});
                }
            }
        }).start();
*/

        Scanner scanner = new Scanner(System.in);


        while (true) {
            String line = scanner.nextLine();
            if ("1".equalsIgnoreCase(line)) {
                bleAdvertiser.startAdvertise();
                System.out.println("starting");
            }
            if ("2".equalsIgnoreCase(line)) {
                bleAdvertiser.stopAdvertise();
                System.out.println("stopping");

            }
        }

    }

    public static byte[] data = new byte[1];

    static {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
    }

    private static BleService getBleService(BleAdvertiser bleAdvertiser) {
        ReadDataHandler read = (characteristic, options) -> {
            System.out.println("onRead");
            return data;
        };
        WriteDataHandler writeDataHandler = (characteristic1, value, options) -> {
            System.out.println("onWrite");
            Hexdump.print(value);

        };
        BleService service = new BleService(Constants.DBUS_OBJECT_BASE + "/service",
                Constants.SERVICE_UUID);


        BleCharacteristic bleCharacteristic = new BleCharacteristic(
                service,
                Constants.DBUS_OBJECT_BASE + "/service/c1",
                Constants.CHARACTERISTIC_MSG_UUID,
                CharacteristicFlag.ALL
        );

        bleCharacteristic.setReadDataHandler(read);
        bleCharacteristic.setWriteDataHandler(writeDataHandler);
        service.getCharacteristics().add(bleCharacteristic);

        BleCharacteristic bleCharacteristic2 = new BleCharacteristic(
                service,
                Constants.DBUS_OBJECT_BASE + "/service/c2",
                Constants.CHARACTERISTIC_TIME_UUID,
                CharacteristicFlag.ALL
        );

        bleCharacteristic2.setReadDataHandler(read);
        bleCharacteristic2.setWriteDataHandler(writeDataHandler);

        service.getCharacteristics().add(bleCharacteristic2);


        BleDescriptor bleDescriptor = new BleDescriptor(bleCharacteristic2,
                Constants.DBUS_OBJECT_BASE + "/service/c2/d1",
                Constants.TIME_DESCRIPTOR_UUID,
                CharacteristicFlag.READ | CharacteristicFlag.WRITE
        );

        bleCharacteristic2.getDescriptors().add(bleDescriptor);


        return service;
    }
}

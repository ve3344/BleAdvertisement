package com.imitee.bleadv;


import com.imitee.bleadv.dbus.test.JsonUtils;
import com.imitee.bleadv.dbus.test.Tester;
import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.base.AdvertiseType;
import com.imitee.bleadv.lib.base.BusConnector;
import com.imitee.bleadv.lib.base.ConnectionListener;
import com.imitee.bleadv.lib.builder.AdvertiseBuilder;
import com.imitee.bleadv.lib.builder.CharacteristicBuilder;
import com.imitee.bleadv.lib.builder.ServiceBuilder;
import com.imitee.bleadv.lib.models.BleAdapter;
import com.imitee.bleadv.lib.models.BleDevice;

import org.bluez.Device1;
import org.freedesktop.Hexdump;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.exceptions.DBusExecutionException;
import org.freedesktop.dbus.types.Variant;

import java.util.List;
import java.util.Map;


/**
 * @author: luo
 * @create: 2020-05-08 14:19
 **/
public class Test {
    public static final String SERVICE_UUID1 = "13333333-3333-3333-3333-33333333300a";
    public static final String SERVICE_UUID2 = "13333333-3333-3333-3333-33333333300b";
    public static final String SERVICE_UUID3 = "13333333-3333-3333-3333-33333333300c";
    public static final String CHARACTERISTIC_UUID1 = "13333333-3333-3333-3333-333333333001";
    public static final String CHARACTERISTIC_UUID2 = "13333333-3333-3333-3333-333333333002";

    public static void main(String[] args) throws DBusException {


        BusConnector.init();

        BusConnector connector = BusConnector.getInstance();

        List<String> adapters = connector.findAdapters();

        BleAdapter adapter = connector.makeModel(BleAdapter.class, adapters.get(0));

        BleAdvertiser advertiser = getBuild(connector, adapter);


        adapter.setPowered(true);
        adapter.setDiscoverable(true);
        adapter.setDiscoverableTimeout(0);
        advertiser.disconnectAllDevices();


        try {
            advertiser.startAdvertise();
        } catch (DBusExecutionException e) {
            e.printStackTrace();
        }

        loop();

    }

    private static void loop() {
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void testBleAdapter(BleAdapter bleAdapter) {
        Tester.testCallAll(bleAdapter);
        System.exit(0);
    }

    private static void testBleDevice(BleDevice bleDevice) {
        Tester.testCallAll(bleDevice);
        System.exit(0);
    }

    private static BleAdvertiser getBuild(BusConnector connector, BleAdapter adapter) {
        return new AdvertiseBuilder(adapter, "/bttest")
                .addService(new ServiceBuilder(SERVICE_UUID1)
                        .setPrimary(true)
                        .addCharacteristic(new CharacteristicBuilder(CHARACTERISTIC_UUID1)
                                .setReadDataHandler((characteristic, options) -> "hello".getBytes())
                                .setWriteDataHandler((characteristic, value, options) -> {
                                    System.out.println("Write:" + Hexdump.toHex(value));
                                })
                                .setNotifyHandler(notify -> {
                                    System.out.println("NotifyChange:" + notify);
                                })
                        )
                        .addCharacteristic(new CharacteristicBuilder(CHARACTERISTIC_UUID2)
                                .setReadDataHandler((characteristic, options) -> "aa".getBytes())
                        )

                )

                .addService(new ServiceBuilder(SERVICE_UUID2)
                        .addCharacteristic(new CharacteristicBuilder(CHARACTERISTIC_UUID2)
                                .setReadDataHandler((characteristic, options) -> "aa".getBytes())
                        )
                        .setPrimary(true)
                )
                .setConnectionListener(new ConnectionListener() {
                    @Override
                    public void onDeviceConnected(Device1 dev, Map<String, Variant<?>> options) {
                        System.out.println("onDeviceConnected");
                        BleDevice bleDevice = null;
                        try {
                            bleDevice = connector.makeModel(BleDevice.class, dev);

                            System.out.println(JsonUtils.mapToJson(connector.getManagedObjects()));

                        } catch (DBusException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onDeviceDisconnected(Device1 dev) {
                        System.out.println("onDeviceDisconnected");
                    }
                })
                .setAdvertiseType(AdvertiseType.BROADCAST)
                .setAdvertiseBleName("adddd")
                .build();
    }


}

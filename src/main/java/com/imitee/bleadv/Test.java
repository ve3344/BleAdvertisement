package com.imitee.bleadv;


import com.imitee.bleadv.dbus.adapters.DbusConvertAdapter;
import com.imitee.bleadv.dbus.core.ModelMaker;
import com.imitee.bleadv.dbus.test.Tester;
import com.imitee.bleadv.lib.models.BleAdapter;
import com.imitee.bleadv.lib.advertise.BleAdvertiser;
import com.imitee.bleadv.lib.base.AdvertiseType;
import com.imitee.bleadv.lib.base.BusConnector;
import com.imitee.bleadv.lib.base.ConnectionListener;
import com.imitee.bleadv.lib.builder.AdvertiseBuilder;
import com.imitee.bleadv.lib.builder.CharacteristicBuilder;
import com.imitee.bleadv.lib.builder.ServiceBuilder;

import org.bluez.Adapter1;
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


        Adapter1 adapter1=connector.requireObject(adapters.get(0), Adapter1.class);

        BleAdapter adapter =new ModelMaker()
                .setConvertAdapter(new DbusConvertAdapter())
                .makeObject(BleAdapter.class,adapter1);

        //test(adapter);




        BleAdvertiser advertiser = getBuild(adapter);


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

    private static void test(BleAdapter bleAdapter) throws DBusException {

        Tester.testCallAll(bleAdapter);
        System.exit(0);
    }

    private static BleAdvertiser getBuild(BleAdapter adapter) {
        return new AdvertiseBuilder(adapter,"/bttest")
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
                    public void onDeviceDiscovered(Device1 dev, Map<String, Variant<?>> options) {
                        System.out.println("onDeviceDiscovered");
                    }

                    @Override
                    public void onDeviceRemoved(Device1 dev) {
                        System.out.println("onDeviceRemoved");
                    }
                })
                .setAdvertiseType(AdvertiseType.BROADCAST)
                .setAdvertiseBleName("adddd")
                .build();
    }

    private static void printMap(Map<?, ?> map, int level) {
        System.out.println("{");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            for (int i = 0; i < level; i++) {
                System.out.print("\t");
            }
            Object key = entry.getKey();
            System.out.printf("%-30s -> ", key.toString());

            Object value = entry.getValue();
            if (value instanceof Map) {
                printMap((Map) value, level + 1);
            } else {
                System.out.print(value.toString());
            }
            System.out.println();


        }
        System.out.println();
        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println("}");
    }


}

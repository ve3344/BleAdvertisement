一个Linux下作为中心节点广播低功耗蓝牙的库。

可以在Raspberry Pi上使用

# 实现功能

- 广播蓝牙
- 设置蓝牙名称
- 监听连接和断开
- 添加 多个Service，Characteristic，Descriptor
- 支持read，write，notify
- 支持长数据读取写入

# 待实现
- 扫描别的蓝牙设备
- 连接别的蓝牙设备
- indicate ,write no response 的兼容
# 依赖

```bash
#ubuntu/debian

sudo apt install -y dbus-java-bin bluez bluetooth libbluetooth-dev libudev-dev

```
# 使用

### 初始化
```java

BusConnector.init();


```

### 获取DBUS连接
```java

BusConnector connector = BusConnector.getInstance();

```

### 获取蓝牙适配器
```java
List<String> adapters = connector.findAdapters();

//这个列表是dbuspath的列表，使用connector.makeModel()，生成适配器示例

BleAdapter adapter = connector.makeModel(BleAdapter.class,adapters.get(0));
```

### 创建服务和特征
````java
new AdvertiseBuilder(adapter,"/bttest")
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

````

### 设置蓝牙信息
```java

adapter.setPowered(true);
adapter.setDiscoverable(true);
adapter.setDiscoverableTimeout(0);

```

### 设置广播名称,类型,连接监听（也可以在Builder设置）
```java

advertiser.setAdvertiseName();
advertiser.setAdvertiseType();
advertiser.setConnectionListener();

connector.findDevices()
```

### 广播开始和停止
```java
//广播开始前可以先断开已经连接的设备。
advertiser.disconnectAllDevices();

advertiser.startAdvertise();

advertiser.stopAdvertise();

```

### 其他
```java
//获取已经连接的设备 返回的是dbuspath，
//使用connector.makeModel()，生成连接设备实例
connector.findDevices();

//断开指定设备,使用这两个都行

bleDevice.disconnect();

bleAdapter.removeDevice();

```
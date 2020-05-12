package com.imitee.bleadv.lib.advertise;

import com.imitee.bleadv.lib.base.BleConstants;

import org.bluez.GattService1;
import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BleService implements GattService1 {
	public static final String THIS_INTERFACE = BleConstants.TYPE_GATT_SERVICE;
	private final boolean isPrimary ;
	private final String objectPath;
	private final String uuid;

	private Map<String, Variant<?>> properties;

	private List<BleCharacteristic> characteristics;

	public BleService(String objectPath, String uuid, boolean isPrimary) {
		this.objectPath = objectPath  ;
		this.uuid = uuid;
		this.isPrimary = isPrimary;
		this.characteristics = new ArrayList<>();
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	private DBusPath[] getCharacteristicsPathArray() {
		return characteristics.stream()
				.map(bleCharacteristic -> new DBusPath(bleCharacteristic.getObjectPath()))
				.toArray(DBusPath[]::new);
	}

	public List<BleCharacteristic> getCharacteristics() {
		return characteristics;
	}

	public Map<String, Map<String, Variant<?>>> getProperties() {
		properties = new HashMap<>();
		properties.put("Primary", new Variant<>(this.isPrimary));
		properties.put("UUID", new Variant<>(uuid));
		properties.put("Characteristics", new Variant<>(getCharacteristicsPathArray()));

		Map<String, Map<String, Variant<?>>> outMap = new HashMap<>();
		outMap.put(THIS_INTERFACE, properties);
		return outMap;
	}

	

	public String getUUID() {
		return uuid;
	}

	@Override
	public boolean isRemote() {
		return false;
	}

	@Override
	public String getObjectPath() {
		return objectPath;
	}

	public Map<String, Variant<?>> GetAll(String interface_name) {
		return properties;
	}

	public <A> A Get(String interface_name, String property_name) {
		System.out.println("Get");
		return null;
	}
	public <A> void Set(String interface_name, String property_name, A value) {
		System.out.println("Set");
	}

	@Override
	public String toString() {
		return "\n  BleService{" +"\n"+
				"  isPrimary=" + isPrimary +"\n"+
				"  objectPath='" + objectPath + '\'' +"\n"+
				"  uuid='" + uuid + '\'' +"\n"+
				"  characteristics=" + characteristics +"\n"+
				'}';
	}
}
package com.imitee.bleadv.lib.advertise;

import com.imitee.bleadv.lib.base.BleConstants;

import org.bluez.GattService1;
import org.freedesktop.dbus.types.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BleService implements GattService1 {
	public static final String THIS_INTERFACE = BleConstants.TYPE_GATT_SERVICE;
	private boolean isPrimary = true;
	private final String objectPath;
	private final String uuid;

	private Map<String, Variant<?>> properties;

	private List<BleCharacteristic> characteristics;

	public BleService(String objectPath, String uuid) {
		this.objectPath = objectPath  ;

		this.uuid = uuid;
		
		properties = new HashMap<>();
		properties.put("Primary", new Variant<>(isPrimary));
		properties.put("UUID", new Variant<>(uuid));
		properties.put("Characteristics", new Variant<>(""));
		
		characteristics = new ArrayList<>();
	}

	public void setPrimary(boolean primary) {
		isPrimary = primary;
	}

	public List<BleCharacteristic> getCharacteristics() {
		return characteristics;
	}

	public Map<String, Map<String, Variant<?>>> getProperties() {		
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
		return null;
	}
	public <A> void Set(String interface_name, String property_name, A value) {
	}
}
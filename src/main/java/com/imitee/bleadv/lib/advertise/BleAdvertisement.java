package com.imitee.bleadv.lib.advertise;


import org.bluez.LEAdvertisement1;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BleAdvertisement implements LEAdvertisement1, Properties{
	private final AdvertiseType advertiseType;
	private final String advertiseName;
	private final String objectPath;
	private Map<String, Variant<?>> properties;


	public BleAdvertisement(String objectPath, List<BleService> services, AdvertiseType advertiseType, String advertiseName) {
		this.objectPath = objectPath;
		this.advertiseType = advertiseType;
		this.advertiseName = advertiseName;

		this.properties = new HashMap<>();

		String[] uuids=new String[services.size()];
		for (int i = 0; i < services.size(); i++) {
			uuids[i]=services.get(i).getUUID();
		}


		properties.put("ServiceUUIDs", new Variant<>(uuids));
		properties.put("Type", new Variant<>(this.advertiseType.getValue()));
		properties.put("LocalName", new Variant<>(this.advertiseName));
	}


	@Override
	public String getObjectPath() {
		return objectPath;
	}

	@Override
	public Map<String, Variant<?>> GetAll(String interface_name) {
		return properties;
	}

	@Override
	public void Release() {

	}

	@Override
	public <A> A Get(String interface_name, String property_name) {
		return null;
	}

	@Override
	public <A> void Set(String interface_name, String property_name, A value) {

	}

	@Override
	public boolean isRemote() {
		return false;
	}
}

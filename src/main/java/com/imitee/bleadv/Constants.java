package com.imitee.bleadv;

// Various configs defining our setup

public final class Constants {
	public static final String SERVICE_UUID = "f5a5d298-7521-42c7-9423-f23a615d3861"; // Actual UUID

	public static final String CHARACTERISTIC_TIME_UUID = "7d2edead-f7bd-485a-bd9d-92ad6ecfe93e";
	public static final String CHARACTERISTIC_MSG_UUID = "7d2ebaad-f7bd-485a-bd9d-92ad6ecfe93e";
	
	public static final String TIME_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";


	public static final  String DBUS_OBJECT_BASE = "/org/blelib"; // Our local base DBus path
	
	private Constants() {}
}
package com.imitee.bleadv.lib.base;

import com.github.hypfvieh.DbusHelper;

import org.bluez.Device1;
import org.bluez.GattCharacteristic1;
import org.bluez.GattService1;
import org.bluez.exceptions.BluezAlreadyConnectedException;
import org.bluez.exceptions.BluezFailedException;
import org.bluez.exceptions.BluezInProgressException;
import org.bluez.exceptions.BluezNotConnectedException;
import org.bluez.exceptions.BluezNotReadyException;
import org.freedesktop.dbus.errors.NoReply;
import org.freedesktop.dbus.exceptions.DBusExecutionException;

import com.imitee.bleadv.lib.advertise.BleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BleDevice {
	public static final String THIS_INTERFACE = BleConstants.TYPE_DEVICE;
	private final String objectPath;
	
	private Device1 device1;
	private BusConnector helper;
	


	private String address;
	
	public BleDevice(Device1 device1, BusConnector helper) {
		this.device1 = device1;
		this.helper = helper;
		
		objectPath = device1.getObjectPath();
		address = this.helper.getProperty(objectPath, THIS_INTERFACE, "Address");
	}
	
	public List<GattCharacteristic1> getCharacteristics(GattService1 gs) {
		List<GattCharacteristic1> characteristic1s=new ArrayList<>();

		if (gs == null) {
			return characteristic1s;
		}
		
		Set<String> deviceNodes = DbusHelper.findNodes(helper.getConnection(),gs.getObjectPath());
		for (String deviceNode : deviceNodes) {
			String devicePath = gs.getObjectPath() + "/" + deviceNode;
			GattCharacteristic1 characteristic = helper.requireObject(devicePath,GattCharacteristic1.class);
			characteristic1s.add(characteristic);
		}
		return characteristic1s;
	}
	

	public String getAddress() {
		return address;
	}
	

	public List<GattService1> getServices() {

		List<GattService1> service1s=new ArrayList<>();
		Set<String> nodes = DbusHelper.findNodes(helper.getConnection(),objectPath);
		for (String s : nodes) {
			String serviceObjectPath = objectPath+ "/" + s;
						
			String serviceUUID = helper.getProperty(serviceObjectPath, BleService.THIS_INTERFACE, "UUID");

			GattService1 service1= helper.requireObject(serviceObjectPath,GattService1.class);
			service1s.add(service1);
		}
		return service1s;
	}
	
	public boolean isConnected() {
		return helper.getProperty(objectPath, THIS_INTERFACE, "Connected");
	}
	
	public boolean connect()  {
		try{
			device1.Connect();
		} catch (BluezNotReadyException | BluezFailedException | BluezInProgressException e) {
			e.printStackTrace();
	
		} catch (BluezAlreadyConnectedException e) {
			return true;
		
		/*
		 * NoReply is often thrown when two nodes attempt to connect to each other simultaneously 
		 * The default timeout is 20s, and reducing this would be nice, though that will require digging into the DBus-java source
		 * TODO: look into this
		 */
		} catch(NoReply n) { 
			System.out.println("Error connecting to " + objectPath + ": No Reply");
			return isConnected();
		} catch (DBusExecutionException e) {
			System.out.println("DBusExecutionException: " + e.getMessage());
			return isConnected();
		}
		
		return true;
	}
	
	public void disconnect() {
		try {
			device1.Disconnect();
		} catch (BluezNotConnectedException e) {
			System.out.println("Not connected, ignoring attempt to disconnect");
		}		
	}
	public String getObjectPath() {
		return objectPath;
	}
}

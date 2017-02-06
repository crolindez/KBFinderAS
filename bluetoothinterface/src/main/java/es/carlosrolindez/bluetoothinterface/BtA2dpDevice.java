package es.carlosrolindez.bluetoothinterface;

import android.bluetooth.BluetoothDevice;


public class BtA2dpDevice {

    //  BT device type
	private static String TAG = "BtA2dpDevice";


//    public static final UUID MY_UUID_SECURE = UUID.fromString("00000000-0000-1000-8000-00805f9b34fb");

	public final String deviceName;
	public final String deviceMAC;
	public boolean connected;
	public final BluetoothDevice mBtDevice;


	public BtA2dpDevice() 	{
		deviceName = "";
		deviceMAC = "";
		connected = false;
		mBtDevice = null;
	}

	public BtA2dpDevice(String name, BluetoothDevice device) {
		deviceName = name;
		deviceMAC = device.getAddress();
		connected = false;
		mBtDevice = device;
	}
	
/*	public static BluetoothDevice deviceInArray(ArrayList<BtA2dpDevice> deviceList, String MAC) {
		for (BtA2dpDevice device : deviceList)
		{
			if (MAC.equals(device.deviceMAC)) return device.mBtDevice;
		}
		return null;
	}

	
	public static void connectDeviceInArray(String MAC,ArrayList<BtA2dpDevice> deviceList) {
		for (BtA2dpDevice device : deviceList)
		{
			if (MAC.equals(device.deviceMAC)) {
				device.connected = true;
				return;
			}
		}	
	}
	
	public static void disconnectDevices(String MAC,ArrayList<BtA2dpDevice> deviceList) {
		for (BtA2dpDevice device : deviceList)
		{
			if (MAC.equals(device.deviceMAC)) {
				device.connected = false;
				return;
			}
		}	
	}

	public static boolean isDeviceConnected(String MAC,ArrayList<BtA2dpDevice> deviceList) {
		for (BtA2dpDevice device : deviceList)
		{
			if (MAC.equals(device.deviceMAC)) {
				return device.connected;
			}
		}	
		return false;
	}
	

	public static String findConnectedDevice(ArrayList<BtA2dpDevice> deviceList) {
		for (BtA2dpDevice device : deviceList)
		{
			if (device.connected) {
				return device.deviceMAC;
			}
		}
		return null;
	}
*/
}

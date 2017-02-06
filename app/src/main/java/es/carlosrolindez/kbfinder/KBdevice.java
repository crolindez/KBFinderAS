package es.carlosrolindez.kbfinder;

import android.bluetooth.BluetoothDevice;

import es.carlosrolindez.bluetoothinterface.BtA2dpDevice;


public class KBdevice extends BtA2dpDevice {
	
    //  BT device type
	private static String TAG = "KBdevice"; 
	
    public static final int OTHER = 0;
    public static final int ISELECT = 1;
	public static final int SELECTBT = 2;
    public static final int IN_WALL_BT = 3;
	public static final int IN_WALL_WIFI = 4;
    
    private static final String iSelectFootprint = "00:08:F4";
    private static final String inWallFootprint = "00:0D:18";
    private static final String selectBtFootprint = "8C:DE:52";
	private static final String inWallWiFiFootprint = "12:05:12";
	
//    public static final UUID MY_UUID_SECURE = UUID.fromString("00000000-0000-1000-8000-00805f9b34fb");
    
	public final int deviceType;

	
	public KBdevice(String name, BluetoothDevice device) {
		super(name,device);
        deviceType = getDeviceType(deviceMAC);
	}
	

	
	public static int getDeviceType(String deviceMAC) {
		String MAC = deviceMAC.substring(0,8);
		if (MAC.equals(iSelectFootprint)) return ISELECT;
		if (MAC.equals(inWallFootprint)) return IN_WALL_BT;
		if (MAC.equals(selectBtFootprint)) return SELECTBT;
        if (MAC.equals(inWallWiFiFootprint)) return IN_WALL_WIFI;
		return OTHER;
	}


	

	public static long password(String MAC) {

		String[] macAddressParts = MAC.split(":");
		long littleMac = 0;
		int rotation;
		long code = 0;
		long pin;
		
		for(int i=2; i<6; i++) {
		    Long hex = Long.parseLong(macAddressParts[i], 16);
		    littleMac *= 256;
		    littleMac += hex;
		}
		
		rotation = Integer.parseInt(macAddressParts[5], 16) & 0x0f;
		
		for(int i=0; i<4; i++) {
			Long hex =  Long.parseLong(macAddressParts[i], 16);
		    code *= 256;
		    code += hex;
		}
		code = code >> rotation;		
		code &= 0xffff;

		littleMac &= 0xffff;

		pin = littleMac ^ code;
		pin %= 10000;
		
		return pin;
				
	}

	
}

package es.carlosrolindez.kbfinder;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;







public class SettingsClass {

	private final ArrayKBdeviceSettings listKBdeviceSettings;
    private static final String TAG = "SettingsClass";
    private final Context mContext;

	public SettingsClass (Context context) {
		listKBdeviceSettings = new ArrayKBdeviceSettings();
		mContext = context;
	}

	
	public KBdeviceSettings getDeviceInArray(String MAC) {
		for (KBdeviceSettings device:listKBdeviceSettings) {
			if (MAC.equals(device.MAC)) return device;
		}
		KBdeviceSettings device = new KBdeviceSettings(MAC);
		listKBdeviceSettings.addSorted(device);
		return device;
	}
	


    public void writeToFile(String filename) {
    	

    	try {	
    		FileOutputStream outStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
    		OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
//			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");

	        writer.write(Integer.toString(listKBdeviceSettings.size())); writer.write("\n");
	      	for (KBdeviceSettings deviceSettings:listKBdeviceSettings) {
	      		writer.write(deviceSettings.MAC + "\n");
	      		writer.write(Integer.toString((deviceSettings.fmPack.size()))); writer.write("\n");
	      		for (FmSet set:deviceSettings.fmPack) {
	      			writer.write(set.frequency + "\n");
	      			writer.write(set.rds + "\n");
	      		}	      		
	      	}
	      	writer.flush();
	      	writer.close();   	
    	} catch (IOException e) {
      		Log.e(TAG,"error writing settings");
    	  e.printStackTrace();
    	}
    	
   }
     
    public static SettingsClass readFromFile(String filename,Context context) {
    	String freq;
    	String rds;
    	String mac;
    	int numberKBdevices;
    	int numberFmSets;
    	FmSet set;
    	KBdeviceSettings device;

		SettingsClass settings = new SettingsClass(context);
//		return settings;

        try {
//        	FileInputStream inputStream = new FileInputStream(filename);
        	FileInputStream inputStream = context.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                
                if ( (receiveString = bufferedReader.readLine()) != null ) {
	               	for (numberKBdevices = Integer.parseInt(receiveString); numberKBdevices>0; numberKBdevices--) {
	                    mac = bufferedReader.readLine();
	            		device = new KBdeviceSettings(mac);
	                    if ( (receiveString = bufferedReader.readLine()) != null ) {
		    	        	for (numberFmSets = Integer.parseInt(receiveString); numberFmSets>0; numberFmSets--) {
		    	        		freq = bufferedReader.readLine();
		    	        		rds = bufferedReader.readLine();
		    	        		set = new FmSet(freq,rds);
		    	        		device.fmPack.addSorted(set);
		    	        	}
		    	        	settings.listKBdeviceSettings.addSorted(device);
		            	}
	               	}
                }
                bufferedReader.close();
            }
            inputStream.close();
        }
        catch (FileNotFoundException e) {   
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return settings;

    }

}

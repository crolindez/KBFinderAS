package es.carlosrolindez.kbfinder;

import android.os.Parcel;
import android.os.Parcelable;




public class KBdeviceSettings implements Parcelable {
	public String MAC;
	public ArrayFmPackage fmPack;
	
	public KBdeviceSettings(String MAC) {
		this.MAC = MAC;
		fmPack = new ArrayFmPackage();
	}

	
	public FmSet getFreqInArray(String targetFreq) {
		for (FmSet set:fmPack) {
			if (targetFreq.equals(set.frequency)) return set;
		}
		return null;
	}
	
	public int getIndexInArray(String targetFreq) {
		int index = 0;
		for (FmSet set:fmPack) {
			if (targetFreq.equals(set.frequency)) return index;
			index++;
		}
		return -1;
	}
	
	public void addFreq2Array(String targetFreq,String targetRDS) {
		FmSet set = new FmSet(targetFreq, targetRDS);
		fmPack.addSorted(set);
	}
	
	public void removeFreqFromArray(String targetFreq) {
		int counter = 0;
		for (FmSet set:fmPack) {
			if (targetFreq.equals(set.frequency)) {
				fmPack.remove(counter);
				return;
			} else {
				counter++;
			}
		}
	}
	
	
	@Override
    public int describeContents() {
        return 0;
    }
	
    @Override
    public void writeToParcel(Parcel parcel, int arg1) 
    {
        parcel.writeString(MAC);
        parcel.writeInt(fmPack.size());
		for (FmSet set:fmPack) {
	        parcel.writeString(set.frequency);
	        parcel.writeString(set.rds);
		}
    }
     
    public static final Creator<KBdeviceSettings> CREATOR = new Creator<KBdeviceSettings>() {
    	
    	@Override
        public KBdeviceSettings createFromParcel(Parcel parcel) 
        {        	
            String MAC = parcel.readString();
            KBdeviceSettings device = new KBdeviceSettings(MAC);
            
            int size = parcel.readInt();
            for (int i=0; i<size; i++) {
            	device.addFreq2Array(parcel.readString(),parcel.readString());
            }
            
            return device;
        }
 
        @Override
        public KBdeviceSettings[] newArray(int size) 
        {
            return new KBdeviceSettings[size];
        }
         
    };




}

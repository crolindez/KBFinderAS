package es.carlosrolindez.kbfinder;

import java.util.ArrayList;

public class ArrayBTdevice extends ArrayList<KBdevice> {
	
	public void addSorted(KBdevice newDevice) {
		if (isEmpty()) {
			add(newDevice);
			return;
		}
		int position=0;
		for (KBdevice device : this) {
			if (newDevice.deviceType>device.deviceType) {
				add(position, newDevice);
				return;
			} else if (newDevice.deviceType==device.deviceType) {
				if (newDevice.deviceName.compareToIgnoreCase(device.deviceName)<=0) {
					add(position, newDevice);
					return;
				}
			}
			position++;
		}
		add(newDevice);
	}
	

}

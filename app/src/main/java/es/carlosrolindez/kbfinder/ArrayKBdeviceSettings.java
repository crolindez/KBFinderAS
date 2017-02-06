package es.carlosrolindez.kbfinder;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ArrayKBdeviceSettings extends ArrayList<KBdeviceSettings> {
	
	public void addSorted(KBdeviceSettings newKbSettings) {
		if (isEmpty()) {
			add(newKbSettings);
			return;
		}
		int position=0;
		for (KBdeviceSettings kbSettings : this) {
			if (newKbSettings.MAC.compareTo(kbSettings.MAC)<0) {
				add(position, newKbSettings);
				return;
			} else if (newKbSettings.MAC.compareTo(kbSettings.MAC)==0) {
				kbSettings.MAC = newKbSettings.MAC;
				kbSettings.fmPack = newKbSettings.fmPack;
				return;
			}
			position++;
		}
		add(newKbSettings);
	}

}	
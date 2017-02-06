package es.carlosrolindez.kbfinder;

import java.util.ArrayList;


@SuppressWarnings("serial")
public class ArrayFmPackage extends ArrayList<FmSet> {
	
	public void addSorted(FmSet newStation) {
		if (isEmpty()) {
			add(newStation);
			return;
		}
		
		int position=0;
		for (FmSet station : this) {
			if (Float.parseFloat(newStation.frequency)<Float.parseFloat(station.frequency)) {
				add(position, newStation);
				return;
			}  else if (Float.parseFloat(newStation.frequency)==Float.parseFloat(station.frequency)) {
				station.frequency = newStation.frequency;
				station.rds = newStation.rds;
				return;
			}

			position++;
		}
		add(newStation);
	}
}	
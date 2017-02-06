package es.carlosrolindez.kbfinder;

public class FmSet {
	public String frequency;
	public String rds;
	
	public FmSet(String freq, String RDS ) {
		frequency = freq;
		rds = RDS;
	}
	
	public String getRDS() {
		return rds;
	}
	
	public void setRDS(String rds) {
		this.rds = rds;
	}
	
	public String getFm() {
		return frequency;
	}
	
	public void setFm(String Freq) {
		frequency = Freq;
	}
}
	

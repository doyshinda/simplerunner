package ca.simplerunner.misc;

public class LocationStat {
	
	double lat;
	double lng;
	long timestamp;
	
	public LocationStat(double lat, double lng, long timestamp) {
		this.lat = lat;
		this.lng = lng;
		this.timestamp = timestamp;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLng() {
		return this.lng;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}

}

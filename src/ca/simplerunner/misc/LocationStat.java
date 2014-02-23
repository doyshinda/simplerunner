package ca.simplerunner.misc;

/**
 * This class represents a Location statistic, including:
 * @lat - latitude of a point along the run route
 * @lng - longitude of a point along the run route
 * @timestamp  - the timestamp the point was taken
 * 
 * @author Abe Friesen
 *
 */
public class LocationStat {
	
	double lat;
	double lng;
	long timestamp;
	
	public LocationStat(double lat, double lng, long timestamp) {
		this.lat = lat;
		this.lng = lng;
		this.timestamp = timestamp;
	}
	
	/*
	 * Return latitude
	 */
	public double getLat() {
		return this.lat;
	}
	
	/*
	 * Return longitude
	 */
	public double getLng() {
		return this.lng;
	}
	
	/*
	 * Return timestamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}
}

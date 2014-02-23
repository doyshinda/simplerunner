package ca.simplerunner.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a Run Statistic, including:
 * @date - the date of the run
 * @pace - the pace of the run
 * @time - the total time of the run
 * @distance - the distance of the run
 * 
 * @author Abe Friesen
 *
 */
public class RunStat {
	
	private String pace;
	private String time;
	private long id;
	private double distance;
	private String date;
	
	public RunStat(String date, long id, String pace, String time, double distance) {
		this.date = date;
		this.pace = pace;
		this.time = time;
		this.id = id;
		this.distance = distance;
	}
	
	/*
	 * Get the date
	 */
	public String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd H:m:s z yyyy", Locale.ENGLISH);
		Date date;
		try {
			date = sdf.parse(this.date);
			sdf.applyPattern("EEE MMM dd");
			return sdf.format(date);
		}
		catch (ParseException e) {
			return this.date;
		}
		
	}
	
	/*
	 * Get the time
	 */
	public String getTime() {
		return this.time;
	}
	
	/*
	 * Get the distance
	 */
	public double getDistance() {
		return this.distance;
	}
	
	/*
	 * Get the runstats ID of the run
	 */
	public long getID() {
		return this.id;
	}
	
	/*
	 * Get the pace
	 */
	public String getPace() {
		return this.pace;
	}

}

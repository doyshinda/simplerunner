package ca.simplerunner.misc;

import ca.simplerunner.R;
import ca.simplerunner.app.Main;
import ca.simplerunner.app.RunView;
import ca.simplerunner.database.Database;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This class handles creating the Main view as shown
 * in the RunView Activity on the 'Main' tab
 * 
 * @author Abe Friesen
 * 
 */
public class MainRunFragment extends Fragment {
	
	Database db;
	RunStat stat;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.mainview, container, false);
	
		RunView parent = (RunView) getActivity();
		long runID = parent.getRunID();
		db = new Database(parent);
		stat = db.getRunStat(runID);
		init(myView); 
        return myView;
    }
	
	/*
	 * Initiate the text views
	 */
	private void init(View myView) {
		TextView dateHeader = (TextView) myView.findViewById(R.id.dateHeader);
		TextView timeField = (TextView) myView.findViewById(R.id.tmeField);
		TextView distanceField = (TextView) myView.findViewById(R.id.distField);
		TextView avgPace = (TextView) myView.findViewById(R.id.avgPaceField);
		TextView avgSpeed = (TextView) myView.findViewById(R.id.avgSpeedField);
		
		dateHeader.setText(stat.getDate());
		timeField.setText(formatTime(stat.getTime()));
		distanceField.setText(Main.formatDistance(stat.getDistance()));
		String avgSpeedStr = calcAvgSpeed(stat.getTime(), stat.getDistance());
		avgPace.setText(calcAvgPace(avgSpeedStr));
		avgSpeed.setText(avgSpeedStr);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.closeDB();
	}
	
	/*
	 * Calculate the average speed
	 */
	public String calcAvgSpeed(String time, double distance) {
		double speed = (distance * 3600)/Double.valueOf(time);
		return Main.formatSpeed(speed);
	}
	
	/*
	 * Calculate the average pace
	 */
	public String calcAvgPace(String speedStr) {
		speedStr = speedStr.replace(" km/h", "");
		double speed = Double.valueOf(speedStr);
		double pace = 1/speed * 60;
		int minutes = (int) Math.floor(pace);
		int seconds = (int) ((pace - Math.floor(pace)) * 60);
		String paceStr = (Integer.toString(minutes) + ":" + Integer.toString(seconds));
		return paceStr + " min/km";
	}
	
	/*
	 * Format run time from milliseconds
	 */
	public static String formatTime(String timeStr) {
		long time = Long.valueOf(timeStr);
		long milHours = 3600000;
		long milMins = 60000;
		long milSecs = 1000;
	 	long hours = 0;
	 	long minutes = 0;
	 	long seconds = 0;
	 	
		if(time >= milHours) {
	 		hours = time / milHours;
	 		time = time % milHours;
	 	}
	 	if(time >= milMins) {
	 		minutes = time / milMins;
	 		time = time % milMins;
	 	}
	 	if(time >= milSecs) {
	 		seconds = time / milSecs;
	 		if(time % milSecs > 500) {
	 			seconds++;
	 		}
	 	}
	 	String minColon = ":";
	 	String secColon = ":";
	 	if(minutes < 10) {
	 		minColon = ":0";
	 	}
	 	if(seconds < 10) {
	 		secColon = ":0";
	 	}
	 	return hours + minColon + minutes + secColon + seconds;
	}
}

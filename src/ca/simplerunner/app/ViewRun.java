package ca.simplerunner.app;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import ca.simplerunner.R;
import ca.simplerunner.database.Database;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class ViewRun extends Activity implements ActionBar.TabListener {

	long runID;
	Database db;
	RunStat stat;
	MapFragment mf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		this.runID = getIntent().getLongExtra("runID", 69);
		db = new Database(this);
		stat = db.getRunStat(runID);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab().setText("Main")
				.setTabListener(this), 0, true);
		actionBar.addTab(actionBar.newTab().setText("Split")
				.setTabListener(this), 1, false);
		actionBar.addTab(actionBar.newTab().setText("Map")
				.setTabListener(this), 2, false);
	}

	/*
	 * Populate Action bar with menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.history_action, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * Handle menu item chosen
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_back:
			db.closeDB();
			finish();
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		if(tab.getText().toString().contentEquals("Main")) {
			loadMainView();
		}
		if(tab.getText().toString().contentEquals("Split")) {
			loadSplitView();
		}
		if(tab.getText().toString().contentEquals("Map")) {
			loadMapView();
		}
	}

	/*
	 * Load the Main View
	 */
	public void loadMainView() {
		if(mf != null) {
			getFragmentManager().beginTransaction().remove(mf).commit();
		}
		setContentView(R.layout.mainview);
		TextView dateHeader = (TextView) findViewById(R.id.dateHeader);
		TextView timeField = (TextView) findViewById(R.id.tmeField);
		TextView distanceField = (TextView) findViewById(R.id.distField);
		TextView avgPace = (TextView) findViewById(R.id.avgPaceField);
		TextView avgSpeed = (TextView) findViewById(R.id.avgSpeedField);
		dateHeader.setText(stat.getDate());
		timeField.setText(formatTime(stat.getTime()));
		distanceField.setText(Main.formatDistance(stat.getDistance()));
		String avgSpeedStr = calcAvgSpeed(stat.getTime(), stat.distance);
		avgPace.setText(calcAvgPace(avgSpeedStr));
		avgSpeed.setText(avgSpeedStr);
	}
	
	/*
	 * Calculate the Average Speed
	 */
	public String calcAvgSpeed(String time, double distance) {
		double speed = (distance * 3600)/Double.valueOf(time);
		return Main.formatSpeed(speed);
	}
	
	/*
	 * Calculates the average pace
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
	public String formatTime(String timeStr) {
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
	
	/*
	 * Load the Split View
	 */
	public void loadSplitView() {
		if(mf != null) {
			getFragmentManager().beginTransaction().remove(mf).commit();
		}
		setContentView(R.layout.mainview);
		TextView dateHeader = (TextView) findViewById(R.id.dateHeader);
		TextView timeField = (TextView) findViewById(R.id.tmeField);
		TextView distanceField = (TextView) findViewById(R.id.distField);
		TextView avgPace = (TextView) findViewById(R.id.avgPaceField);
		TextView avgSpeed = (TextView) findViewById(R.id.avgSpeedField);
		dateHeader.setText(stat.getDate());
		timeField.setText(formatTime(stat.getTime()));
		distanceField.setText(Main.formatDistance(stat.getDistance()));
		String avgSpeedStr = calcAvgSpeed(stat.getTime(), stat.distance);
		avgPace.setText(calcAvgPace(avgSpeedStr));
		avgSpeed.setText(avgSpeedStr);		
	}
	
	/*
	 * Load the Map View
	 */
	public void loadMapView() {
		setContentView(R.layout.mapview);
		if(mf == null) {
			mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		}
		GoogleMap map = mf.getMap();
		ArrayList<LatLng> coords = db.getRunCoordinates(runID);
		PolylineOptions opts = new PolylineOptions();
		opts.addAll(coords);
		int mid = coords.size()/2;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords.get(mid), 15));
		map.addPolyline(opts);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}
}
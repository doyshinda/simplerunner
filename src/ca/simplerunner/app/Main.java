package ca.simplerunner.app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import ca.simplerunner.R;
import ca.simplerunner.database.Database;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

/**
 * The Main Activity of the app. Responsible for logging
 * GPS coordinates, timing the run and updating the UI
 * with relevant run information
 * 
 * @author Abe Friesen
 *
 */
public class Main extends Activity
implements ConnectionCallbacks, OnConnectionFailedListener,
LocationListener, GpsStatus.Listener {

	private Database db;
	private LocationManager locationManager;
	private LocationClient locationClient;
	private ArrayList<Location> locations;
	private Location prevLoc;
	private GoogleMap map;

	private TextView gpsStatus;
	private TextView paceField;
	private TextView distanceField;
	private TextView speedField;
	private Chronometer timer;

	private boolean gpsConnected = false;
	private boolean logging = false;
	private long startTime = 0;
	private long delta = 0;
	private double distance = 0.0;
	private String pace = "00:00";
	private float[] results;

	// Location Request settings
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(4000)
			.setFastestInterval(1000)
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initDB();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.addGpsStatusListener(this);
		results = new float[1];

		init();
		goToMyLocation();
	}

	@Override
	public void onResume() {
		super.onResume();
		gpsOn();
		initDB();
	}

	/*
	 * Populate Action bar with menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.default_action, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * Handle menu item chosen
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_history:
			loadHistoryActivity();
			return true;
		default:
			return false;
		}
	}

	/*
	 * Initiate the layout and widgets
	 */
	private void init() {
		Button startButton = (Button) findViewById(R.id.startButton);
		Button stopButton = (Button) findViewById(R.id.stopButton);
//		Button pauseButton = (Button) findViewById(R.id.pauseButton);
		gpsStatus = (TextView) findViewById(R.id.GPSstatus);
		paceField = (TextView) findViewById(R.id.paceField);
		speedField = (TextView) findViewById(R.id.speedField);
		distanceField = (TextView) findViewById(R.id.distanceField);
		timer = (Chronometer) findViewById(R.id.timer);

		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!gpsOn()) {
					showGpsDisabledAlert();
				}
				else {
					initLogging();
				}
			}
		});

		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				recordRun();				
			}
		});

//		pauseButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				pauseLogging();
//			}
//		});
	}

	/*
	 * Go to the nearest location of the phone as possible
	 */
	private void goToMyLocation() {
		if(!gpsOn()) {
			showGpsDisabledAlert();
		}

		MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mf.getMap();
		map.setMyLocationEnabled(true);
		locationClient = new LocationClient(
				getApplicationContext(),
				this,
				this);
		locationClient.connect();    	
	}

	/*
	 * Initiate logging GPS coordinates
	 */
	private void initLogging() {
		//Wait for the GPS to connect
		if(!logging) {
			final ProgressDialog progress = new ProgressDialog(Main.this);
			progress.setTitle("Waiting for GPS");
			progress.setMessage("Waiting for GPS connection to be established.");
			progress.show();
			new Thread(new Runnable() {
				public void run() {
					while(!gpsConnected) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							continue;
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							beginLogging(progress);
						}
					});
				}
			}).start();
		}
	}

	/*
	 * Begin logging GPS coordinates
	 */
	public void beginLogging(ProgressDialog progress) {
		progress.hide();
		if(!logging) {
			locations = new ArrayList<Location>();
			logging = true;
			startTime = SystemClock.elapsedRealtime() - delta;
			timer.setBase(startTime);
			timer.start();
		}
	}

	/*
	 * Pause logging GPS coordinates
	 */
//	private void pauseLogging() {
//		timer.stop();
//		logging = false;
//		long currTime = SystemClock.elapsedRealtime();
//		delta = currTime - startTime;
//	}

	/*
	 * Stop logging GPS coordinates and store the information
	 * in the database
	 */
	private void recordRun() {
		timer.stop();
		long runLength = SystemClock.elapsedRealtime() - startTime;
		logging = false;
		String date = new Date().toString();
		if(!pace.contentEquals("00:00") && (locations.size() > 0)) {
			final long statsID = db.addRunStats(date, pace, distance, Long.toString(runLength));
			final ProgressDialog progress = new ProgressDialog(Main.this);
			progress.setTitle("Saving Run");
			progress.setCancelable(false);
			progress.setMessage("Saving your run information...");
			progress.show();
			new Thread(new Runnable() {
				public void run() {
					Database db = new Database(Main.this);
					db.batchInsertLocations(statsID, locations);
					db.closeDB();
					runOnUiThread(new Runnable() {
						public void run() {
							loadRunViewActivity(statsID, progress);
						}
					});
				}
			}).start();
		}
		reset();
	}

	/*
	 * Load View Run
	 */
	public void loadRunViewActivity(long runID, ProgressDialog progress) {
		reset();
		progress.hide();
		closeDB();
		Intent i = new Intent(Main.this, RunView.class);
		i.putExtra("runID", runID);
		startActivity(i);
	}

	/*
	 * Reset app with default values
	 */
	public void reset() {
		delta = 0;
		distance = 0.0;
		prevLoc = null;
		pace = "00:00";
		timer.setText(this.getResources().getString(R.string.zeroTime));
		distanceField.setText(this.getResources().getString(R.string.zeroDistance));
		paceField.setText(this.getResources().getString(R.string.zeroPace));
		speedField.setText(this.getResources().getString(R.string.zeroSpeed));
	}

	/*
	 * Called when GPS Status has changed
	 */
	@Override
	public void onGpsStatusChanged(int event) {
		switch(event) {
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			greenLightGps();
			break;
		case GpsStatus.GPS_EVENT_STARTED:
			//Nothing to do here
			break;
		case GpsStatus.GPS_EVENT_STOPPED:
			redLightGps();
			break;
		}
	}

	/*
	 * Called when location is changed.
	 */
	@Override
	public void onLocationChanged(Location loc) {
		LatLng latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
		if(logging) {
			update(loc);
		}
		else {
			CircleOptions circle  = new CircleOptions();
			circle.fillColor(Color.BLUE);
			circle.center(latlng);
			map.addCircle(circle);
		}
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
	}

	/*
	 * Alert user GPS is disabled and ask if they want to enable it
	 */
	private void showGpsDisabledAlert() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Simple Runner requires GPS to be enabled, it is disabled in your device. Would you like to enable it?")
		.setCancelable(false)
		.setPositiveButton("Enable GPS",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				Intent callGPSSettingIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(callGPSSettingIntent);
			}
		});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	/*
	 * Update the GPS Icon to green
	 */
	private void greenLightGps() {
		gpsConnected = true;
		gpsStatus.setBackgroundColor(this.getResources().getColor(R.color.green));
		gpsStatus.setText(this.getResources().getString(R.string.GPSon));
	}

	/*
	 * Update the GPS Icon to red
	 */
	private void redLightGps() {
		gpsConnected = false;
		gpsStatus.setBackgroundColor(this.getResources().getColor(R.color.red));
		gpsStatus.setText(this.getResources().getString(R.string.GPSoff));
	}

	/*
	 * Update the app with the newest location
	 */
	private void update(final Location loc) {
		locations.add(loc);
		// Calculate distance run
		new Thread(new Runnable() {
			public void run() {
				if(prevLoc != null) {
					Location.distanceBetween(prevLoc.getLatitude(), prevLoc.getLongitude(),
							loc.getLatitude(), loc.getLongitude(), results);
					distance += results[0];
				}
				prevLoc = loc;
				final double speed = calculateSpeed();
				pace = calculatePace(speed);
				runOnUiThread(new Runnable() {
					public void run() {
						speedField.setText(formatSpeed(speed));
						paceField.setText(pace);
						distanceField.setText(formatDistance(distance));
					}
				});
			}
		}).start();
	}

	/*
	 * Called when Location connected.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		locationClient.requestLocationUpdates(REQUEST, this);
	}

	/*
	 * Called when Location disconnected.
	 */
	@Override
	public void onDisconnected() {
		// Nothing to do here
	}

	/*
	 * Notification when the connection fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// Nothing to do here		
	}

	/*
	 * Checks if the GPS is enabled on device
	 */
	private boolean gpsOn() {
		boolean isOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		return isOn;
	}

	/*
	 * Calculates the speed based on time and distance since
	 * the previous location
	 */
	private double calculateSpeed() {
		long currTime = SystemClock.elapsedRealtime();
		long delta = currTime - startTime;
		double speed = (distance * 3600)/delta;
		return speed;
	}

	/*
	 * Convert the speed double to string with units per hour
	 */
	public static String formatSpeed(double speed) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String speedStr = f.format(speed);
		return speedStr + " km/h";
	}

	/*
	 * Format the distance to the appropriate units
	 */
	public static String formatDistance(double distance) {
		DecimalFormat f = new DecimalFormat("#0.00");
		String distStr = f.format(distance/1000);
		return distStr + " km";
	}

	/*
	 * Calculates the pace based on speed
	 */
	private String calculatePace(double speed) {
		double pace = 1/speed * 60;
		int minutes = (int) Math.floor(pace);
		int seconds = (int) ((pace - minutes) * 60);
		if(minutes > 120) {
			minutes = 0;
			seconds = 0;
		}
		String paceStr = (Integer.toString(minutes) + ":" + Integer.toString(seconds));
		return paceStr + " min/km";
	}

	/*
	 * Loads the History Activity
	 */
	private void loadHistoryActivity() {
		Intent i = new Intent(Main.this, History.class);
		startActivity(i);
	}
	
	/*
	 * Initiate Database
	 */
	private void initDB() {
		if(db == null)
			db = new Database(this);
	}
	
	/*
	 * Close Database
	 */
	private void closeDB() {
		db.closeDB();
		db = null;
	}

	/*
	 * Quit App when user pushes back button on Activity
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		quitApp();
	}

	/*
	 * Shut down app
	 */
	public void quitApp() {
		closeDB();
		this.finish();
		Process.killProcess(Process.myPid());
	}
}

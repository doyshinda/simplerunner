package ca.simplerunner.database;

import java.util.ArrayList;

import ca.simplerunner.app.RunStat;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Class that handles all database transactions
 * 
 * @author Abe Friesen
 */
public class Database {
	
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	
	public Database(Context context) {
		dbHelper = new DBHelper(context);
		openDb();
	}
	
	private void openDb() {
		db = dbHelper.getWritableDatabase();
	}
	
	public void closeDB() {
		dbHelper.close();
	}
	
	/*
	 * Query the database for the lat and lng coordinates
	 * for run with <statsID>. Returns list of LatLng
	 */
	public ArrayList<LatLng> getRunCoordinates(long statsID) {
		Cursor cursor = db.query(DBHelper.LOC_TABLE, new String[]{DBHelper.LAT, DBHelper.LNG}, 
				DBHelper.STATS_ID + " = ?", 
				new String[]{String.valueOf(statsID)}, null, null, null);
		
		ArrayList<LatLng> locations = new ArrayList<LatLng>();
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			double lat = cursor.getDouble(cursor.getColumnIndex(DBHelper.LAT));
			double lng = cursor.getDouble(cursor.getColumnIndex(DBHelper.LNG));
			locations.add(new LatLng(lat, lng));
			cursor.moveToNext();
		}
		cursor.close();
		
		return locations;
	}
	
	/*
	 * Query the database for run stats
	 */
	public ArrayList<RunStat> getRunStats() {
		Cursor cursor = db.query(DBHelper.STATS_TABLE, new String[]{DBHelper.DATETIME, DBHelper.STATS_ID, DBHelper.PACE,
				DBHelper.DISTANCE, DBHelper.TIME}, null, null, null, null, DBHelper.STATS_ID + " DESC", "10");
		ArrayList<RunStat> results = new ArrayList<RunStat>();
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATETIME));
			long id = cursor.getLong(cursor.getColumnIndex(DBHelper.STATS_ID));
			String pace = cursor.getString(cursor.getColumnIndex(DBHelper.PACE));
			String time = cursor.getString(cursor.getColumnIndex(DBHelper.TIME));
			double distance = cursor.getDouble(cursor.getColumnIndex(DBHelper.DISTANCE));
			RunStat stat = new RunStat(date, id, pace, time, distance);
			results.add(stat);
			cursor.moveToNext();
		}
		return results;		
	}
	
	/*
	 * Query the database for a specific run stat
	 */
	public RunStat getRunStat(long id) {
		Cursor cursor = db.query(DBHelper.STATS_TABLE, new String[]{DBHelper.DATETIME, DBHelper.PACE,
				DBHelper.DISTANCE, DBHelper.TIME}, DBHelper.STATS_ID + " = ?", new String[]{Long.toString(id)}, null,
				null, DBHelper.STATS_ID + " DESC", "10");
		cursor.moveToFirst();
		String date = cursor.getString(cursor.getColumnIndex(DBHelper.DATETIME));
		String pace = cursor.getString(cursor.getColumnIndex(DBHelper.PACE));
		String time = cursor.getString(cursor.getColumnIndex(DBHelper.TIME));
		double distance = cursor.getDouble(cursor.getColumnIndex(DBHelper.DISTANCE));
		RunStat stat = new RunStat(date, id, pace, time, distance);
		return stat;
	}
	
	/*
	 * Update the stats table with the actual stats from
	 * the run after the run is complete
	 */
	public int updateStats(int statsID, String pace, double distance, String time) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.PACE, pace);
		values.put(DBHelper.DISTANCE, distance);
		values.put(DBHelper.TIME, time);
		String whereClause = "where " + DBHelper.STATS_ID + " = ?";
		String[] whereArgs = new String[]{String.valueOf(statsID)};
		int rowsChanged = db.update(DBHelper.STATS_TABLE, values, whereClause, whereArgs);
		return rowsChanged;

	}
	
	/*
	 * Create a new entry in the Stats table and return its ID
	 */
	public long addRunStats(String date, String pace, double distance, String time) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.DATETIME, date);
		values.put(DBHelper.PACE, pace);
		values.put(DBHelper.DISTANCE, distance);
		values.put(DBHelper.TIME, time);
		
		Long id = db.insert(DBHelper.STATS_TABLE, null, values);
		return id;
	}
	
	/*
	 * Insert a location into the database
	 */
	public void insertLocation(long statsID, double lat, double lng) {
		
		ContentValues values = new ContentValues();
		values.put(DBHelper.STATS_ID, statsID);
		values.put(DBHelper.LAT, lat);
		values.put(DBHelper.LNG, lng);
		
		@SuppressWarnings("unused")
		Long id = db.insert(DBHelper.LOC_TABLE, null, values);
	}

}

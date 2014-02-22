package ca.simplerunner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class that extends SQLiteOpenHelper to aid in 
 * database transactions
 * 
 * @author Abe Friesen
 */
public class DBHelper extends SQLiteOpenHelper {
	
	public static final String STATS_TABLE = "runstats";
	public static final String LOC_TABLE = "locations";
	public static final String DATABASE_NAME="simplerunner.db";
	public static final String ID = "id";
	public static final String STATS_ID = "statsid";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String PACE = "pace";
	public static final String DISTANCE = "distance";
	public static final String TIME = "time";
	public static final String DATETIME = "datetime";
	public static int DATABASE_VERSION = 6;
	

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + STATS_TABLE + " (");
		sb.append(DATETIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");
		sb.append(STATS_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, ");
		sb.append(PACE + " TEXT NOT NULL, ");
		sb.append(DISTANCE + " REAL NOT NULL, ");
		sb.append(TIME + " TEXT NOT NULL");
		sb.append(");");
		db.execSQL(sb.toString());
		
		sb = new StringBuilder();
		sb.append("CREATE TABLE " + LOC_TABLE + " (");
		sb.append(ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, ");
		sb.append(STATS_ID + " INTEGER NOT NULL, ");
		sb.append(LAT + " REAL NOT NULL, ");
		sb.append(LNG + " REAL NOT NULL, ");
		sb.append("FOREIGN KEY(" + STATS_ID + ") REFERENCES ");
		sb.append(LOC_TABLE + "(" + STATS_ID + ")");		
		sb.append(");");
		db.execSQL(sb.toString());
		
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DATABASE_VERSION = newVersion;
		//TODO: Figure out how to save all data when upgrading database
		// and check to see what the newest version of the database is
//		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}

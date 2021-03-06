package com.example.splitit;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * A class for creating a database with one table which has the columns EventName, DateOfEvent, Attender and TotalCost.
 */
public class Helper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 26;
	// Table name
	public static final String TABLE_NAME="EVENTLIST";

	// Column names
	public static final String colEventName = "EventName";
	public static final String colDate = "DateOfEvent";
	public static final String colAttender = "Attender";
	public static final String colTotalCost = "TotalCost";

	//Sql queries
	private static final String CREATE_TABLE = 	"CREATE TABLE " + TABLE_NAME + " (" + colEventName + " TEXT, " + colTotalCost + " INTEGER, " + colAttender + " TEXT, " + colDate + " TEXT, PRIMARY KEY ("+colEventName+", "+colAttender+", " + colDate +"))";

	private static final String DROP_TABLE ="DROP TABLE IF EXISTS " + TABLE_NAME;

	// Database name
	private static final String DATABASE_NAME="basename";
	/**
	 * Constructor that calls the parent's constructor. 
	 * @param context to use to open or create the database
	 */
	public Helper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE);

	}		

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		try {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		} catch (SQLException e) {
		}
	}

}

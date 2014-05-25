package com.example.splitit;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Helper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	// Table name
	public static final String TABLE_NAME="EVENTLIST";

	// Column names
	private static final String colEventName = "EventName";
	private static final String colDate = "Date";
	private static final String colAttender = "Attender";
	private static final String colTotalCost = "Total Cost";
	
	//Sql queries
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + colEventName + " VARCHAR(100) PRIMARY KEY, " + colDate + " VARCHAR(100), " + colAttender + " VARCHAR(100), " + colTotalCost + " INTEGER;";
	

	// Database name
	private static final String DATABASE_NAME="basename";
	
	//Other
	private Context context;

	public Helper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		try {
			db.execSQL(CREATE_TABLE);
		} catch (SQLException e) {

		}

	}		

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Helper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	/**
	 * 
	 * @param eventname the name of the event added to the database
	 * @param attender the name of the attender added to the database
	 * @param date the date of the event
	 */
	public void addAttender(String eventname, String attender, int date){

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(colEventName, eventname);
		values.put(colDate, date);
		values.put(colAttender, attender);
		values.put(colTotalCost, 100);

		db.insert(TABLE_NAME, null, values);

		db.close();

	}

	public List<String> getAttenders(String name, int date){
		List<String> attenders = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();

		String[] columns = {colAttender};

		Cursor cursor =
				db.query(true,
						TABLE_NAME,
						columns, colDate + "=" + date + " AND " + colEventName +"=" + name,
						null, null, null, null, null, null);

		if (cursor != null){
			cursor.moveToFirst();
		}
		if(!cursor.isLast()){
			attenders.add(cursor.getString(0));
			cursor.moveToNext();
		}
		else {
			attenders.add(cursor.getString(0));
		}

		cursor.close();
		return attenders;
	}

	public List<String> getAllAttenders(){

		List<String> attenders = new ArrayList<String>();

		//		String query = "SELECT " + colAttender + " FROM " + EVENT_TABLE;

		String query = "SELECT * FROM " + TABLE_NAME + ";";

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.rawQuery(query, null);

		if(cursor.moveToFirst()){
			do{
				if(!cursor.isNull(0)){
					attenders.add(cursor.getString(0));
				}
			}while(cursor.moveToNext());
		}
		cursor.close();
		return attenders;
	}
}

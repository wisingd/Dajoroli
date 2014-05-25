package com.example.splitit;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Helper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 2;
	// Table name
	public static final String EVENT_TABLE="Eventlist";

	// Column names
	public static final String colEventName = "Event Name";
	public static final String colDate = "Date";
	public static final String colAttender = "Attender";
	public static final String colTotalCost = "Total Cost";

	// Database name
	public static final String DATABASE_NAME="basename";

	//	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY_WORD + " TEXT, " + KEY_DEFINITION + " TEXT);";

	public Helper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + EVENT_TABLE + " (" + colEventName + " TEXT PRIMARY KEY , " + colDate + " TEXT PRIMARY KEY , "
				+ colAttender + " TEXT PRIMARY KEY , " + colTotalCost + " INTEGER)");
	}		

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Helper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
		onCreate(db);
	}
/**
 * 
 * @param eventname the name of the event added to the database
 * @param attender the name of the attender added to the database
 * @param date the date of the event
 */
	public void addAttender(String eventname, String attender, int date){

		Log.d("addAttender", attender);

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(colEventName, eventname);
		values.put(colDate, date);
		values.put(colAttender, attender);
		values.put(colTotalCost, 100);

		db.insert(EVENT_TABLE, null, values);

		db.close();

	}

	public List<String> getAttenders(String name, int date){
		List<String> attenders = new ArrayList<String>();

		SQLiteDatabase db = this.getReadableDatabase();

		String[] columns = {colAttender};

		Cursor cursor =
				db.query(true,
						EVENT_TABLE,
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
		
		String query = "SELECT " + colAttender + " FROM " + EVENT_TABLE;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Cursor cursor = db.rawQuery(query, null);
		
		String string = null;
		
		if(cursor.moveToFirst()){
			do{
				attenders.add(cursor.getString(0));
			}while(cursor.moveToNext());
		}
		return attenders;
	}
}

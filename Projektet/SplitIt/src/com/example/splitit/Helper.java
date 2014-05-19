package com.example.splitit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	private static final String EVENT_TABLE="Eventlist";
	private static final String colEventName = "Event Name";
	private static final String colDate = "Date";
	private static final String colAttender = "Attender";
	private static final String colTotalCost = "Total Cost";
	private static final String DATABASE_NAME="basename";
	
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
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
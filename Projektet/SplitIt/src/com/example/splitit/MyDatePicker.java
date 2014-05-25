package com.example.splitit;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.DatePicker;


public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	int pYear;
	int pDay;
	int pMonth;
	public static SharedPreferences sharedevent;

	public static final String MyEvent = "Myevent";


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		pYear = year;
		pDay = day;
		pMonth = month;
		
		sharedevent = getActivity().getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);
		
		Editor editor = sharedevent.edit();
		
		editor.putInt("year", pYear);
		editor.putInt("month", pMonth);
		editor.putInt("day", pDay);
		
		editor.commit();
		
	}
	
	public int getYear(){
		return pYear;
	}
	
	public int getMonth(){
		return pMonth;
	}
	
	public int getDay(){
		return pDay;
	}
}
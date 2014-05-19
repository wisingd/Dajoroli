package com.example.splitit;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class MyDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	int pYear;
	int pDay;
	int pMonth;

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
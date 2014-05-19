package com.example.splitit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactViewing extends ActionBarActivity {

	public SharedPreferences sharednames;

	public SharedPreferences shareddebts;

	public static final String MyNames = "Mynames";

	public static final String MyDebts = "Mydebts";

	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		TextView textView = new TextView(this);
		textView.setTextSize(20);
		textView.setBackgroundColor(Color.parseColor("#87DDFF"));
		textView.setText(getContactString());
		setContentView(textView);
	}

	public String getContactString(){

		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);

		Map<String,?> mappen = shareddebts.getAll();

		Set<String> settet = mappen.keySet();

		Iterator <String> iteratorn = settet.iterator();

		List<String> iOweThese = new ArrayList<String>();
		List<String> theseOweMe = new ArrayList<String>();
		List<String> evenWithThese = new ArrayList<String>();		
		while(iteratorn.hasNext()){
			String key = iteratorn.next(); 
			if( shareddebts.getInt(key, 0) > 0){

				theseOweMe.add(key);
			}
			else if(shareddebts.getInt(key,0) == 0){
				evenWithThese.add(key);
			}
			else
				iOweThese.add(key);
		}
		Collections.sort(iOweThese);
		Collections.sort(theseOweMe);
		Collections.sort(evenWithThese);

		String message = "";
		if(iOweThese.size() != 0){
			message = "You have a debt to these contacts: \n";
			for(String temp: iOweThese){
				message = message + temp + "\t" + Integer.toString(Math.abs(shareddebts.getInt(temp, 0))) + " kr.\n";
			}
			message = message + "\n";
		}
		if(theseOweMe.size() != 0){
			message = message +  "These contacts have a debt to you \n";
			
			for(String temp: theseOweMe){
				message = message +  temp  + "\t" + Integer.toString(shareddebts.getInt(temp, 0)) + " kr.\n";
			}
			message = message +"\n";
		}
		if(evenWithThese.size() != 0){
			message = message + "You are even with these contacts: \n";

			for(String temp: evenWithThese){
				message = message + temp +"\n";
			}
			message = message + "\n";
		}

		return message;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() { }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}
}
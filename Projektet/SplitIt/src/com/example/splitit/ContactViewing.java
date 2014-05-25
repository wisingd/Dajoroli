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

/**
 * An activity that displays the users stored contacts and their associated debts.
 * 
 * @author Johannes
 *
 */
public class ContactViewing extends ActionBarActivity {

	public SharedPreferences sharednames;
	public SharedPreferences shareddebts;

	public static final String MyNames = "Mynames";
	public static final String MyDebts = "Mydebts";
/**
 * Starts the activity and sets the layout to a textview that is filled using getContactString(). 
 */
	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);

		TextView textView = new TextView(this);
		textView.setTextSize(20);
		textView.setBackgroundColor(Color.parseColor("#50649F"));
		textView.setText(getContactString());
		setContentView(textView);
	}
/**
 * Returns a string that describes what the user's current contact list contains. 
 * Takes the values and keys stored in the SharedPreferences shareddebts and transforms 
 * them into a map from which the keyset is stored as a set. An iterator then runs through
 * the different contacts and depending on which value that is associated with the 
 * contact the contact is added to one out of three lists. These list are then written 
 * in a specific order in a string and this string is then returned.  
 * 
 * @return A string containing all the contacts and their associated debts.
 */
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

		if (iOweThese.size() != 0){
			message = "You have a debt to these contacts: \n";

			for(String temp: iOweThese){
				message = message + temp + " \t" + Integer.toString(Math.abs(shareddebts.getInt(temp, 0))) + " kr.\n";
			}
			message = message + "\n";
		}

		if (theseOweMe.size() != 0){
			message = message +  "These contacts have a debt to you \n";

			for(String temp: theseOweMe){
				message = message +  temp  + " \t" + Integer.toString(shareddebts.getInt(temp, 0)) + " kr.\n";
			}
			message = message +"\n";
		}

		if (evenWithThese.size() != 0){
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

		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
package com.example.splitit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Creates an activity that allows the user to write the name and date of an event.
 *   
 * @author Johannes
 */
public class EventCreater extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private static int year, month, day;
	public static SharedPreferences sharedevent;
	public static final String MyEvent = "Myevent";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creater);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.event_creater, menu);
		return true;
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_event_creater,
					container, false);
			return rootView;
		}
	}
	/**
	 * The method that runs when the user clicks the "Date"-button, displays a DatePicker.
	 * 
	 * @param v the view that runs the method, i.e. the button "Date"
	 */
	public void showDatePickerDialog(View v) {
		TextView value;
		value = (TextView) findViewById(R.id.textview2);

		MyDatePicker newFragment = new MyDatePicker();

		newFragment.show(getFragmentManager(), "datePicker");

		year = newFragment.getYear();
		month = newFragment.getMonth();
		day = newFragment.getDay();

		value.setText("" + day + "/" + month + "/" + year);
	}	

	public void setDate(){
		TextView value = (TextView) findViewById(R.id.textview2);

		sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);

		value.setText("" + sharedevent.getInt("day", 0) + "/" + sharedevent.getInt("month", 0) + "/" + sharedevent.getInt("year", 0));

	}
	/**
	 * Displays a dialog where the user can enter a name for the event.
	 * Consists  of a EditText-line where the user can write the name, 
	 * a button that closes the dialog and a button that writes the written
	 *  name in an TextView on the original activity.
	 *  
	 * @param view the view that runs the method, i.e. the button "Name" 
	 */
	public void eventNameDialog(View view){

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Name");
		alert.setMessage("Choose a name for your event");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				TextView value;
				value = (TextView) findViewById(R.id.textview);
				String message = "" + input.getText();

				value.setText("" + message);

				sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);

				Editor editor = sharedevent.edit();
				editor.putString("name", message);
				editor.commit();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}
	
	public void showAttenderPickerDialog(View v){
		
	}

	/**
	 * Run when the user clicks the "Create" button and continues with the event creation.
	 * 
	 * 
	 * @param v The view from which the method is initiated.
	 */
	public void createEvent(View v){
		sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);

		new AlertDialog.Builder(this).setTitle("Work in progress").setMessage("Work in progress. Datumet du valde är " + sharedevent.getInt("day", 0) + "/" + sharedevent.getInt("month", 0) + " året " + sharedevent.getInt("year", 0) ).setPositiveButton("Return", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				return;
			}
		}).show();

		//
//		Intent intent = new Intent(this, AddAttenders.class);
		//		EditText editText = (EditText) findViewById(R.id.event_name);
		//		String message = editText.getText().toString();
		//
//		if(sharedevent.getString("name", "").length() != 0){
//			startActivity(intent);
//		}
		//
		//		else{
		//			new AlertDialog.Builder(this).setTitle("No name").setMessage("You did not enter a valid name.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
		//				public void onClick(DialogInterface dialog, int which){
		//					return;
		//				}
		//			}).show();
		//		}
	}
}
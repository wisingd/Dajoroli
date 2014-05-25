package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Creates an activity that allows the user to write the name and date of an event.
 *   
 */
public class EventCreater extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private static int year, month, day;
	public static SharedPreferences sharedevent;
<<<<<<< HEAD
=======

	public static SharedPreferences sharednames;

>>>>>>> FETCH_HEAD
	public static final String MyEvent = "Myevent";

	public static final String MyNames = "Mynames";

	public Helper db;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creater);

		db = new Helper(this);
		
		db.getWritableDatabase();

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

		final EditText input = new EditText(this);
		alert.setTitle("Name")
		.setMessage("Choose a name for your event")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String message = "" + input.getText();


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

	public void showAttenderPickerDialog(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		if(mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Choose contacts")
			.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {

					if (isChecked) {
						selectedItems.add(list.get(which));
					} 
					else if (selectedItems.contains(which)) {
						selectedItems.remove(Integer.valueOf(which));
					}
				}
			});

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String listString = "";
					sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);

					if(sharedevent.getString("name", "") != null && sharedevent.getInt("year", 0) > 2014)
						for (String s :selectedItems){
							db.addAttender(sharedevent.getString("name", ""), s, sharedevent.getInt("year", 0));
							listString = listString  + s + "\n";
						}

					new AlertDialog.Builder(view.getContext()).setPositiveButton
					("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setTitle("The Names:")
					.setMessage("" + listString + " och det valda namnet är " + sharedevent.getString("name", "") + " och året är " + sharedevent.getInt("year", 0))
					.show();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			alert.show();
		}

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts.")
			.setPositiveButton("okidoki", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}


	/**
	 * Run when the user clicks the "Create" button and continues with the event creation.
	 * 
	 * 
	 * @param v The view from which the method is initiated.
	 */
	public void createEvent(View v){

		List<String> attenders = db.getAllAttenders();
		String output = "";

		for(String s : attenders){
			output = output + s + "\n";
		}
		new AlertDialog.Builder(this).setTitle("Work in progress").setMessage(output).setPositiveButton("Return", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				return;
			}
		}).show();

	}

}
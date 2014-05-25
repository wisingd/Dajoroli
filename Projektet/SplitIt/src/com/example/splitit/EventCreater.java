package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;




/**
 * Creates an activity that allows the user to write the name and date of an event.
 *   
 */
public class EventCreater extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private static int year, month, day;

	public static SharedPreferences sharedevent;

	public static SharedPreferences shareddebts;

	public static final String MyEvent = "Myevent";

	public static final String MyDebts = "Mydebts";
	
	Helper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creater);

		helper = new Helper(this);

//		SQLiteDatabase sqldata = helper.getWritableDatabase();
//
//		String eventname = "Name1";
//		int date = 0;
//		String attender = "Klas";
//		int cost = 100;
//		
//		ContentValues values = new ContentValues();
//		values.put("EventName", eventname);
//		values.put("DateOfEvent", date);
//		values.put("Attender", attender);
//		values.put("TotalCost", cost);
//
//		long id = sqldata.insert("EVENTLIST", null, values);
//		
//		Toast toast = Toast.makeText(this, Long.toString(id), 3);
//		
//		toast.show();
		
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
	 * 
	 * @param v the view that runs the method, i.e. the button "Date"
	 */
	public void showDatePickerDialog(View v) {
		
		MyDatePicker newFragment = new MyDatePicker();

		newFragment.show(getFragmentManager(), "datePicker");

		year = newFragment.getYear();
		month = newFragment.getMonth();
		day = newFragment.getDay();

	}	

	/**
	 * Displays a dialog where the user can enter a name for the event.
	 * Consists  of a EditText-line where the user can write the name, 
	 * a button that closes the dialog and a button that writes the written
	 *  name in an TextView on the original activity.
	 *  
	 * @param view the view that runs the method, i.e. the button "Name" 
	 */
	public void eventNameDialog(final View view){

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
				showAttenderPickerDialog(view);			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	public long addAttender(String eventname, String attender, int date){

		SQLiteDatabase db = helper.getWritableDatabase();
		
		int amount = 100;

		ContentValues values = new ContentValues();
		values.put(Helper.colEventName, eventname);
		values.put(Helper.colDate, date);
		values.put(Helper.colAttender, attender);
		values.put(Helper.colTotalCost, amount);

		long id = db.insert(Helper.TABLE_NAME, null, values);

		return id;

	}


	public void showAttenderPickerDialog(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = shareddebts.getAll();

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

					if(sharedevent.getString("name", "") != null)
						for (String s :selectedItems){
							long id = addAttender(sharedevent.getString("name", ""), s, sharedevent.getInt("year", 0));
							listString = listString  + s + "\n";
							if (id == -1){
								Toast toast = Toast.makeText(EventCreater.this, "failed", 3);
								toast.show();
							}
							else{
								Toast toast = Toast.makeText(EventCreater.this, "success", 3);
								toast.show();
							}
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

	public String getAllAttenders(){
		SQLiteDatabase db = helper.getReadableDatabase();

		String attenders = "";

		String[] columns = {Helper.colAttender};

		Cursor cursor = db.query(Helper.TABLE_NAME, columns,null, null, null, null, null);

		while(cursor.moveToNext()){
			attenders= attenders + cursor.getString(0);
			
			
		}
//		if(cursor.moveToFirst()){
//			do{
//				if(!cursor.isNull(0)){
//				}
//			}while(cursor.moveToNext());
//		}
		db.close();
		return attenders;

	}

	public void showEvents(View v){

		String attenders = getAllAttenders();
//		String output = "";
//
//		for(String s : attenders){
//			output = output + s + "\n";
//		}
		new AlertDialog.Builder(this).setTitle("Work in progress").setMessage(attenders).setPositiveButton("Return", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				return;
			}
		}).show();

	}

}
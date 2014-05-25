package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import android.text.InputType;
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

	private String datet;

	Helper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creater);

		helper = new Helper(this);

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
				eventCostDialog(view);			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});

		alert.show();
	}

	public void eventCostDialog(final View view){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		alert.setTitle("Cost")
		.setMessage("Specify the cost of the event")
		.setView(input)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);
				Editor editor = sharedevent.edit();

				String value = "" + input.getText();
				int cost = Integer.parseInt(value);

				editor.putInt("cost", cost);
				editor.commit();
				showAttenderPickerDialog(view);			
			}

		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
	}

	public long addAttender(String eventname, String attender, String date, int cost){

		SQLiteDatabase db = helper.getWritableDatabase();


		ContentValues values = new ContentValues();
		values.put(Helper.colEventName, eventname);
		values.put(Helper.colDate, date);
		values.put(Helper.colAttender, attender);
		values.put(Helper.colTotalCost, cost);

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
					else  {
						selectedItems.remove(list.get(which));
					}
				}
			});

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String listString = "";
					sharedevent = getSharedPreferences(MyEvent, Context.MODE_WORLD_READABLE);
					shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
					Editor editor = shareddebts.edit();
					Editor editor2 = sharedevent.edit();

					if(sharedevent.contains("year")){
						datet = Integer.toString(sharedevent.getInt("day", 0)) + "/" + Integer.toString(sharedevent.getInt("month", 0)+1) + " " + Integer.toString(sharedevent.getInt("year", 0));

						for (String s :selectedItems){
							long id = addAttender(sharedevent.getString("name", ""), s, datet, sharedevent.getInt("cost", 0));
							editor.putInt(s, shareddebts.getInt(s,0) + sharedevent.getInt("cost", 0)/selectedItems.size());
							listString = listString + "\n" + s ;
							if (id == -1){
								Toast toast = Toast.makeText(EventCreater.this, "failed", 3);
								toast.show();
							}
							else{
								Toast toast = Toast.makeText(EventCreater.this, "success", 3);
								toast.show();
							}
						}
						editor.commit();

						new AlertDialog.Builder(view.getContext()).setPositiveButton
						("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.setTitle("The Names:")
						.setMessage("You have added  " + listString + " \nto '" + sharedevent.getString("name", "") + "' that took place on "+ datet+"." )
						.show();
						
						editor2.clear().commit();

					}else{
						new AlertDialog.Builder(view.getContext())
						.setTitle("Set date")
						.setMessage("You have to set a date for the event.")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

							public void onClick(DialogInterface dialog, int which){
								return;
							}
						}).show();

						

					}

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
			.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}


	public void showEvents(final View view){

		final SQLiteDatabase db = helper.getReadableDatabase();

		String[] columns = {Helper.colEventName};

		Cursor cursor = db.query(true,Helper.TABLE_NAME, columns, null, null, null, null, null, null);

		final List<String> list = new ArrayList<String>();

		while(cursor.moveToNext()){
			list.add(cursor.getString(cursor.getColumnIndex(Helper.colEventName)));
		}

		db.close();

		CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
		final ArrayList<String> selectedItems = new ArrayList<String>();

		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
		alert.setTitle("Which event?")
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setItems(cs, new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int which) {

				final int position = which;
				final String chosenevent = list.get(which);

				String[] columns2 = {Helper.colAttender, Helper.colEventName, Helper.colDate, Helper.colTotalCost};

				SQLiteDatabase db = helper.getReadableDatabase();

				String [] whereargs = new String []{chosenevent};
				Cursor cursor2 = db.query(Helper.TABLE_NAME, columns2, "EventName=?",whereargs , null, null, null);

				List<String> attendlist = new ArrayList<String>();

				String date = "";
				int cost = 0;

				while(cursor2.moveToNext()){
					attendlist.add(cursor2.getString(cursor2.getColumnIndex(Helper.colAttender)));
					date = cursor2.getString(cursor2.getColumnIndex(Helper.colDate));
					cost = cursor2.getInt(cursor2.getColumnIndex(Helper.colTotalCost));
				}
				String str="";
				for(String s : attendlist){
					str = str +"\n" + s ;
				}

				new AlertDialog.Builder(view.getContext())
				.setTitle(chosenevent)
				.setMessage("Date: " + date + "\nTotal cost: " + cost + " kr \nAttenders: "+ str )
				.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).show();

			}
		}).show();
	}



}
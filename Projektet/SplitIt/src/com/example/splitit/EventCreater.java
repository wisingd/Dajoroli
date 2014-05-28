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
	 * Displays a datepicker.
	 * @param v the view that runs the method, i.e. the button "Date"
	 */
	public void showDatePickerDialog(View v) {

		MyDatePicker newFragment = new MyDatePicker();

		newFragment.show(getFragmentManager(), "datePicker");

	}	

	/**
	 * Starts the chain of the events that leads to the creation of events.
	 * Checks if the user has already specified a date for the event to be created 
	 * and if so displays an AlertDialog where the user can type in the name of the
	 * event. When the user has typed a name the typed name is stored as a 
	 * SharedPreference and the method eventCostDialog is run. If the user fails to
	 * enter a valid name an AlertDialog will appear and the event creation will be 
	 * aborted.
	 *  
	 * @param view the view that runs the method, i.e. the button "Create" 
	 */
	public void eventNameDialog(final View view){

		sharedevent = getSharedPreferences(MyEvent, Context.MODE_PRIVATE);

		if(sharedevent.contains("year")){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			final EditText input = new EditText(this);
			alert.setTitle("Name")
			.setMessage("Choose a name for your event")
			.setView(input)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String message = "" + input.getText();

					if (message.length() != 0){
						Editor editor = sharedevent.edit();
						editor.putString("name", message);
						editor.commit();
						eventCostDialog(view);
					}
					else{
						Toast.makeText(view.getContext(), "You have to set a name to be able to create an event.", Toast.LENGTH_LONG).show();
						eventNameDialog(view);
					}
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});

			alert.show();
		}else{
			Miscellaneous.displayMessage("Set date", "You have to set a date to be able to create an event", view.getContext());
		}

	}

	/**
	 * Allows the user to enter an amount and save the given amount.
	 * Presents the user with a AlertDialog where the cost of the
	 * event that is being created is supposed to be typed.
	 * If the user types a valid number, i.e. types anything, the entered
	 * number will be stored as a SharedPreference and showAttenderPickerDialog
	 * wil be run. If the user fails to type anything a new AlertDialog will be 
	 * displayed and the event creation will be aborted.
	 * @param view
	 */
	public void eventCostDialog(final View view){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		alert.setTitle("Cost")
		.setMessage("Specify the cost of the event")
		.setView(input)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				sharedevent = getSharedPreferences(MyEvent, Context.MODE_PRIVATE);
				Editor editor = sharedevent.edit();
				String value = "" + input.getText();

				if(value.length()!=0){

					int cost = Integer.parseInt(value);

					editor.putInt("cost", cost);
					editor.commit();
					showAttenderPickerDialog(view);
				}
				else{
					Toast.makeText(view.getContext(), "You have to specify an amount, e.g. 0.", Toast.LENGTH_LONG).show();
					eventCostDialog(view);
				}

			}

		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.show();
	}
	/**
	 * Adds a row to the database EVENTLIST with the given parameters as columns.
	 * 
	 * @param eventname The name of the event that will be added to the database
	 * @param attender The name of the attender that will be added to the database
	 * @param date The date of the event that will be added to the database
	 * @param cost The cost of the event that will be added to the database
	 * @return the row ID of the newly inserted row, or -1 if an error occurred 
	 */
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

	/**
	 * Allows the user to choose attenders to an event trough a AlertDialog and runs addAttender for every chosen attender.
	 * First checks if the user has any added contacts and if not displays an AlertDialog and aborts the event creation.
	 * If the user do have contacts a list of them is displayed in AlertDialog where the user can choose how
	 * many of the contacts as the user wants. In the AlertDialog there is also a Cancel-button and 
	 * an Ok-button. If the user press the Ok-button the number of chosen attenders is checked and
	 * if none have been selected another AlertDialog is displayed and the event creation is aborted.
	 * If the Ok-button is pressed and some attenders have been picked these attenders will be added
	 * to the database through addAttenders with the values stored from eventCostDialog, eventNameDialog 
	 * and the datepicker.
	 *  
	 * @param view
	 */
	public void showAttenderPickerDialog(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
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

			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					if(selectedItems.size()!=0){
						sharedevent = getSharedPreferences(MyEvent, Context.MODE_PRIVATE);
						shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
						Editor editor = shareddebts.edit();
						Editor editor2 = sharedevent.edit();

						datet = Integer.toString(sharedevent.getInt("day", 0)) + "/" + Integer.toString(sharedevent.getInt("month", 0)+1) + " " + Integer.toString(sharedevent.getInt("year", 0));
						
						String listString = Miscellaneous.listToPrettyString(selectedItems);

						for (String s :selectedItems){
							addAttender(sharedevent.getString("name", ""), s, datet, sharedevent.getInt("cost", 0));
							editor.putInt(s, shareddebts.getInt(s,0) + sharedevent.getInt("cost", 0)/selectedItems.size());
						}

						editor.commit();
						String message ="You have added  " + listString + " to '" + sharedevent.getString("name", "") + "' that took place on "+ datet+"." ; 

						editor2.clear().commit();
						Miscellaneous.displayMessage("Event created!", message, view.getContext());

					}else{
						Toast.makeText(view.getContext(), "You have to set at least one attender", Toast.LENGTH_LONG).show();
						showAttenderPickerDialog(view);
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

			Miscellaneous.displayMessage("No friends :(","You do not have any contacts.", view.getContext());
		}
	}
	public void showEvents(final View view){

		SQLiteDatabase db = helper.getWritableDatabase();

		String[] columns = {Helper.colEventName, Helper.colDate};

		Cursor cursor = db.query(true,Helper.TABLE_NAME, columns, null, null, null, null, null, null);

		final List<String> list = new ArrayList<String>();
		final List<String> datelist = new ArrayList<String>();
		final List<String> outputlist=new ArrayList<String>();

		while(cursor.moveToNext()){
			outputlist.add(cursor.getString(cursor.getColumnIndex(Helper.colEventName)) + " - " + cursor.getString(cursor.getColumnIndex(Helper.colDate)));
			datelist.add(cursor.getString(cursor.getColumnIndex(Helper.colDate)));
			list.add(cursor.getString(cursor.getColumnIndex(Helper.colEventName)));
		}

		db.close();

		CharSequence[] cs = outputlist.toArray(new CharSequence[outputlist.size()]);

		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
		alert.setTitle("Which event?")
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setItems(cs, new DialogInterface.OnClickListener() {
			public void onClick (DialogInterface dialog, int which) {

				final String chosenevent = list.get(which);

				final String chosendate = datelist.get(which);

				String[] columns2 = {Helper.colAttender, Helper.colEventName, Helper.colDate, Helper.colTotalCost};

				SQLiteDatabase db = helper.getReadableDatabase();

				String where = Helper.colEventName +"=? AND "+ Helper.colDate + "=?"; 

				String [] whereargs = {chosenevent, chosendate};
				Cursor cursor2 = db.query(Helper.TABLE_NAME, columns2, where, whereargs , null, null, null);

				List<String> attendlist = new ArrayList<String>();


				int cost =0;
				while(cursor2.moveToNext()){
					attendlist.add(cursor2.getString(cursor2.getColumnIndex(Helper.colAttender)));
					cost = cursor2.getInt(cursor2.getColumnIndex(Helper.colTotalCost));
				}

				String str="";
				for(String s : attendlist){
					str = str +"\n" + s ;
				}

				new AlertDialog.Builder(view.getContext())
				.setTitle(chosenevent)
				.setMessage("Date: " + chosendate + "\nTotal cost: " + cost + " kr \nAttenders: "+ str )
				.setPositiveButton("OK", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).setNeutralButton("Edit", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
						alert.setTitle("Edit option");

						List<String> options = new ArrayList<String>();
						options.add("Remove attender");
						options.add("Change cost");
						options.add("Change name");
						options.add("Change date");

						CharSequence[] cs = options.toArray(new CharSequence[options.size()]);

						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						})
						.setItems(cs, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,	int which) {
								if(which == 0){
									removeAttender(chosenevent, chosendate, view);
								}
								else if(which == 1){
									changeCost(chosenevent, chosendate);
								}
								else if(which == 2){
									changeName(chosenevent, chosendate);
								}
								else{
									changeDate(chosenevent, chosendate);
								}
							}
						}).show();
					}
				})
				.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						final SQLiteDatabase db = helper.getWritableDatabase();

						String[] columns = {Helper.colAttender, Helper.colEventName, Helper.colDate, Helper.colTotalCost};

						String where = Helper.colEventName + "=? AND "+ Helper.colDate + "=?";

						String[] args = {chosenevent, chosendate};

						Cursor cursor = db.query(Helper.TABLE_NAME, columns, where, args, null, null, null);

						final List<String> list = new ArrayList<String>();

						int cost =0;
						while(cursor.moveToNext()){
							list.add(cursor.getString(cursor.getColumnIndex(Helper.colAttender)));
							cost = cursor.getInt(cursor.getColumnIndex(Helper.colTotalCost));
						}


						deletionConfirmation(chosenevent, cost, list, view);
					}
				}).show();
			}
		}).show();
	}
	/**
	 * 
	 * @param chosenevent The name of the event that will be deleted
	 * @param totalCost The total cost of the event that will be deleted
	 * @param list A list containing the attenders
	 * @param view 
	 */
	public void deletionConfirmation(final String chosenevent, final int totalCost,final List<String> list, View view){
		String att = Miscellaneous.listToPrettyString(list);
		
		final SQLiteDatabase db = helper.getWritableDatabase();
		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
		String output = "Are you sure you want to delete the event '" + chosenevent + "'? This will decrease "+att+" debts by " + Integer.toString(totalCost/list.size()) + " kr each."; 
		alert.setTitle("Confirm")
		.setMessage(output)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

				Editor editor = shareddebts.edit();
				for(String s : list){
					editor.putInt(s, shareddebts.getInt(s,0)-totalCost/list.size());
				}
				editor.commit();

				String where2 = Helper.colEventName +"=?";
				String[] args2 ={chosenevent};
				db.delete(Helper.TABLE_NAME, where2, args2);
				db.close();
			}
		}).show();
	}

	/**
	 * Displays an AlertDialog where the user can choose attenders to remove from a given event and performs the
	 * necessary changes to the database and the SharedPreferences.
	 * 
	 * @param eventname The name of the event from which a set of attenders should be removed
	 * @param eventdate The date of the event from which a set of attender should be removed
	 * @param view 
	 */
	public void removeAttender(final String eventname, final String eventdate, final View view){

		final SQLiteDatabase db = helper.getWritableDatabase();

		String[] columns = {Helper.colAttender};

		String where = Helper.colEventName+"= ? AND " + Helper.colDate + "=?"; 

		String[] whereargs = {eventname, eventdate};

		Cursor cursor = db.query(Helper.TABLE_NAME, columns, where, whereargs, null, null, null);

		final ArrayList<String> list = new ArrayList<String>();

		while(cursor.moveToNext()){
			list.add(cursor.getString(cursor.getColumnIndex(Helper.colAttender)));
		}

		CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

		final ArrayList<String> selectedItems = new ArrayList<String>();

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Who do you want to remove?")
		.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {

				if (isChecked) {
					selectedItems.add(list.get(which));
				} 
				else {
					selectedItems.remove(list.get(which));
				}
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setPositiveButton("Next", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(list.size()!=0){

					String[] columns = {Helper.colTotalCost, Helper.colDate, Helper.colAttender, Helper.colEventName};

					String where =  Helper.colDate + "=? AND " + Helper.colEventName + "=?";

					String[] whereargs =  {eventdate, eventname};

					Cursor cursor2 = db.query(Helper.TABLE_NAME, columns, where, whereargs, null, null, null,null);

					List<String> originallist = new ArrayList<String>();

					int cost = 0;
					while(cursor2.moveToNext()){
						originallist.add(cursor2.getString(cursor2.getColumnIndex(Helper.colAttender)));
						cost = cursor2.getInt(cursor2.getColumnIndex(Helper.colTotalCost));
					}

					int origcost = cost/originallist.size();

					int change = cost/(originallist.size()-selectedItems.size())-origcost;

					shareddebts=getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

					Editor editor = shareddebts.edit();

					where = Helper.colAttender + "=? AND " + Helper.colDate + "=? AND " + Helper.colEventName + "=?";
					for(String s : selectedItems){
						String[] whereargs2 = {s, eventdate, eventname};  
						db.delete(Helper.TABLE_NAME, where, whereargs2);
						editor.putInt(s, shareddebts.getInt(s, 0)-origcost);
						originallist.remove(s);
					}
					for(String s :originallist){
						editor.putInt(s, shareddebts.getInt(s, 0) + change);
					}
					editor.commit();
					if(selectedItems.size()==1){
						Miscellaneous.displayMessage("Attender removed", "You have removed the attender.", view.getContext());
					}
					else{
						Miscellaneous.displayMessage("Attenders removed", "You have removed the attenders.", view.getContext());
					}
				}

				else{
					Miscellaneous.displayMessage("No one selected", "You did not choose any attender to remove", view.getContext());
				}
			}
		}).show();
	}

	public void changeCost(String eventname, String eventdate){

	}
	public void changeName(String eventname, String eventdate){

	}
	public void changeDate(String eventname, String eventdate){

	}

}
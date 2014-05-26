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

	private int cost;

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

				if(input.getText()!=null){
					String value = "" + input.getText();
					int cost = Integer.parseInt(value);

					editor.putInt("cost", cost);
					editor.commit();
					showAttenderPickerDialog(view);
				}
				else{
					new AlertDialog.Builder(view.getContext()).setTitle("No amount")
					.setMessage("You have to specify an amount, e.g 0.")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int button){

						}

					}).show();

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
				.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).setNeutralButton("Edit", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
						alert.setTitle("Edit option").
						setMessage("What do you want to do?");

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
									removeAttender(chosenevent, chosendate);
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
						final int finalcost=cost;
						final int nrOfAttenders = list.size(); 

						String att = "";
						for(String s : list){
							if(list.size()==1){
								att = att + s + "'s";
							}
							else if(list.indexOf(s) == list.size()-1){
								att = att +" and " +s+"'s";
							}
							else if(list.indexOf(s)==list.size()-2){
								att = att + s+"'s";
							}
							else{
								att = att  + s +"'s" + ", ";
							}

						}

						AlertDialog.Builder alert3 = new AlertDialog.Builder(view.getContext());
						String output = "Are you sure you want to delete the event '" + chosenevent + "'? This will decrease "+att+" debts by " + Integer.toString(cost/nrOfAttenders) + " kr each."; 
						alert3.setTitle("Confirm")
						.setMessage(output)
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							}
						})
						.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1){
								shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_WRITEABLE);

								Editor editor = shareddebts.edit();
								for(String s : list){
									editor.putInt(s, shareddebts.getInt(s,0)-finalcost/nrOfAttenders);
								}
								editor.commit();

								String where2 = Helper.colEventName +"=?";
								String[] args2 ={chosenevent};
								db.delete(Helper.TABLE_NAME, where2, args2);
								db.close();
							}
						}).show();

					}
				}).show();

			}

		}).show();

	}

	public void removeAttender(String eventname, String eventdate){

//		SQLiteDatabase db = helper.getWritableDatabase();
//
//		String[] columns = {Helper.colAttender};
//
//		String where = Helper.colEventName+"= ? AND " + Helper.colDate + "=?"; 
//
//		String[] whereargs = {eventname, eventdate};
//
//		Cursor cursor = db.query(Helper.TABLE_NAME, columns, where, whereargs, null, null, null);
//
//		final ArrayList<String> list = new ArrayList<String>();
//
//		while(cursor.moveToNext()){
//			list.add(cursor.getString(cursor.getColumnIndex(Helper.colAttender)));
//		}
//
//		CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
//
//		final ArrayList<String> selectedItems = new ArrayList<String>();
//
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		alert.setTitle("Choose attenders")
//		.setMessage("Who do you want to remove?")
//		.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {				
//			@Override
//			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//				if (isChecked) {
//					selectedItems.add(list.get(which));
//				} 
//				else {
//					selectedItems.remove(list.get(which));
//				}
//			}
//		})
//		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		}).setPositiveButton("Next", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//				String where = 
//				for(String s : selectedItems){
//					String whereargs =  
//							db.delete(Helper.TABLE_NAME, where, whereArgs);
//				}
//			}
//		}).show();

	}
	public void changeCost(String eventname, String eventdate){

	}
	public void changeName(String eventname, String eventdate){

	}
	public void changeDate(String eventname, String eventdate){

	}
}
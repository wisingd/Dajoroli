package com.example.splitit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class EventCreater extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	int year, month, day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_creater);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

//	private String getDateString() {
//		String message = Integer.toString(year) + "/" + Integer.toString(month) + "/" + Integer.toString(day);
//		return message;
//	}

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

	public void showDatePickerDialog(View v) {
		TextView value;
		value = (TextView) findViewById(R.id.textview2);
		//final EditText input = new EditText(this);

		MyDatePicker newFragment = new MyDatePicker();
		newFragment.show(getFragmentManager(), "datePicker");

		value = (TextView) findViewById(R.id.textview2);

		year = newFragment.getYear();
		month = newFragment.getMonth();
		day = newFragment.getDay();

		value.setText("" + day + "/" + month + "/" + year);
	}

	public void eventNameDialog(View view){

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Title");
		alert.setMessage("Message");

		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				TextView value;
				value = (TextView) findViewById(R.id.textview);
				String message = "" + input.getText();

				value.setText("" + message);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}


//	public void createEvent(View v){
//
//		Intent intent = new Intent(this, AddAttenders.class);
//		EditText editText = (EditText) findViewById(R.id.event_name);
//		String message = editText.getText().toString();
//
//		if(message.length() != 0){
//			intent.putExtra(EXTRA_MESSAGE, message);
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
//	}
}
package com.example.splitit;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class EventCreater extends ActionBarActivity {

	private static int eventyear;

	private static int eventmonth;

	private static int eventday;

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


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

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_creater, menu);
		return true;
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
//	public void showDatePickerDialog(View v) {
//		DialogFragment newFragment = new DatePickerFragment();
//		newFragment.show(getFragmentManager(), "datePicker");
//		;
//	}

	public static void setDate(int year, int month, int day){
		eventyear = year;

		eventmonth = month;

		eventday = day;

	}

	public void createEvent(View v){
		Intent intent = new Intent(this, AddAttenders.class);

		EditText editText = (EditText) findViewById(R.id.event_name);

		String message = editText.getText().toString();

		if(message.length() != 0){

			intent.putExtra(EXTRA_MESSAGE, message);
			startActivity(intent);
		}
		else{
			new AlertDialog.Builder(this).setTitle("No name").setMessage("You did not enter a valid name.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}
	//	public static class DatePickerFragment  extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	//		
	//
	//		@Override
	//		public Dialog onCreateDialog(Bundle savedInstanceState) {
	//			// Use the current date as the default date in the picker
	//			final Calendar c = Calendar.getInstance();
	//			int year = c.get(Calendar.YEAR);
	//			int month = c.get(Calendar.MONTH);
	//			int day = c.get(Calendar.DAY_OF_MONTH);
	//
	//			// Create a new instance of DatePickerDialog and return it
	//			return new DatePickerDialog(getActivity(), this, year, month, day);
	//		}
	//
	//		public void onDateSet(DatePicker view, int year, int month, int day) {
	//			EventCreater.setDate(year, month, day);
	//		}
	//	}
}

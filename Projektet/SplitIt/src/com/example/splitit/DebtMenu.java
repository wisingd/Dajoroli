package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

public class DebtMenu extends ActionBarActivity implements OnItemSelectedListener{

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static String BOOLEAN_MESSAGE = "com.example.myfirstapp.trueorfalse";
	public final static String ANOTHER_MESSAGE = "com.example.myfirstapp.contactlist";

	public SharedPreferences sharednames;
	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyNames = "Mynames";
	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	private Spinner spinner;
	private String selectedName;

	String listString = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
	}

	public void theyOweMe(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		if (mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();

			// DIALOGRUTA 1
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Who owes you?")
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
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			// OK-button for Dialog 1
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					final EditText input = new EditText(view.getContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setHint("Debt");

					// DIALOG 2
					new AlertDialog.Builder(view.getContext())
					.setTitle("Enter the total debt")
					.setView(input)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})
					// OK-button for Dialog 2
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							final String value = "" + input.getText();
							final int debtAmount = Integer.parseInt(value);
							listString = "Do you want to assign the total debt " + value + "kr (" + debtAmount/selectedItems.size() + " kr each) to the following contacts?";

							for (String s :selectedItems){
								listString = listString + "\n" + s;
							}
							// DIALOG 3
							new AlertDialog.Builder(view.getContext())
							.setTitle("Confirm:")
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							})
							// OK-button for Dialog 3
							.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
									Editor editor = shareddebts.edit();

									for (String s :selectedItems){

										int olddebt = shareddebts.getInt(s, 0);
										int newdebt = olddebt + debtAmount / selectedItems.size();
										editor.putInt(s, newdebt);
										editor.commit();
									}
								}
							})
							.setMessage("" + listString)
							.show(); // Dialog 3
						}
					})
					.show(); // Dialog 2
				}
			})
			.show(); // Dialog 1
		} // IF

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts yet.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}

	public void iOweThem(final View view){

		final ArrayList<String> list = new ArrayList<String>();
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		if (mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();

			// DIALOGRUTA 1
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle("Who do you owe?")
			.setItems(cs, new DialogInterface.OnClickListener() {
				public void onClick (DialogInterface dialog, int which) {

					final int position = which;
					final EditText input = new EditText(view.getContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setHint("Debt");

					// DIALOG 2
					new AlertDialog.Builder(view.getContext())
					.setTitle("Enter debt to " + list.get(position))
					.setView(input)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})

					// OK-button for Dialog 2
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = "" + input.getText();
							final int debtAmount = Integer.parseInt(value);
							final String name = list.get(position);
							String listString = "Do you want to add a debt of " + value + " kr to " + name + "?";

							// DIALOG 3
							new AlertDialog.Builder(view.getContext())
							.setTitle("Confirm:")
							.setMessage("" + listString)
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							})

							// OK-button for Dialog 3
							.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Change the debt in SharedPreferences
									shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
									Editor editor = shareddebts.edit();
									int olddebt = shareddebts.getInt(name, 0);
									int newdebt = olddebt - debtAmount;
									editor.putInt(name, newdebt);
									editor.commit();
								}
							})
							.show(); // Dialog 3
						}
					})
					.show(); // Debt
				}
			})
			.show();// SetItems

		} // IF

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts yet.")
			.setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}// Metod

	public void deleteDebt(final View view){
	}

	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
	}

	public void viewContacts(View view){
		Intent intent = new Intent(this, ContactViewing.class);
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		startActivity(intent);
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		selectedName = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
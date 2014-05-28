package com.example.splitit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Toast;

public class DebtMenu extends ActionBarActivity implements OnItemSelectedListener{


	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	String listString = "";

	int newdebt;
	int olddebt;

	public ArrayList<String> getContactList(){
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		Map<String,?> mappen = shareddebts.getAll();
		ArrayList<String> list = new ArrayList<String>();
		Set<String> settet = mappen.keySet();

		if (mappen.size() > 0){
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}
			return list; 
		}
		else
			return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
	}


	public void whoOwesYou(final View view){
		final ArrayList<String> list = getContactList();

		if (list == null)
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts yet. Add one in <i>Contacts</i>"), Toast.LENGTH_LONG).show();
		else {
			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Who owes you?")
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
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			.setPositiveButton("Next", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					if (selectedItems.size() == 0){
						Toast.makeText(view.getContext(), "You have to choose at least one contact.", Toast.LENGTH_LONG).show();
						whoOwesYou(view);
					}

					else {
						enterDebt(view, selectedItems);
					}
				}
			})
			.show();
		}
	}

	public void enterDebt(final View view, ArrayList<String> selectedItems){

		final EditText input = new EditText(view.getContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("Debt");
		final ArrayList<String> selectedContacts = selectedItems;

		// DIALOG 2
		new AlertDialog.Builder(view.getContext())
		.setTitle("Enter the total debt")
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "Debts canceled.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Next", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				final String value = "" + input.getText();

				if (value.length() > 0){
					final int debtAmount = Integer.parseInt(value);
					listString = "Do you want to assign the total debt " + value + " kr (" + debtAmount/selectedContacts.size() + " kr each) to the following contacts?";

					for (String s :selectedContacts){
						listString = listString + "\n" + s;
					}

					confirmTheyOweMe(view, selectedContacts, debtAmount);

				}
				else{
					Toast.makeText(view.getContext(), "You have to enter a debt.", Toast.LENGTH_LONG).show();
					enterDebt(view, selectedContacts);
				}
			}
		})
		.show();
	}


	public void confirmTheyOweMe(final View view, ArrayList<String> selectedItems, int debt){

		final ArrayList<String> selectedContacts = selectedItems;
		final int debtAmount = debt;
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, MODE_PRIVATE);

		final Editor editor = shareddebts.edit();

		final ArrayList<String> numberList = new ArrayList<String>();


		new AlertDialog.Builder(view.getContext())
		.setTitle("Confirm:")
		.setMessage("" + listString)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "OK, debt canceled.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				String numberNames = "";

				for (String s :selectedContacts){
					String number = sharednumber.getString(s, null);
					olddebt = shareddebts.getInt(s, 0);
					newdebt = olddebt + debtAmount / selectedContacts.size();
					editor.putInt(s, newdebt);
					editor.commit();

					if (number != null){
						numberList.add(s);
					}
				}

				if (numberList.size() > 0) {
					for (String s : numberList){
						String number = sharednumber.getString(s, null);
						numberNames = numberNames + "\n" + s + " (" + number + ")";
					}

					notifyTheyOweMeSMS(view, numberNames, numberList, selectedContacts, debtAmount);
				}
				
				else {
					Toast.makeText(view.getContext(), "Debt added!", Toast.LENGTH_LONG).show();
				}
			}
		})
		.show();
	}
	/**
	 * 
	 * @param view
	 * @param listOfNamesWithNumber A list of all the names that have an associated number
	 * @param listOfNumbers A list of all the numbers
	 * @param selectedItems A list of all the selected contacts
	 * @param debt The debt
	 */
	public void notifyTheyOweMeSMS(final View view, String listOfNamesWithNumber, ArrayList<String> listOfNumbers, ArrayList<String> selectedItems, int debt){

		String numberNames = listOfNamesWithNumber;
		final ArrayList<String> numberList = listOfNumbers;
		final ArrayList<String> selectedContacts = selectedItems;
		final int debtAmount = debt;

		new AlertDialog.Builder(view.getContext())
		.setTitle("Notify")
		.setMessage("Would you like to notify \n" + numberNames + "\n\nby sending an SMS?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "OK, no SMS sent but the debts have been added.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				for(String s : numberList){
					if (newdebt > 0){
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() + " kr to you in Split It."
										+ "\n\nIn total, you now owe me " + Math.abs(newdebt) + " kr.");
					}
					else if (newdebt < 0){
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() + " kr to you in Split It."
										+ "\n\nIn total, I now owe you " + Math.abs(newdebt) + " kr.");
					}
					else {
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() + " kr to you in Split It."
										+ "\n\nGuess what? We are now even!");
					}
					Toast.makeText(view.getContext(), "SMS have been sent and the debts have been added.", Toast.LENGTH_LONG).show();
				}
			}
		})
		.show();
	}

	public void whoDoYouOwe(final View view){

		final ArrayList<String> list = getContactList();

		if (list == null)
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts yet. Add one in <i>Contacts</i>"), Toast.LENGTH_LONG).show();

		else {
			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle("Who do you owe?")
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(view.getContext(), "Canceled.", Toast.LENGTH_SHORT).show();
				}
			})
			.setItems(cs, new DialogInterface.OnClickListener() {
				public void onClick (DialogInterface dialog, int which) {

					whatDoYouOwe(view, which, list);

				}
			})
			.show();
		}
	}

	public void whatDoYouOwe(final View view, final int which, final ArrayList<String> list){

		final int position = which;
		final ArrayList<String> contacts = list;
		final String name = contacts.get(position);

		final EditText input = new EditText(view.getContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("Debt");

		new AlertDialog.Builder(view.getContext())
		.setTitle("Enter debt to " + name)
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "Canceled.", Toast.LENGTH_SHORT).show();
			}
		})

		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = "" + input.getText();
				if (value.length() > 0){
					final int debtAmount = Integer.parseInt(value);
					final String name = list.get(position);

					confirmIOweThem(view, name, debtAmount);
				}
				else{
					Toast.makeText(view.getContext(), "You have to enter a debt.", Toast.LENGTH_LONG).show();
					whatDoYouOwe(view, which, contacts);
				}
			}
		})
		.show();
	}

	public void confirmIOweThem(final View view, String debtName, int debt){

		final String name = debtName;
		final int debtAmount = debt;

		String listString = "Do you want to add a debt of " + debtAmount + " kr to " + name + "?";

		new AlertDialog.Builder(view.getContext())
		.setTitle("Confirm:")
		.setMessage("" + listString)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "OK, debts canceled.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Change the debt in SharedPreferences
				shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
				Editor editor = shareddebts.edit();
				olddebt = shareddebts.getInt(name, 0);
				newdebt = olddebt - debtAmount;
				editor.putInt(name, newdebt);
				editor.commit();

				sharednumber = getSharedPreferences(MyNumbers, MODE_PRIVATE);
				String number = sharednumber.getString(name, null);

				if (number != null) {

					notifyOwingSMS(view, name, debtAmount);

				}

				else
					Toast.makeText(view.getContext(), "The debt has been added.", Toast.LENGTH_LONG).show();
			}
		})
		.show();
	}

	public void notifyOwingSMS(final View view, String names, int debt){

		final String name = names;
		final int debtAmount = debt;

		new AlertDialog.Builder(view.getContext())
		.setTitle("Notify")
		.setMessage("Would you like to notify " + name + " (" + sharednumber.getString(name, null) + ") by sending an SMS?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "OK, no SMS has been sent but the debt has been added.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (newdebt > 0){
					sendSMS(sharednumber.getString(name, null), 
							"Hello " + name + "! I have added that I have a debt of " + Math.abs(debtAmount) + " kr to you in Split It."
									+ "\n\nIn total, you now owe me " + newdebt + " kr.");
				}
				else if (newdebt < 0){
					sendSMS(sharednumber.getString(name, null), 
							"Hello " + name + "! I have added that I have a debt of " + Math.abs(debtAmount) + " kr to you in Split It."
									+ "\n\nIn total, I now owe you " + newdebt + " kr.");
				}
				else {
					sendSMS(sharednumber.getString(name, null), 
							"Hello " + name + "! I have added that I have a debt of " + Math.abs(debtAmount) + " kr to you in Split It."
									+ "\n\nGuess what? We are now even!");
				}

				Toast.makeText(view.getContext(), "SMS has been sent and the debt has been added.", Toast.LENGTH_LONG).show();
			}
		})
		.show();
	}

	public void evenDebt(final View view){

		final ArrayList<String> list = getContactList();

		if (list == null)
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any friends, so you can not even any debts. Add a new one in <i>Contact</i>"), Toast.LENGTH_LONG).show();

		else {
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
					else {
						selectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(view.getContext(), "Cancel, no debts have been evened.", Toast.LENGTH_LONG).show();
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for (String s: selectedItems){
						listString = listString + "\n" + s;
					}
					new AlertDialog.Builder(view.getContext())
					.setTitle("Confirm")
					.setMessage("Are you sure you want to even your debt situation with the following contacts? " + listString)
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Change the debt in SharedPreferences
							shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
							Editor editor = shareddebts.edit();

							for(String s: selectedItems){
								editor.putInt(s, 0);
								editor.commit();
							}
						}
					})
					.show();
				}
			})
			.show();
		}// IF

	}//METHOD

	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
	}

	public String getContactDebts(View view){
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);
		Map<String,?> mappen = shareddebts.getAll();
		Set<String> settet = mappen.keySet();
		Iterator <String> iteratorn = settet.iterator();
		String message = "";


		if (mappen.size() == 0)
			return null;

		else{
			List<String> iOweThese = new ArrayList<String>();
			List<String> theseOweMe = new ArrayList<String>();
			List<String> evenWithThese = new ArrayList<String>();		

			while(iteratorn.hasNext()){

				String key = iteratorn.next(); 

				if( shareddebts.getInt(key, 0) > 0)
					theseOweMe.add(key);
				else if(shareddebts.getInt(key,0) == 0)
					evenWithThese.add(key);
				else
					iOweThese.add(key);
			}

			Collections.sort(iOweThese);
			Collections.sort(theseOweMe);
			Collections.sort(evenWithThese);



			if (iOweThese.size() != 0){
				message = "You have a debt to these contacts:";

				for(String temp: iOweThese){
					message = message + "\n" + temp + " (" + sharednumber.getString(temp, "--") + ") " + Integer.toString(Math.abs(shareddebts.getInt(temp, 0))) + " kr.";
				}
				message = message + "\n\n";
			}
			if (theseOweMe.size() != 0){
				message = message +  "These contacts have a debt to you:";

				for(String temp: theseOweMe){
					message = message + "\n" + temp + " (" + sharednumber.getString(temp, "--") + ") " + Integer.toString(shareddebts.getInt(temp, 0)) + " kr.";
				}
				message = message +"\n\n";
			}
			if (evenWithThese.size() != 0){
				message = message + "You are even with these contacts:";

				for(String temp: evenWithThese){
					message = message + "\n"  + temp + " (" + sharednumber.getString(temp, "--") + ") ";
				}
				message = message + "\n";
			}
		}
		return message;
	}

	public void viewContacts(final View view){

		String message = getContactDebts(view);
		if (message == null){
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts. Add a new in <i>'Contacts'</i>"), Toast.LENGTH_LONG).show();
		}
		else {
			new AlertDialog.Builder(view.getContext())
			.setTitle("All contacts")
			.setMessage("" + message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
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
		//String selectedName = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
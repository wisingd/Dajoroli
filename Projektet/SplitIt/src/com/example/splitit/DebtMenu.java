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
/**
 * An activity that handles debts to contacts.
 *
 */
public class DebtMenu extends ActionBarActivity implements OnItemSelectedListener{


	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	String listString = "";
	String number = "";

	int newdebt;
	int olddebt;


	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
	}


	/**
	 * Returns an ArrayList<String> of contacts that is contained within the SharedPreference 'shareddebts'.
	 * @return
	 * Returns every key stored in  the SharedPreferences shareddebts by getting its keys
	 * as a Set<String> and running through it with an Iterator.
	 * @return A list of all the contacts
	 */
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

	public void theyOweMe(View view){
		chooseContacts(view, true);
	}

	public void iOweThem(View view){
		chooseContacts(view, false);
	}

	/**
	 * Initial Dialog, where the user picks contacts.
	 * @param view
	 * @param bool True if "They Owe Me" is pressed, false if "I Owe Them" is pressed.
	 */
	public void chooseContacts(final View view, final boolean bool){

		final ArrayList<String> list = getContactList();

		if (list == null)
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts yet. Add one in <i>Contacts</i>"), Toast.LENGTH_LONG).show();

		else {
			final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

			if (bool){
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
							chooseContacts(view, true);
						}
						else {
							enterDebt(view, selectedItems, bool);
						}
					}
				})
				.show();
			}

			else {
				alert.setTitle("Who do you owe?")
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(view.getContext(), "Canceled.", Toast.LENGTH_SHORT).show();
					}
				})
				.setItems(cs, new DialogInterface.OnClickListener() {
					public void onClick (DialogInterface dialog, int which) {

						selectedItems.add(list.get(which));
						enterDebt(view, selectedItems, bool);
					}
				})
				.show();
			}
		}
	}

	/**
	 * This method creates a Dialog where the user enter the debt, even if it is the user who owes it or other persons owing the user.
	 * 
	 * @param selectedItems The selected contacts.
	 * @param bool True if "They Owe Me" is pressed, false if "I Owe Them" is pressed.
	 */
	public void enterDebt(final View view, final ArrayList<String> selectedItems, final boolean bool){

		final EditText input = new EditText(view.getContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("Debt");

		new AlertDialog.Builder(view.getContext())
		.setTitle("Enter total debt")
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "Debts canceled.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Next", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				final String value = "" + input.getText();
				final int debtAmount = Integer.parseInt(value);

				if (value.length() > 0) {

					confirmDebt(view, selectedItems, debtAmount, bool);
				}
				else {
					Toast.makeText(view.getContext(), "You have to enter a debt.", Toast.LENGTH_LONG).show();
					enterDebt(view, selectedItems, true);
				}
			}
		})
		.show();
	}

	/**
	 * This methods creates a Dialog where the user confirms names and debt.
	 * 
	 * @param selectedItems The selected contacts.
	 * @param debt The entered debt.
	 * @param bool True if "They Owe Me" is pressed, false if "I Owe Them" is pressed.
	 */

	public void confirmDebt(final View view, final ArrayList<String> selectedItems, final int debt, final boolean bool){

		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, MODE_PRIVATE);

		final Editor editor = shareddebts.edit();
		final ArrayList<String> numberList = new ArrayList<String>();

		if (bool) 
			listString = "You're about to add a debt of " + debt/selectedItems.size() + " kr to " 
					+ Miscellaneous.listToPrettyString(selectedItems, false) + ". Is this correct?"; 

		else
			listString = "Do you want to add a debt of " + debt + " kr to " + Miscellaneous.listToPrettyString(selectedItems, false) + "?";


		new AlertDialog.Builder(view.getContext())
		.setTitle("Confirmation")
		.setMessage("" + listString)
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(view.getContext(), "OK, debt canceled.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (bool){
					for (String s :selectedItems){
						number = sharednumber.getString(s, null);
						olddebt = shareddebts.getInt(s, 0);
						newdebt = olddebt + debt / selectedItems.size();
						editor.putInt(s, newdebt);
						editor.commit();

						if (number != null){
							numberList.add(s);
						}
					}
				}

				else {
					number = sharednumber.getString(selectedItems.get(0), null);
					olddebt = shareddebts.getInt(selectedItems.get(0), 0);
					newdebt = olddebt - debt;
					editor.putInt(selectedItems.get(0), newdebt);
					editor.commit();

					if (number != null){
						numberList.add(selectedItems.get(0));
					}
				}

				if (numberList.size() > 0) {
					String numberNames="";
					for (String s : numberList){
						String number = sharednumber.getString(s, null);
						numberNames = numberNames + "\n" + s + " (" + number + ")";
					}

					notifySMS(view, numberNames, numberList, selectedItems, debt);
				}

				else {
					Toast.makeText(view.getContext(), "Debt added!", Toast.LENGTH_LONG).show();
				}
			}
		})
		.show();
	}

	/**
	 * The method creates the String that is to be sent in the SMS and then sends the SMS. 
	 * 
	 * @param listOfNamesWithNumber A list of all the names that have an associated number
	 * @param listOfNumbers A list of all the numbers
	 * @param selectedItems A list of all the selected contacts
	 * @param debt The debt
	 */
	public void notifySMS(final View view, String listOfNamesWithNumber, final ArrayList<String> numberList, final ArrayList<String> selectedContacts, final int debtAmount){

		new AlertDialog.Builder(view.getContext())
		.setTitle("Notify")
		.setMessage("Would you like to notify \n" + listOfNamesWithNumber + "\n\nby sending an SMS?")
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "OK, no SMS sent but the debts have been added.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				for (String s : numberList){
					newdebt = shareddebts.getInt(s, 0);
					if (newdebt > 0){
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() 
								+ " kr to you in Split It." + "\n\nIn total, you now owe me " + Math.abs(newdebt) + " kr.");
					}
					else if (newdebt < 0){
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() 
								+ " kr to you in Split It."	+ "\n\nIn total, I now owe you " + Math.abs(newdebt) + " kr.");
					}
					else {
						sendSMS(sharednumber.getString(s, null), 
								"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedContacts.size() 
								+ " kr to you in Split It." + "\n\nGuess what? We are now even!");
					}

					Toast.makeText(view.getContext(), "SMS have been sent and the debt have been added.", Toast.LENGTH_LONG).show();
				}
			}
		})
		.show();
	}

	/**
	 * This method evens a debt, that is setting the total debt amongst contacts to zero (0). 
	 */
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

					listString = Miscellaneous.listToPrettyString(selectedItems, false);

					new AlertDialog.Builder(view.getContext())
					.setTitle("Confirm")
					.setMessage("You are about to even your debt situation with " + listString + ". Is this correct?")
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
		}

	}

	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
	}

	/**
	 * The method creates a String of all the lists containing all the contacts and their debts.
	 * @return A list with all contacts and their respective debts.
	 */
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

	/**
	 * Opens a Dialog with all the contacts and their respective debts.
	 */
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
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
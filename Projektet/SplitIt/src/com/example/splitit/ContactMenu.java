package com.example.splitit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The starting menu of the contact management. From here you can add contacts, view contacts debts and delete contacts. 
 */
public class ContactMenu extends ActionBarActivity {

	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";
	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	String listString="";

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.contact_view);
	}

	/**
	 * The method allows the user to enter a name of the new contact
	 */
	public void writeName(final View view){
		shareddebts = getSharedPreferences(MyDebts, MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, MODE_PRIVATE);

		final EditText input = new EditText(view.getContext());
		input.setHint("Name of your new friend");


		new AlertDialog.Builder(view.getContext())
		.setTitle("Name")
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		})
		.setPositiveButton("Next", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if (input.length() == 0){
					Toast.makeText(view.getContext(), "You have to enter a name!", Toast.LENGTH_LONG).show();
					writeName(view);
				}

				else{
					String name = "" + input.getText();
					addNumber(view, name);
				}
			}
		})
		.show();
	}
	
	/**
	 * The method allows users to add a number to a contact
	 * @param name The name of the contact
	 */
	public void addNumber(final View view, final String name){

		new AlertDialog.Builder(view.getContext())
		.setTitle("Phone number")
		.setMessage(Html.fromHtml("Would you like to add a phone number to <b>" + name + "</b>, so you can notify your new friend when you add a debt?"))
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmContact(view, null, name);
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				writeNumber(view, name);
			}
		})
		.show();
	}

	/**
	 * The method opens an inputdialog in which the user can enter the number of the new contact.
	 */
	public void writeNumber(final View view, final String name){
		final EditText input = new EditText(view.getContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);						
		input.setHint(name + "'s phone number");

		new AlertDialog.Builder(view.getContext())
		.setTitle("Phone number")
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				addNumber(view, name);
			}
		})
		.setPositiveButton("Next", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String number = "" + input.getText();

				if (number.length() == 0){
					Toast.makeText(view.getContext(), "You have to enter a number!", Toast.LENGTH_LONG).show();
					writeNumber(view, name);
				}
				else
					confirmContact(view, number, name);
			}
		})
		.show();
	}
	/**
	 * A confirmation where the user can confirm name and potentially the number.
	 */
	public void confirmContact(final View view, String number, final String name){
		final Editor editor = sharednumber.edit();
		final Editor editor2 = shareddebts.edit();
		String value = null;
		String str = ""; 


		if (number == null){
			str = ", without a number,";
		}

		else {
			value = number;
			str = ", with the number " + number + ",";
		}

		final String value2 = value;
		String msg = "Are you sure you want to add " + name + str + " to your list of contacts?";

		new AlertDialog.Builder(view.getContext())
		.setTitle("Confirm")
		.setMessage(msg)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editor.putString(name, value2);
				editor.commit();

				editor2.putInt(name, 0);
				editor2.commit();

				Toast.makeText(view.getContext(), "OK, contact added!", Toast.LENGTH_LONG).show();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "OK, contact not added.", Toast.LENGTH_LONG).show();
			}
		})
		.show();
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
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts. Add a new one by pressing <i>'New...'</i>"), Toast.LENGTH_LONG).show();
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
	
	/**
	 * Opens a dialog where the user can choose to erase or edit contacts.
	 */
	public void editContact(final View view){

		final ArrayList<String> list = getContactList();

		if (list == null)
			Toast.makeText(view.getContext(), Html.fromHtml("You do not have any contacts yet. Add one in <i>Contacts</i>"), Toast.LENGTH_LONG).show();

		else {
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle("Choose")
			.setMessage("What would you like to do?")

			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setNeutralButton("Erase contacts", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AlertDialog.Builder(view.getContext())
					.setTitle("Erasing contacts")
					.setMessage("Would you like to delete all contacts or just some of them?")
					.setPositiveButton("Erase all", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							eraseAllContacts(view);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							Toast.makeText(view.getContext(), Html.fromHtml("OK, no contacts erased!"), Toast.LENGTH_LONG).show();
						}
					})
					.setNeutralButton("Choose", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							eraseSomeContacts(view, list);
						}
					})
					.show();
				}
			})
			.setPositiveButton("Edit number", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chooseNumberContact(view, list);
				}
			})
			.show();
		}
	}
	
	
	/**
	 * The dialog in which the user choose which contact to edit.
	 * @param list List of all contacts.
	 */
	public void chooseNumberContact(final View view, final ArrayList<String> list){
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);

		for(String s : list){
			String number = sharednumber.getString(s, "--");
			list.set(list.indexOf(s), s + " (" + number + ")");
		}

		final CharSequence[] cs = list.toArray(new CharSequence[list.size()]);

		new AlertDialog.Builder(view.getContext())
		.setTitle("Who's number would you like to change?")
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "Canceled, nothing edited.", Toast.LENGTH_LONG).show();
			}
		})
		.setItems(cs, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final int position = which;
				editNumber(view, position, list);
			}
		})
		.show();
	}

	/**
	 * The method in which the new number is entered and the old number changed.
	 * @param position To keep track of which contact is chosen from chooseNumberContact()
	 * @param list List of all contacts.
	 */
	public void editNumber(final View view, final int position, final ArrayList<String> list){

		final EditText input = new EditText(view.getContext());
		input.setHint("Number");
		input.setInputType(InputType.TYPE_CLASS_NUMBER);

		new AlertDialog.Builder(view.getContext())
		.setTitle("Change number")
		.setView(input)
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), "Canceled, nothing edited.", Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Change", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				final String newNumber = "" + input.getText();

				if (newNumber.length() == 0) {
					Toast.makeText(view.getContext(), "You have to enter a number!", Toast.LENGTH_LONG).show();
					editNumber(view, position, list);
				}

				else {
					String name = list.get(position);

					new AlertDialog.Builder(view.getContext())
					.setTitle("Confirm")
					.setMessage("Do you want to change " +  name + "'s number to " + newNumber + "?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Editor editor = sharednumber.edit();
							editor.putString(list.get(position), newNumber);
							editor.commit();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(view.getContext(), "OK, nothing edited.", Toast.LENGTH_LONG).show();
						}
					})
					.show();
				}
			}
		})
		.show();
	}

	/**
	 * The method erases all contacts, with a confirmation dialog first.
	 */
	public void eraseAllContacts(final View view){

		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

		new AlertDialog.Builder(view.getContext())
		.setTitle("Erase contacts")
		.setMessage("Are you sure you want to erase all your contacts?")
		.setNegativeButton("No", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				Toast.makeText(view.getContext(), Html.fromHtml("OK, no contacts erased."), Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
				sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);

				Editor editor = shareddebts.edit();
				editor.clear();
				editor.commit();

				Editor editor2 = sharednumber.edit();
				editor2.clear();
				editor2.commit();

				Toast.makeText(view.getContext(), Html.fromHtml("All contacts have been erased!"), Toast.LENGTH_LONG).show();
			}
		}).show();
	}
	
	/**
	 * A method where the user gets to choose which contacts to erase.
	 * @param list List of all contacts
	 */
	
	public void eraseSomeContacts(final View view, final ArrayList<String> list){

		shareddebts = getSharedPreferences(MyDebts, MODE_PRIVATE);

		CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
		final ArrayList<String> selectedItems = new ArrayList<String>();

		new AlertDialog.Builder(view.getContext())
		.setTitle("Who do you want to erase?")
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
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(view.getContext(), Html.fromHtml("OK, no contacts erased."), Toast.LENGTH_LONG).show();
			}
		})
		.setPositiveButton("Erase", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (selectedItems.size() == 0){
					Toast.makeText(view.getContext(), Html.fromHtml("You have to choose at least one contact!"), Toast.LENGTH_LONG).show();
					eraseSomeContacts(view, list);
				}
				else {

					new AlertDialog.Builder(view.getContext())
					.setTitle("Confirm")
					.setMessage("Are you sure you want to delete " + Miscellaneous.listToPrettyString(selectedItems, false) 
							+ " from your list of contacts?")
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Toast.makeText(view.getContext(), Html.fromHtml("OK, no contacts erased."), Toast.LENGTH_LONG).show();
								}
							})
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									for(String s : selectedItems){
										Editor editor = shareddebts.edit();
										editor.remove(s);
										editor.commit();

										Editor editor2 = sharednumber.edit();
										editor2.remove(s);
										editor2.commit();

										Toast.makeText(view.getContext(), Html.fromHtml("OK, the selected contacts have been erased."), Toast.LENGTH_LONG).show();
									}
								}
							})
							.show();
				}
			}
		})
		.show();
	}

	/**
	 * This method loops through the SharedPreference shareddebts and creates a list if it
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
}

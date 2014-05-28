package com.example.splitit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

	public void addContact(final View view){
		shareddebts = getSharedPreferences(MyDebts, MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, MODE_PRIVATE);

		final Editor editor = sharednumber.edit();
		final Editor editor2 = shareddebts.edit();

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
					new AlertDialog.Builder(view.getContext())
					.setTitle("No name")
					.setMessage("You have to enter a name!")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.show();
				}

				else {
					final String value = "" + input.getText();
					new AlertDialog.Builder(view.getContext())
					.setTitle("Phone number")
					.setMessage(Html.fromHtml("Would you like to add a phone number to <b>" + value + "</b>, so you can notify your new friend when you add a debt?"))
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new AlertDialog.Builder(view.getContext())
							.setTitle("Confirm")
							.setMessage("Would you like to add " + value + " to your list of contacts, without a phone number?")
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									editor.putString(value, null);
									editor.commit();

									editor2.putInt(value, 0);
									editor2.commit();

									new AlertDialog.Builder(view.getContext())
									.setTitle("New contact added")
									.setMessage("You have added " + value + " to your list of contacts!")
									.setNegativeButton("OK", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.setPositiveButton("View contacts", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											viewContacts(view);
										}
									})
									.show();
								}
							})
							.show();
						}
					})
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							final EditText input2 = new EditText(view.getContext());
							input2.setInputType(InputType.TYPE_CLASS_NUMBER);						
							input2.setHint(value + "'s phone number");

							new AlertDialog.Builder(view.getContext())
							.setTitle("Phone number")
							.setView(input2)
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.setPositiveButton("Next", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									final String number = "" + input2.getText();
									new AlertDialog.Builder(view.getContext())
									.setTitle("Confirm")
									.setMessage("Would you like to add " + value + " with the number " + number + " to your list of contacts?")
									.setNegativeButton("No", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
										}
									})
									.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											editor.putString(value, number);
											editor.commit();

											editor2.putInt(value, 0);
											editor2.commit();

											new AlertDialog.Builder(view.getContext())
											.setTitle("New contact added")
											.setMessage("You have added " + value + " to your list of contacts!")
											.setNegativeButton("OK", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
												}
											})
											.setPositiveButton("View contacts", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													viewContacts(view);
												}
											})
											.show();
										}
									})
									.show();
								}
							})
							.show();
						}
					})
					.show();
				}
			}
		})
		.show();
	}

	public void viewContacts(View view){

		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);
		Map<String,?> mappen = shareddebts.getAll();
		Set<String> settet = mappen.keySet();
		Iterator <String> iteratorn = settet.iterator();


		if (mappen.size() > 0) {

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

			String message = "";

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

			new AlertDialog.Builder(this)
			.setTitle("All contacts")
			.setMessage("" + message)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}

		else {
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts yet.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
				}
			})
			.setNegativeButton("Add a contact", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//ContactMenu.addContact(view));
				}
			})
			.show();
		}
	}

	public void editContact(final View view){

		AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

		alert.setTitle("Choose")
		.setMessage("What would you like to do?")

		// CANCEL
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})

		// ERASE
		.setNeutralButton("Erase contacts", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new AlertDialog.Builder(view.getContext())
				.setTitle("Erasing contacts")
				.setMessage("Would you like to delete all contacts or choose some?")
				// ERASE ALL
				.setPositiveButton("Erase all", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						final ArrayList<String> list = new ArrayList<String>();
						shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
						Map<String,?> mappen = shareddebts.getAll();

						if (mappen.size() > 0){
							Set<String> settet = mappen.keySet();
							Iterator <String> iteratorn = settet.iterator();
							while(iteratorn.hasNext()){
								String string  = iteratorn.next();
								list.add(string);
							}
							new AlertDialog.Builder(view.getContext())
							.setTitle("Erase contacts")
							.setMessage("Are you sure you want to erase all your contacts?")
							.setNegativeButton("No", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int which){
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

									new AlertDialog.Builder(view.getContext())
									.setTitle("Contacts erased!")
									.setMessage("You have erased all of your contacts!")
									.setPositiveButton("OK", new DialogInterface.OnClickListener(){
										public void onClick(DialogInterface dialog, int which){
											return;
										}
									}).show();
								}
							}).show();
						}

						else{
							new AlertDialog.Builder(view.getContext())
							.setTitle("No friends :( ")
							.setMessage("You do not have any contacts yet.")
							.setPositiveButton("OK", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int which){
									return;
								}
							})
							.show();
						}
					}
				}) 


				.setNegativeButton("Choose", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						final ArrayList<String> list = new ArrayList<String>();
						shareddebts = getSharedPreferences(MyDebts, MODE_PRIVATE);
						Map<String,?> mappen = shareddebts.getAll();

						if (mappen.size() > 0){
							Set<String> settet = mappen.keySet();
							Iterator <String> iteratorn = settet.iterator();
							while(iteratorn.hasNext()){
								String string  = iteratorn.next();
								list.add(string);
							}

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
								}
							})
							.setPositiveButton("Erase", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String str = "";
									for (String s : selectedItems){
										str = str + "\n" + s;
									}

									new AlertDialog.Builder(view.getContext())
									.setTitle("Confirm")
									.setMessage("Are you sure you want to delete " + str + "\n from your list of contacts?")
									.setNegativeButton("No", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
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
											}
										}
									})
									.show();
								}
							})
							.show();
						}//IF
						else{
							new AlertDialog.Builder(view.getContext())
							.setTitle("No friends :( ")
							.setMessage("You do not have any contacts yet.")
							.setPositiveButton("OK", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface dialog, int which){
									return;
								}
							})
							.show();
						}
					}
				}) 
				.show();
			}
		}) //ERASE 


		// EDIT 
		.setPositiveButton("Edit number", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final ArrayList<String> list = new ArrayList<String>();
				final ArrayList<String> list2 = new ArrayList<String>();
				shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
				sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);
				Map<String,?> mappen = shareddebts.getAll();


				if (mappen.size() > 0){
					Set<String> settet = mappen.keySet();
					Iterator <String> iteratorn = settet.iterator();
					while(iteratorn.hasNext()){
						String name  = iteratorn.next();
						list.add(name);
						String number = sharednumber.getString(name, "--");
						String str = name + " (" + number +")";
						list2.add(str);
					}
					final CharSequence[] cs = list2.toArray(new CharSequence[list2.size()]);

					// DIALOG 1
					new AlertDialog.Builder(view.getContext())
					.setTitle("Who's number would you like to change?")
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setItems(cs, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							final int position = which;
							final EditText input = new EditText(view.getContext());
							input.setHint("Number");
							input.setInputType(InputType.TYPE_CLASS_NUMBER);
							new AlertDialog.Builder(view.getContext())
							.setTitle("Change number")
							.setView(input)
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.setPositiveButton("Change", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									final String newNumber = "" + input.getText();
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
										}
									})
									.show();
								}
							})
							.show();
						}
					})
					.show();

				}//IF
				else{
					new AlertDialog.Builder(view.getContext())
					.setTitle("No friends :( ")
					.setMessage("You do not have any contacts yet.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							return;
						}
					})
					.show();
				}
			}
		})
		.show();

	}//Metod

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

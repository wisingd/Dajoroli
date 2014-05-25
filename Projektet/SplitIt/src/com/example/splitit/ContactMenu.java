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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
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

	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	String listString="";

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.contact_view);
	}

	public void addContact(final View view){
		shareddebts = getSharedPreferences(MyDebts, MODE_WORLD_READABLE);
		sharednumber = getSharedPreferences(MyNumbers, MODE_WORLD_READABLE);

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

				final String value = "" + input.getText();
				new AlertDialog.Builder(view.getContext())
				.setTitle("Phone number")
				.setMessage("Would you like to add a phone number to " + value + ", so you can notify your new friend when you add a debt?")
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
		})
		.show();
	}

	public void viewContacts(View view){

		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);

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
					message = message + "\n" + temp + "\t" + "\t" + Integer.toString(Math.abs(shareddebts.getInt(temp, 0))) + " kr.";
				}
				message = message + "\n\n";
			}
			if (theseOweMe.size() != 0){
				message = message +  "These contacts have a debt to you:";

				for(String temp: theseOweMe){
					message = message + "\n" + temp  + "\t" + "\t" + Integer.toString(shareddebts.getInt(temp, 0)) + " kr.";
				}
				message = message +"\n\n";
			}
			if (evenWithThese.size() != 0){
				message = message + "You are even with these contacts:";

				for(String temp: evenWithThese){
					message = message + "\n" + temp;
				}
				message = message + "\n";
			}

			new AlertDialog.Builder(this)
			.setTitle("Test")
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
			.setTitle("No friends :(")
			.setMessage("You don't seem to have any contacts yet.")
			.setNegativeButton("Add contact", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
		}
	}

	public void eraseContacts(final View view){

		new AlertDialog.Builder(this)
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

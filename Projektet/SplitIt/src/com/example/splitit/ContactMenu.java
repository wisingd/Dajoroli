package com.example.splitit;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * The starting menu of the contact management. From here you can add contacts, view contacts debts and delete contacts. 
 */
public class ContactMenu extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static String BOOLEAN_MESSAGE = "com.example.myfirstapp.trueorfalse";
	public final static String ANOTHER_MESSAGE = "com.example.myfirstapp.contactlist";
	
	public SharedPreferences sharednames;
	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyNames = "Mynames";
	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.contact_view);
	}

	public void addContact(View view) {

		String number="";
		EditText editText = (EditText) findViewById(R.id.contact_name);
		String message = editText.getText().toString();
		sharednames = getSharedPreferences(MyNames, MODE_WORLD_READABLE);

		if(message.length()>0){
			if(!sharednames.contains(message)){ 

				Editor editor = sharednames.edit();
				editor.putString(message, message);
				editor.commit();
			
				shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);

				Editor editor2 = shareddebts.edit();
				editor2.putInt(message, 0);
				editor2.commit();

				EditText edittext2 = (EditText) findViewById(R.id.phone_number);
				String numberstring = edittext2.getText().toString();

				if(numberstring.length() != 0){

					sharednumber = getSharedPreferences(MyNumbers, Context.MODE_WORLD_READABLE);
					
					Editor editor3 = sharednumber.edit();
					number = edittext2.getText().toString();
					editor3.putString(message, number);
					editor3.commit();

					new AlertDialog.Builder(this).setTitle("Update").setMessage("You have added " + message + " (" + number + ") as a contact!").setPositiveButton("Okidoki", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							return;
						}
					}).show();					
				}

				else{
					new AlertDialog.Builder(this).setTitle("Update").setMessage("You have added " + message + " as a contact!").setPositiveButton("Okidoki", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							return;
						}
					}).show();
				}

			}

			else{
				new AlertDialog.Builder(this).setTitle("Update").setMessage("You already have a contact with this name!").setPositiveButton("Okidoki", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).show();
			}
		}

		else{
			new AlertDialog.Builder(this).setTitle("Update").setMessage("You have to enter a name!").setPositiveButton("Okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}
	
	public void viewContacts(View view){

		Intent intent = new Intent(this, ContactViewing.class);
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		if(mappen.size() > 0){
			startActivity(intent);	
		}
		else{
			new AlertDialog.Builder(this).setTitle("No friends :( ").setMessage("You do not have any contacts.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}

	public void doYouReallyWantToEraseContacts(View view){
		new AlertDialog.Builder(this).setTitle("Erase contacts").setMessage("Do you really want to erase all contacts?").setPositiveButton("Yes", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				eraseContacts();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				return;
			}
		}).show();
	}

	public void eraseContacts(){

		sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
		sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);

		Editor editor = shareddebts.edit();
		editor.clear();
		editor.commit();
		Editor editor2 = sharednames.edit();
		editor2.clear();
		
		//		editor2.putString("---", "---");
		
		editor2.commit();
		Editor editor3 = sharednumber.edit();
		editor3.clear();
		editor3.commit();

		new AlertDialog.Builder(this).setTitle("Contacts erased!").setMessage("You have erased all of your contacts!").setPositiveButton("Okej", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				return;
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
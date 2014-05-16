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

	/** Called when the user clicks the "Create contact!"-button */
	public void addContact(View view) {

		String number="";

		Intent intent = new Intent(this, DisplayMessageActivity.class);

		EditText editText = (EditText) findViewById(R.id.contact_name);

		String message = editText.getText().toString();

		sharednames = getSharedPreferences(MyNames, MODE_WORLD_READABLE);

		if(!sharednames.contains(message) && message.length()>0){

			Editor editor =sharednames.edit();

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

			}

			intent.putExtra(BOOLEAN_MESSAGE, true);
		}
		if(number != ""){

			intent.putExtra(EXTRA_MESSAGE, message + " with the number " + number);

		}
		else{
			intent.putExtra(EXTRA_MESSAGE, message);
		}

		startActivity(intent);

	}
	/** Called when the user clicks the other button */
	public void viewContacts(View view){

		Intent intent = new Intent(this, AnotherActivity.class);

		String contacts = "";

		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);

		Map<String,?> mappen = sharednames.getAll();

		if(mappen.size() > 0){

			shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);

			Set<String> settet = mappen.keySet();

			Iterator <String> iteratorn = settet.iterator();

			String keyen = iteratorn.next();

			contacts = contacts + sharednames.getString(keyen, "") + " with the debt " + Integer.toString(shareddebts.getInt(keyen, 0));

			while(iteratorn.hasNext()){

				keyen = iteratorn.next();

				contacts =  contacts + "\n" + sharednames.getString(keyen, "") + " with the debt " + Integer.toString(shareddebts.getInt(keyen, 0));

			}
		}

		intent.putExtra(ANOTHER_MESSAGE, contacts);
		startActivity(intent);

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

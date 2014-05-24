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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
	}

	public void theyOweMeTwo(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = sharednames.getAll();

		if(mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

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
					else if (selectedItems.contains(which)) {
						selectedItems.remove(Integer.valueOf(which));
					}
				}
			});

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String listString = "";

					for (String s :selectedItems){
						listString = listString + s + "\n";
					}

					new AlertDialog.Builder(view.getContext()).setPositiveButton
					("Okilidokili", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setTitle("The Names:")
					.setMessage("" + listString)
					.show();
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			alert.show();
		}

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts.")
			.setPositiveButton("okidoki", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}

	//	public void theyOweMe(View view){
	//
	//		int newdebt, olddebt;
	//		EditText editText = (EditText) findViewById(R.id.debt_amount);
	//
	//		if(editText.getText() != null && !editText.getText().toString().isEmpty()){
	//
	//			int debtamount = Integer.parseInt(editText.getText().toString());
	//
	//			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);
	//			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
	//			sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);
	//
	//			if(sharednames.contains(selectedName) && debtamount != 0){
	//
	//				Editor editor = shareddebts.edit();
	//
	//				olddebt = shareddebts.getInt(selectedName, 0);
	//				newdebt = olddebt + debtamount;
	//				editor.putInt(selectedName, newdebt);
	//				editor.commit();
	//
	//				new AlertDialog.Builder(this).setTitle("Update").setMessage(Miscellaneous.debtUpdateMessage(true, newdebt, olddebt, selectedName)).setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
	//					public void onClick(DialogInterface dialog, int which){
	//						return;
	//					}
	//				}).show();
	//
	//				//				Intent intent = null;
	//				//				intent.putExtra(BOOLEAN_MESSAGE, true);
	//				//
	//				//				if(sharednumber.getString(selectedName, "").length() > 0){
	//				//					sendSMS(sharednumber.getString(selectedName, ""), "La till att du �r skyldig mig " + debtamount + " st�lar");
	//				//				}
	//			}
	//
	//			else{
	//				new AlertDialog.Builder(this).setTitle("Failed update").setMessage("Your request failed, you have to enter an amount that is not 0.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
	//					public void onClick(DialogInterface dialog, int which){
	//						return;
	//					}
	//				}).show();
	//
	//			}
	//
	//		}
	//
	//		//		intent.putExtra(ANOTHER_MESSAGE, selectedName);
	//		//
	//		//		startActivity(intent);
	//		else{
	//			new AlertDialog.Builder(this).setTitle("Failed update").setMessage("Your request failed, you have to enter an amount.").setPositiveButton("OK", new DialogInterface.OnClickListener(){
	//				public void onClick(DialogInterface dialog, int which){
	//					return;
	//				}
	//			}).show();
	//		}
	//	}

	public void iOweThem(View view){
		int newdebt, olddebt;

		//		Intent intent = new Intent(this, DebtAddingActivity.class);

		EditText editText = (EditText) findViewById(R.id.debt_amount);

		if(editText.getText() != null && !editText.getText().toString().isEmpty()){

			int debtamount = Integer.parseInt(editText.getText().toString());

			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);
			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);
			sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);

			if(sharednames.contains(selectedName) && debtamount != 0){

				Editor editor = shareddebts.edit();
				olddebt = shareddebts.getInt(selectedName, 0);
				newdebt = olddebt - debtamount;
				editor.putInt(selectedName, newdebt);
				editor.commit();

				String string = "";

				if ( newdebt > 0 && olddebt >= 0)
					string = selectedName +" now has a total debt of " + Integer.toString(newdebt) + "kr.";

				else if (newdebt > 0 && olddebt < 0)
					string = "Your debt situation has changed! " + selectedName + " now owes you " + Integer.toString(newdebt) + "kr."; 

				else if(newdebt == 0)
					string = "You are now even with " + selectedName + ".";

				else
					string = "Your total debt to " + selectedName + " has been decreased to " + Integer.toString(newdebt) + ".";

				new AlertDialog.Builder(this).setTitle("Update").setMessage(Miscellaneous.debtUpdateMessage(false, newdebt, olddebt, selectedName)).setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).show();

				//				intent.putExtra(BOOLEAN_MESSAGE, true);

				//				if(sharednumber.getString(selectedName, "").length() > 0){
				//					sendSMS(sharednumber.getString(selectedName, ""), "La till att jag �r skyldig dig " + debtamount + " st�lar");
				//				}				
			}

			else{
				new AlertDialog.Builder(this).setTitle("Failed update").setMessage("Your request failed, you have to enter an amount that is not 0.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).show();
			}
		}

		else{
			new AlertDialog.Builder(this).setTitle("Failed update").setMessage("Your request failed, you have to enter an amount.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}

		//		intent.putExtra(ANOTHER_MESSAGE, selectedName);
		//
		//		startActivity(intent);

	}

	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
	}

	public void splitDebt(View view){
		Intent intent = new Intent(this, SplitADebt.class);
		startActivity(intent);
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

package com.example.splitit;

import android.content.Context;
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

public class DebtMenu extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	public final static String BOOLEAN_MESSAGE = "com.example.myfirstapp.trueorfalse";

	public final static String ANOTHER_MESSAGE = "com.example.myfirstapp.contactlist";

	public SharedPreferences sharednames;

	public SharedPreferences shareddebts;

	public static final String MyNames = "Mynames";

	public static final String MyDebts = "Mydebts";

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
		//rest of the code
	}

	public void theyOweMe(View view){
		int newdebt, olddebt;

		Intent intent = new Intent(this, DebtAddingActivity.class);

		EditText editText = (EditText) findViewById(R.id.debt_amount);

		EditText editText2 = (EditText) findViewById(R.id.contact_name);

		String contact = editText2.getText().toString();

		if(editText.getText() != null && !editText.getText().toString().isEmpty()){


			int debtamount = Integer.parseInt(editText.getText().toString());


			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);

			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

			if(sharednames.contains(contact) && debtamount != 0){

				Editor editor = shareddebts.edit();

				olddebt = shareddebts.getInt(contact, 0);

				newdebt = olddebt + debtamount;

				editor.putInt(contact, newdebt);

				editor.commit();

				intent.putExtra(BOOLEAN_MESSAGE, true);
			}
		}


		intent.putExtra(ANOTHER_MESSAGE, contact);

		startActivity(intent);

	}

	public void iOweThem(View view){
		int newdebt, olddebt;

		Intent intent = new Intent(this, DebtAddingActivity.class);

		EditText editText = (EditText) findViewById(R.id.debt_amount);

		EditText editText2 = (EditText) findViewById(R.id.contact_name);

		String contact = editText2.getText().toString();

		if(editText.getText() != null && !editText.getText().toString().isEmpty()){


			int debtamount = Integer.parseInt(editText.getText().toString());


			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);

			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

			if(sharednames.contains(contact) && debtamount != 0){

				Editor editor = shareddebts.edit();

				olddebt = shareddebts.getInt(contact, 0);

				newdebt = olddebt - debtamount;

				editor.putInt(contact, newdebt);

				editor.commit();

				intent.putExtra(BOOLEAN_MESSAGE, true);
			}
		}


		intent.putExtra(ANOTHER_MESSAGE, contact);

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
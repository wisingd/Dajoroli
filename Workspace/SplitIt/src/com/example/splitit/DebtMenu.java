package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class DebtMenu extends ActionBarActivity implements OnItemSelectedListener{

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	public final static String BOOLEAN_MESSAGE = "com.example.myfirstapp.trueorfalse";

	public final static String ANOTHER_MESSAGE = "com.example.myfirstapp.contactlist";

	public SharedPreferences sharednames;

	public SharedPreferences shareddebts;

	public static final String MyNames = "Mynames";

	public static final String MyDebts = "Mydebts";
	
	private Spinner spinner;
	
	private String selectedName;

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
		//rest of the code
		addNamesToSpinner();
		spinner.setOnItemSelectedListener(this);
	}
	
	public void addNamesToSpinner(){
		spinner = (Spinner) findViewById(R.id.contact_name);
		List<String> list = new ArrayList<String>();

		sharednames = getSharedPreferences(MyNames, Context.MODE_WORLD_READABLE);
		
		Map<String,?> mappen = sharednames.getAll();

		if(mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				
				list.add(string);
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		spinner.setAdapter(dataAdapter);
	}

	public void theyOweMe(View view){
		int newdebt, olddebt;

		Intent intent = new Intent(this, DebtAddingActivity.class);

		EditText editText = (EditText) findViewById(R.id.debt_amount);

		if(editText.getText() != null && !editText.getText().toString().isEmpty()){


			int debtamount = Integer.parseInt(editText.getText().toString());


			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);

			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

			if(sharednames.contains(selectedName) && debtamount != 0){

				Editor editor = shareddebts.edit();

				olddebt = shareddebts.getInt(selectedName, 0);

				newdebt = olddebt + debtamount;

				editor.putInt(selectedName, newdebt);

				editor.commit();

				intent.putExtra(BOOLEAN_MESSAGE, true);
			}
		}


		intent.putExtra(ANOTHER_MESSAGE, selectedName);

		startActivity(intent);


	}

	public void iOweThem(View view){
		int newdebt, olddebt;

		Intent intent = new Intent(this, DebtAddingActivity.class);

		EditText editText = (EditText) findViewById(R.id.debt_amount);

		if(editText.getText() != null && !editText.getText().toString().isEmpty()){


			int debtamount = Integer.parseInt(editText.getText().toString());


			sharednames = getSharedPreferences(MyNames, Context.MODE_PRIVATE);

			shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

			if(sharednames.contains(selectedName) && debtamount != 0){

				Editor editor = shareddebts.edit();

				olddebt = shareddebts.getInt(selectedName, 0);

				newdebt = olddebt - debtamount;

				editor.putInt(selectedName, newdebt);

				editor.commit();

				intent.putExtra(BOOLEAN_MESSAGE, true);
			}
		}


		intent.putExtra(ANOTHER_MESSAGE, selectedName);

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

	@Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		selectedName = (String) parent.getItemAtPosition(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}

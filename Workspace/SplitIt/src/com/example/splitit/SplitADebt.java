package com.example.splitit;

import java.util.ArrayList;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SplitADebt extends ActionBarActivity implements OnItemSelectedListener{

	private Spinner spinner1;

	private Spinner spinner2;

	private Spinner spinner3;

	private String selectedName1;

	private String selectedName2;

	private String selectedName3;

	public SharedPreferences sharednames;

	public SharedPreferences shareddebts;

	public SharedPreferences sharednumber;

	public static final String MyNames = "Mynames";

	public static final String MyDebts = "Mydebts";

	public static final String MyNumbers = "Mynumbers";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_split_adebt);
		addNamesToSpinner(spinner1, R.id.spinner1);
		addNamesToSpinner(spinner2, R.id.spinner2);
		addNamesToSpinner(spinner3, R.id.spinner3);

	}

	public void splitIt(View view){
		if(selectedName1 == selectedName2 || selectedName1 == selectedName3 || selectedName2 == selectedName3){
			new AlertDialog.Builder(this).setTitle("Samma person").setMessage("You used the same person multiple times").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
		else{
			EditText editText = (EditText) findViewById(R.id.debt_amount);

			if(editText.getText() != null && !editText.getText().toString().isEmpty()){

				int debtamount = Integer.parseInt(editText.getText().toString());

				shareddebts = getSharedPreferences(MyDebts, Context.MODE_PRIVATE);

				sharednumber = getSharedPreferences(MyNumbers, Context.MODE_PRIVATE);

				if (debtamount != 0){

					Editor editor = shareddebts.edit();

					editor.putInt(selectedName1, shareddebts.getInt(selectedName1, 0) + debtamount / 3);

					editor.putInt(selectedName2, shareddebts.getInt(selectedName2, 0) + debtamount / 3);

					editor.putInt(selectedName3, shareddebts.getInt(selectedName3, 0) + debtamount / 3);

					editor.commit();
					
					new AlertDialog.Builder(this).setTitle("").setMessage("Your debt situation with " + selectedName1 + ", " + selectedName2 + " and " + selectedName3 +" have been updated.").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							return;
						}
					}).show();					

				}

				else{
					new AlertDialog.Builder(this).setTitle("").setMessage("You have to set an amount that is not zero").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which){
							return;
						}
					}).show();
				}
			}
			else{
				new AlertDialog.Builder(this).setTitle("").setMessage("You have to set an amount").setPositiveButton("okidoki", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which){
						return;
					}
				}).show();
			}

		}

	}


	public void addNamesToSpinner(Spinner spinner, int id){
		spinner = (Spinner) findViewById(id);
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
		spinner.setOnItemSelectedListener(this);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		switch(parent.getId()){
		case R.id.spinner1 :
			selectedName1 = (String) parent.getItemAtPosition(position);
			break;
		case R.id.spinner2 :
			selectedName2 = (String) parent.getItemAtPosition(position);
			break;
		case R.id.spinner3 :
			selectedName3 = (String) parent.getItemAtPosition(position);
			break;
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}
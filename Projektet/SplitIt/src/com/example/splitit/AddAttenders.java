package com.example.splitit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * This class is not activity, but solely adds the ability to choose among your contacts and how many of them should be active. 
 * This is accomplished by spinners and check-boxes.
 */
public class AddAttenders extends ActionBarActivity implements OnItemSelectedListener{

	private Spinner spinner1;
	private Spinner spinner2;
	private Spinner spinner3;

	private boolean checkbox1;
	private boolean checkbox2;
	private boolean checkbox3;

	private String selectedName1;
	private String selectedName2;
	private String selectedName3;

	public SharedPreferences sharednames;	
	public static final String MyNames = "Mynames";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		checkbox1=false;
		checkbox2=false;
		checkbox3=false;

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_attenders);

		addNamesToSpinner(spinner1, R.id.spinner1);
		addNamesToSpinner(spinner2, R.id.spinner2);
		addNamesToSpinner(spinner3, R.id.spinner3);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * Adding the names from the SharedPreference to the spinners. This is done 
	 * by first adding them to a map, from which the keys are converted to a list.
	 * To add the values to the spinners, an iterator is added to the list and runs through the list.
	 */
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

	/**
	 * Defines what happens when the check-boxes are clicked. That is, altering between being true or false. This will affect
	 * which and how many contacts are accounted for later.
	 */
	public void onCheckBoxClick(View v){
		boolean checked = ((CheckBox) v).isChecked();		
		switch(v.getId()){
		case  R.id.checkBox1:
			if(checked){
				checkbox1 = true;
			}
			else {
				checkbox1 = false;
			}
			break;
		case  R.id.checkBox2:
			if(checked){
				checkbox2 = true;
			}
			else {
				checkbox2 = false;
			}
			break;
		case  R.id.checkBox3:
			if(checked){
				checkbox3 = true;
			}
			else {
				checkbox3 = false;
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.add_attenders, menu);
		return true;
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

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_add_attenders,
					container, false);
			return rootView;
		}
	}

	/** Stores the selected name a name is selected in one of the spinners.
	 * When a name is selected in one of the spinners it is stored as a string that corresponds
	 * to the spinner it was selected in. That is, when a name is selected in "spinner1"
	 * it is saved in "selectedName1".
	 */
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
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}

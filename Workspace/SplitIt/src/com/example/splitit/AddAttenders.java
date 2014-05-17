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

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_attenders, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_add_attenders,
					container, false);
			return rootView;
		}
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
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}

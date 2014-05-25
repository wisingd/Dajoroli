package com.example.splitit;

import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static String BOOLEAN_MESSAGE = "com.example.myfirstapp.trueorfalse";
	public final static String ANOTHER_MESSAGE = "com.example.myfirstapp.contactlist";

	public SharedPreferences sharednames;
	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyNames = "Mynames";
	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";
	
	public static Helper db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new Helper(this);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public void startDebtMenu(View view){
		Intent intent = new Intent(this, DebtMenu.class);
		startActivity(intent);
	}
	
	public void startContactMenu(View view){
		Intent intent = new Intent(this, ContactMenu.class);
		startActivity(intent);
	}
	public void startEvent(View view){
		Intent intent = new Intent(this,EventCreater.class);
		startActivity(intent);
	}
}

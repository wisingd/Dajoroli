package com.example.splitit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

public class DebtMenu extends ActionBarActivity implements OnItemSelectedListener{


	public SharedPreferences shareddebts;
	public SharedPreferences sharednumber;

	public static final String MyDebts = "Mydebts";
	public static final String MyNumbers = "Mynumbers";

	private String selectedName;

	String listString = "";
	
	int newdebt;
	int olddebt;

	@Override
	public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
		setContentView(R.layout.debt_view);
	}

	public void theyOweMe(final View view){
		final ArrayList<String> list = new ArrayList<String>();
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = shareddebts.getAll();

		if (mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();

			// DIALOG 1
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Who owes you?")
			.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {

					if (isChecked) {
						selectedItems.add(list.get(which));
					} 
					else {
						selectedItems.remove(list.get(which));
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			.setPositiveButton("Next", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					final EditText input = new EditText(view.getContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setHint("Debt");

					// DIALOG 2
					new AlertDialog.Builder(view.getContext())
					.setTitle("Enter the total debt")
					.setView(input)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})
					.setPositiveButton("Next", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							final String value = "" + input.getText();
							final int debtAmount = Integer.parseInt(value);
							listString = "Do you want to assign the total debt " + value + "kr (" + debtAmount/selectedItems.size() + " kr each) to the following contacts?";

							for (String s :selectedItems){
								listString = listString + "\n" + s;
							}

							// DIALOG 3
							new AlertDialog.Builder(view.getContext())
							.setTitle("Confirm:")
							.setMessage("" + listString)
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							})
							.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
									Editor editor = shareddebts.edit();

									sharednumber = getSharedPreferences(MyNumbers, MODE_WORLD_READABLE);
									final ArrayList<String> numberList = new ArrayList<String>();
									String numberNames = "";

									for (String s :selectedItems){
										String number = sharednumber.getString(s, null);
										olddebt = shareddebts.getInt(s, 0);
										newdebt = olddebt + debtAmount / selectedItems.size();
										editor.putInt(s, newdebt);
										editor.commit();

										if (number != null){
											numberList.add(s);
										}
									}

									if (numberList.size() > 0) {
										for (String s : numberList){
											numberNames = numberNames + "\n" + s;
										}

										// DIALOG 4
										new AlertDialog.Builder(view.getContext())
										.setTitle("Notify")
										.setMessage("Would you like to notify \n" + numberNames + "\n\nby sending a SMS?")
										.setNegativeButton("No", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										})
										.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {

												for(String s : numberList){
													if (newdebt > 0){
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nIn total, you now owe me " + newdebt + " kr.");
													}
													else if (newdebt < 0){
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nIn total, I now owe you " + newdebt + " kr.");
													}
													else {
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nGuess what? We are now even!");
													}
												}
											}
										})
										.show();
									}
								}
							})
							.show(); // Dialog 3
						}
					})
					.show(); // Dialog 2
				}
			})
			.show(); // Dialog 1
		} // IF

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts yet.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
				}
			})
			.setNegativeButton("Add a contact", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//ContactMenu.addContact(view));
				}
			})
			.show();
		}
	}

	public void iOweThem(final View view){

		final ArrayList<String> list = new ArrayList<String>();
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = shareddebts.getAll();

		if (mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();

			// DIALOG 1
			AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
			alert.setTitle("Who do you owe?")
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setItems(cs, new DialogInterface.OnClickListener() {
				public void onClick (DialogInterface dialog, int which) {

					final int position = which;
					final EditText input = new EditText(view.getContext());
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setHint("Debt");

					// DIALOG 2
					new AlertDialog.Builder(view.getContext())
					.setTitle("Enter debt to " + list.get(position))
					.setView(input)
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						}
					})

					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String value = "" + input.getText();
							final int debtAmount = Integer.parseInt(value);
							final String name = list.get(position);
							String listString = "Do you want to add a debt of " + value + " kr to " + name + "?";

							// DIALOG 3
							new AlertDialog.Builder(view.getContext())
							.setTitle("Confirm:")
							.setMessage("" + listString)
							.setNegativeButton("No", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							})

							.setPositiveButton ("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Change the debt in SharedPreferences
									shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
									Editor editor = shareddebts.edit();
									olddebt = shareddebts.getInt(name, 0);
									newdebt = olddebt - debtAmount;
									editor.putInt(name, newdebt);
									editor.commit();

									sharednumber = getSharedPreferences(MyNumbers, MODE_WORLD_READABLE);
									final ArrayList<String> numberList = new ArrayList<String>();
									String numberNames = "";

									for (String s :selectedItems){
										String number = sharednumber.getString(s, null);
										if (number != null){
											numberList.add(s);
										}
									}

									if (numberList.size() > 0) {
										for (String s : numberList){
											numberNames = numberNames + "\n" + s;
										}

										// DIALOG 4
										new AlertDialog.Builder(view.getContext())
										.setTitle("Notify")
										.setMessage("Would you like to notify \n" + numberNames + "\n\nby sending a SMS?")
										.setNegativeButton("No", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										})
										.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {

												for(String s : numberList){
													if (newdebt > 0){
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nIn total, you now owe me " + newdebt + " kr.");
													}
													else if (newdebt < 0){
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nIn total, I now owe you " + newdebt + " kr.");
													}
													else {
														sendSMS(sharednumber.getString(s, null), 
																"Hello " + s + "! I have added that I have a debt of " + debtAmount / selectedItems.size() + " kr to you in Split It."
																		+ "\n\nGuess what? We are now even!");
													}
												}
											}
										})
										.show();
									}
								}
							})
							.show(); // Dialog 3
						}
					})
					.show(); // Debt
				}
			})
			.show();// SetItems

		} // IF

		else{
			new AlertDialog.Builder(this)
			.setTitle("No friends :( ")
			.setMessage("You do not have any contacts yet.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					return;
				}
			}).show();
		}
	}// Metod

	public void deleteDebt(final View view){
		listString = "";
		final ArrayList<String> list = new ArrayList<String>();
		shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
		Map<String,?> mappen = shareddebts.getAll();

		if (mappen.size() > 0){
			Set<String> settet = mappen.keySet();
			Iterator <String> iteratorn = settet.iterator();
			while(iteratorn.hasNext()){
				String string  = iteratorn.next();
				list.add(string);
			}//WHILE

			CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
			final ArrayList<String> selectedItems = new ArrayList<String>();
			String stringList = "";

			// DIALOGRUTA 1
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Choose contacts")
			.setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {

					if (isChecked) {
						selectedItems.add(list.get(which));
					} 
					else {
						selectedItems.remove(Integer.valueOf(which));
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for (String s: selectedItems){
						listString = listString + "\n" + s;
					}
					new AlertDialog.Builder(view.getContext())
					.setTitle("Confirm")
					.setMessage("Are you sure you want to even your debt situation with the following contacts? " + listString)
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Change the debt in SharedPreferences
							shareddebts = getSharedPreferences(MyDebts, Context.MODE_WORLD_READABLE);
							Editor editor = shareddebts.edit();

							for(String s: selectedItems){
								editor.putInt(s, 0);
								editor.commit();
							}
							
						
						}
					})
					.show();
				}
			})
			.show();
		}// IF

		else {
			new AlertDialog.Builder(view.getContext())
			.setTitle("No contacts")
			.setMessage("You do not seem to have any friends yet")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setNegativeButton("Add contact", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//ContactMenu.addContact(view);
				}
			})
			.show();
		}
	}//METHOD

	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
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
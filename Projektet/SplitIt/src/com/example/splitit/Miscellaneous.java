package com.example.splitit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * A class with some miscellaneous methods.
 * @author Johannes
 *
 */
public class Miscellaneous {
	
	/**
	 * A method that returns a string which states what the new debts have resulted in.
	 * Depending on if a new debt has increased or decreased the total debt of the user to the contact
	 * different strings are written. The string also differs depending on what the total situation is
	 * with the contact.
	 * 
	 * @param addingOrSubtracting 	true if the debt has increased else false
	 * @param newamount 			the amount before the addition of the last amount
	 * @param oldamount				the amount after the addition of the last amount
	 * @param name					the name of the contact with who the debt situation has been updated
	 * @return
	 */
	public static String debtUpdateMessage( boolean addingOrSubtracting, int newamount, int oldamount, String name){
		
		String snew = Integer.toString(newamount);

		String string = "";

		if (addingOrSubtracting){
			if ( newamount > 0 && oldamount >= 0){
				string = name +"'s debt to you has increased to " + snew + " kr.";
			}
			else if (newamount > 0){
				string = "Your debt situation has changed! " + name + " now owes you " + snew + " kr."; 
			}
			else if(newamount == 0){
				string = "You are now even with " + name + ".";
			}
			else{
				snew = Integer.toString(Math.abs(newamount));
				string = "Your debt to " + name + " has decreased to " + snew + " kr.";
			}
		}
		else{
			if(newamount < 0 && oldamount <= 0){
				snew = Integer.toString(Math.abs(newamount));

				string = "Your debt to " + name + " has increased to " + snew + " kr.";
			}
			else if(newamount < 0){
				snew = Integer.toString(Math.abs(newamount));

				string = "Your debt situtation has changed! You now owe " + name + " " + snew + " kr."; 
			}
			else if (newamount == 0){
				string = "You are now even with " + name + ".";
			}
			else {
				string = name + "'s debt to you has decreased to " + snew + "kr.";
			}
			
		}
		return string;
	}

	/**
	 * Displays an alert dialog with one button with the text "OK".
	 * @param title The title of the message
	 * @param message The message
	 * @param button The only button
	 */
	public static void displayMessage(String title, String message, Context context){
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	
			public void onClick(DialogInterface dialog, int which){
				return;
			}
		}).show();
	}

}

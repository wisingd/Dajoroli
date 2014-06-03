package com.example.splitit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;

/**
 * A class with some miscellaneous methods.
 */
public class Miscellaneous {

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

	/**
	 * Prints a list of strings as a pretty string, e.g. a list of vegetables transforms to something like "Tomato, Potato, Carrot, Cucumber and Turnip". 
	 * @param list The list that will be printed.
	 * @param bool True if it should be with 's, false otherwise.
	 * @return A string containing what the list contained.
	 */
	public static String listToPrettyString(List<String> list, boolean bool){
		String att = "";
		String str = "";
		
		if(bool)
			str = "'s";
		
		for(String s : list){
			if (list.size() == 1){
				att = att + s + str;
			}
			else if(list.indexOf(s) == list.size()-1){
				att = att +" and " + s + str;
			}
			else if(list.indexOf(s)==list.size()-2){
				att = att + s + str;
			}
			else{
				att = att  + s + str + ", ";
			}

		}
		return att;
	}

}

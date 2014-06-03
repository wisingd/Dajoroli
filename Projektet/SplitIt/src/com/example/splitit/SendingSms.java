package com.example.splitit;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
/**
 * A class for sending SMS.
 *
 */
public class SendingSms extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		
	}
	/**
	 * Sends the given message to the given number via SMS.
	 * @param phonenumber the number to which the SMS should be sent.
	 * @param message the text to be sent
	 */
	public void sendSMS(String phonenumber, String message){
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SendingSms.class), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phonenumber, null, message, pi, null);
	}

}

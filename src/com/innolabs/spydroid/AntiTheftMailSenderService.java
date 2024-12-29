package com.innolabs.spydroid;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;
import android.util.Log;


/**
 * AntiTheft Mail Sender Service is a Service that sends the After Theft details to the user either by e-mail or SMS.
 * 
 * @author Srikanth Rao
 * 
 * @see android.app.Service
 */
@SuppressWarnings("deprecation")
public class AntiTheftMailSenderService extends Service {
	
	/** Shared Preference to retrieve the Before theft values from the device */	
	SharedPreferences antiTheftSharedPrefs = null;
	
	/** Shared Preference to retrieve the After Theft values from the device */
	SharedPreferences afterTheftSharedDetails = null;

	/** String value to store the alert number */
	String alert_number = null;
	
	/** String value to store the alert mail address */
	String alert_mail = null;
	
	/** String value to store the IMEI number of the device */
	String imeiNumber = null;
	
	/** String value to store the mobile number of the device */
	String mobileNumber = null;

	/** String value to store the Operator Name of the new SIM card on the device */
	String operatorName = null;
	
	/** String value to store the address of the device */
	String address = null;
	
	/** String value to store the unique IMSI number of the SIM card */
	String simNumber = null;

	/** Long type variable to store the longitude co-ordinate of the location */
	long lattitude = 0;
	
	/** Long type variable to store the latitude co-ordinate of the location */
	long longtitude = 0;

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String Email_Tag = "SPY DROID - ANTITHEFT EMAIL SENDER SERVICE";


	/**
	 * Method that is called as soon as the service is started.
	 * 
	 * This method is used to save the new and old SIM details and e-mail or send SMS to the user.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v(Email_Tag, "Anti-Theft Email Sender Service Started");
		
		// Opening Shared Preferences Before_Theft_Prefs and Shared_Prefs
		antiTheftSharedPrefs = getSharedPreferences("Before_Theft_Prefs", MODE_PRIVATE);
		afterTheftSharedDetails = getSharedPreferences("Shared_Prefs", MODE_PRIVATE);

		Log.v(Email_Tag, "Retrieving and sharing the values from Shared Preferences");
		
		// Saving data to String and Long variables from Shared Preferences
		alert_number = antiTheftSharedPrefs.getString("ALERT_NUMBER", null);		
		alert_mail = antiTheftSharedPrefs.getString("ALERT_EMAIL", null);				
		imeiNumber = afterTheftSharedDetails.getString("IMEI_NUMBER", null);
		mobileNumber = afterTheftSharedDetails.getString("MOBILE_NUMBER", null);		
		operatorName = afterTheftSharedDetails.getString("OPERATOR_NAME", null);		
		simNumber = afterTheftSharedDetails.getString("SIM_NUMBER", null);
		lattitude = afterTheftSharedDetails.getLong("LATTITUDE", 0);
		longtitude = afterTheftSharedDetails.getLong("LOGITUDE", 0);
		address = afterTheftSharedDetails.getString("GPS_LOCATION", null);

		Log.v(Email_Tag, "Checking for Internet Availability");
		
		// Checking Internet Connectivity
		if(InternetConnectionAvailable())
		{
			
			Log.v(Email_Tag, "Internet Connection Available");

			// Sending Email
			sendEmail();

			Log.v(Email_Tag, "Sending Email");
		}

		// Sending SMS
		sendSMS();

		Log.v(Email_Tag, "Sending SMS");

		return super.onStartCommand(intent, flags, startId);
	}



	/**
	 * Send SMS is a method to send the SMS to the user if SIM card is Changed
	 */
	private void sendSMS() {

		// Setting the SMS Message String
		String smsMessage = "SIM CHANGE ALERT :"+"\n"+"New Number :"+mobileNumber+"\n"+"SIM ID : "+simNumber+"\n"+"Present Address : "+address;

		Log.v(Email_Tag, "SMS Message : "+smsMessage);

		// Starting SMS Manager to send SMS
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(AntiTheftMailSenderService.this, 0, new Intent("SMS_SENT"), 0);
		
		// Sending SMS
		smsManager.sendTextMessage(alert_number, null, smsMessage, pendingIntent, null);

		Log.v(Email_Tag, "SMS Message"+smsMessage+"Sent to"+alert_number);

	}


	/**
	 * Send Email is a method to send the email to the user if SIM card is Changed
	 */
	private void sendEmail() {

		Log.v(Email_Tag, "Sending Email");

		// Setting the username and password for the email
		String email_address = "0xr00t3r@gmail.com" , email_password = "uc4nn0tcr4ck!t";

		// Creating Mail object
		Mail mail = new Mail(email_address, email_password);

		String toaddress[] = {alert_mail};

		// Setting From Address
		mail.setFrom(email_address);

		// Setting to Address
		mail.setTo(toaddress);

		// Setting Subject for the mail
		mail.setSubject("SIM CARD CHANGE ALERT");

		// Setting Mail body
		mail.setBody("Device IMEI Number : "+imeiNumber+"\n"+"New Mobile Number : "+mobileNumber+"\n"+"New SIM Number : "+simNumber+"\n"+"New SIM Operator : "+operatorName+"\n"+"Device Location Details : "+"\n"+"Lattitude : "+lattitude+"\t"+"Logitude : "+longtitude+"\n"+address);

		Log.v(Email_Tag, "Email Body :- "+"Device IMEI Number : "+imeiNumber+"\n"+"New Mobile Number : "+mobileNumber+"\n"+"New SIM Number : "+simNumber+"\n"+"New SIM Operator : "+operatorName+"\n"+"Device Location Details : "+"\n"+"Lattitude : "+lattitude+"\t"+"Logitude : "+longtitude+"\n"+address);
		
		try {

			// Sending Email
			mail.send();

			// Checking if Email is sent or not
			if(mail.send())
			{
				Log.v(Email_Tag, "Mail Send Successfully ... ");
			}
			else
			{
				Log.v(Email_Tag, "Mail not Sent ...");
			}

		} catch (Exception e) {

			// Printing the Exceptions
			e.printStackTrace();

		}

	}



	/** Method called when BindService is called from other instance
	 * 
	 * @param intent The Intent from which the method was called
	 * 
	 * @return null Noting is sent back to the bindService method
	 * 
	 * @see android.app.Service#onBind(Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/**
	 * Method to check for Internet Connectivity to send the e-mail
	 * 
	 * @return Boolean value whether Internet connection is available or not
	 */
	private boolean InternetConnectionAvailable() {

		Log.v(Email_Tag, "Checking Internet Connectivity");

		// Starting Connectivity Manager Service
		ConnectivityManager checkConnectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo connectivity = checkConnectivity.getActiveNetworkInfo();

		// Checking if Internet is avaialble
		if(connectivity == null)
		{
			Log.v(Email_Tag, "No Internet Connection");

			return false;
		}

		Log.v(Email_Tag, "Internet Connected or Connecting");

		return connectivity.isConnectedOrConnecting();
	}

}

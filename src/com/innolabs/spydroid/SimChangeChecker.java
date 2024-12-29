package com.innolabs.spydroid;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Sim Changer Service is a class which has methods which checks for the Sim Change Activity and initiates the Anti Theft Service 
 * 
 * @see android.app.Service
 * 
 * @author Srikanth Rao
 *
 */
public class SimChangeChecker extends Service {

	/** String value to store old SIM unique IMSI number */
	String oldSIMNumber = null;
	
	/** String value to store new SIM unique IMSI number */
	String newSIMNumber = null;
	
	/** Telephony Manager variable to access the SIM and DEVICE details */
	TelephonyManager telephoneManager = null;
	
	/** Shared Preference to retrieve the old SIM card details that are store before */
	SharedPreferences simChangePreferences = null;
	
	/** String value to show the TAG for the activity in the LOG CAT */
	public static String SIMCHANGECHECKER_TAG = "SPY DROID - SIM CHANGE CHECKER SERVICE";
	
	/**
	 * Method that starts as soon as it receives the BOOT COMPLETED intent from the Android System.
	 * 
	 * This method is used to check for the SIM change activity in the device and initiate an action if SIM change or Theft activity is found.
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
		Log.v(SIMCHANGECHECKER_TAG, "Sim Change Checker Service Started");
		
		// Accessing Shared Preferences values
		simChangePreferences = getSharedPreferences("SIM_Change_Prefs", MODE_PRIVATE);
		
		// Getting the old SIM IMSI number from Shared Preference
		oldSIMNumber = simChangePreferences.getString("SIM_NUMBER", null);
		
		// Checking the new SIM card IMSI number from the device
		newSIMNumber = telephoneManager.getSimSerialNumber();
		
		Log.v(SIMCHANGECHECKER_TAG, "Comparing the old and new SIM properties");
		
		// Checking whether both old and new SIM properties are same or not
		if(oldSIMNumber.equals(newSIMNumber))
		{
			// Return Nothing
			return super.onStartCommand(intent, flags, startId);
		}
		else 
		{
			// Starting Anti Theft Service
			Intent newDataCollectorIntent = new Intent(SimChangeChecker.this, AntiTheftService.class);
			Log.v(SIMCHANGECHECKER_TAG, "Starting Anti Theft Service");
			startService(newDataCollectorIntent);
			
			// Starting Anti Theft Mail Sender Service
			Intent antiTheftMailSenderIntent = new Intent(SimChangeChecker.this, AntiTheftMailSenderService.class);
			Log.v(SIMCHANGECHECKER_TAG, "Starting Anti Theft Mail Sender Service");
			startService(antiTheftMailSenderIntent);
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

}

package com.innolabs.spydroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Boot Receiver is a class of Android Component type Broadcast Receiver which initiates on Receiving some Intent.
 * 
 * This class BootReceiver is instantiated once the Mobile phone is turned ON after a Shutdown or a Restart.
 * 
 * @author Srikanth Rao
 * 
 * @see android.content.BroadcastReceiver
 *
 */
public class BootReceiver extends BroadcastReceiver {

	public static String BOOTRECEIVER_TAG = "SPY DROID - BOOT RECEIVER {BROADCAST RECEIVER}";
	
	/**
	 * onReceive is a method used to start both the Spy Droid Main Service and SIM Change Checker service for Anti-Theft Module of the application
	 * 
	 * @param context The Context from which the method was called.
	 * @param intent The Intent which was received by the Broadcast Receiver. In this method the Intent received is BOOT_COMPLETED intent.
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(Context, Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.v(BOOTRECEIVER_TAG, "Boot Receiver Service Started");
		
		Log.v(BOOTRECEIVER_TAG, "Starting Main Service");
		
		// Starting Main Service
		Intent mainServiceIntent = new Intent(context, MainService.class);
		context.startService(mainServiceIntent);
		
		Log.v(BOOTRECEIVER_TAG, "Starting SIM Change Checker Service");
		
		// Starting SIMChange Checked Service
		Intent simChangeCheckIntent = new Intent(context, SimChangeChecker.class);
		context.startService(simChangeCheckIntent);
		
	}

	
	
}

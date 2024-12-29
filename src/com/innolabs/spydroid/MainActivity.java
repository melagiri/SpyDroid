package com.innolabs.spydroid;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Activity to display the main page of the application.
 * 
 * This is the activity which is displayed to the user after the Splash Screen Activity.
 * 
 * @see com.google.android.maps.MapActivity 
 * 
 * @see android.location.LocationListener
 * 
 * @author Srikanth Rao
 */
public class MainActivity extends MapActivity implements LocationListener {

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String MAINACTIVITY_TAG = "SPY DROID - MAIN ACTIVITY";

	/** Text View to display the IMEI number of the device. */
	TextView tv_imei;

	/**Telephony Manager to access the phone details which are stored to check for the theft activity. */
	TelephonyManager telephoneManager = null;

	/** MapView displays the map on the Activity screen. */
	MapView mapView;

	/**GeoCoder variable is used in the process of transforming a (latitude, longitude) coordinate into a (partial) address. */
	Geocoder geoCoder;

	/** String value of the bestProvider. */
	String bestProvider;

	/** Double value to store the latitude value. */
	double lattitude = 0;

	/** Double value to store the longitude value. */
	double longitude = 0 ;

	/**
	 * Method to initialize and add some view to the user interface.
	 * 
	 * @param savedInstanceState State which is saved and retrieved when an application is paused and re-opened.
	 * 
	 * @see android.app.Activity#onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		Log.v(MAINACTIVITY_TAG, "Content View set to R.layout.main");

		// Accessing the Telephony Manager Service
		telephoneManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

		Log.v(MAINACTIVITY_TAG, "Accessing Telephony Manager Service");

		// Registering the TextView
		tv_imei = (TextView) findViewById(R.id.imei);

		// Getting the IMEI number of the device
		String imei = telephoneManager.getDeviceId();

		Log.v(MAINACTIVITY_TAG, "Getting Device ID : "+imei);

		// Setting the value of IMEI to the TextView
		tv_imei.setText(imei);

		// Getting reference to MapView
		mapView = (MapView) findViewById(R.id.mapview);

		Log.v(MAINACTIVITY_TAG, "Registered Map View");

		// Setting Zoom Controls on MapView
		mapView.setBuiltInZoomControls(true);

		// Getting LocationManager object from System Service LOCATION_SERVICE
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Log.v(MAINACTIVITY_TAG, "Accessing the Location Service");

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);

		// Checking if location is changed or not
		if(location!=null){

			// Calling method onLocationChanged if the user or device location is changed
			onLocationChanged(location);

			Log.v(MAINACTIVITY_TAG, "OnLocationChanged method called");
		}

		// Setting update intervals for the locationManager to check for location change
		locationManager.requestLocationUpdates(provider, 10000, 0, this);

		Log.v(MAINACTIVITY_TAG, "Location Manager set to update every 10 seconds");		

	}

	/**
	 * Method which is called when the user location has been changed.
	 * 
	 * @see android.location.LocationListener#onLocationChanged(Location)
	 * 
	 * @param location The new value of location to where the user has been moved from old location.
	 */
	public void onLocationChanged(Location location) {

		// Getting latitude
		double latitude = location.getLatitude();

		Log.v(MAINACTIVITY_TAG, "Getting latitude : "+latitude);

		// Getting longitude
		double longitude = location.getLongitude();

		Log.v(MAINACTIVITY_TAG, "Getting longtitude : "+longitude);

		// Creating an instance of GeoPoint corresponding to latitude and longitude
		GeoPoint point = new GeoPoint((int)(latitude * 1E6), (int)(longitude*1E6));

		// Getting MapController
		MapController mapController = mapView.getController();

		// Locating the Geographical point in the Map
		mapController.animateTo(point);

		// Applying a zoom
		mapController.setZoom(17);

		// Redraw the map
		mapView.invalidate();

		Log.v(MAINACTIVITY_TAG, "Redrawing the Map");

		// Getting list of overlays available in the map
		List<Overlay> mapOverlays = mapView.getOverlays();

		// Creating a drawable object to represent the image of mark in the map
		Drawable drawable = this.getResources().getDrawable(R.drawable.cur_position);

		// Creating an instance of ItemizedOverlay to mark the current location in the map
		CurrentLocationOverlay currentLocationOverlay = new CurrentLocationOverlay(drawable);

		// Creating an item to represent a mark in the overlay
		OverlayItem currentLocation = new OverlayItem(point, "Current Location", "Latitude : " + latitude + ", Longitude:" + longitude);

		// Adding the mark to the overlay
		currentLocationOverlay.addOverlay(currentLocation);

		// Clear Existing overlays in the map
		mapOverlays.clear();

		// Adding new overlay to map overlay
		mapOverlays.add(currentLocationOverlay);	



	}

	/** Method initiated when the provider is disabled
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(String)
	 * 
	 * @param provider String value of the provider.
	 */
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}


	/** Method initiated when the provider is enabled
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(String)
	 * 
	 * @param provider String value of the provider.
	 */
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}


	/** Method to check for the status change
	 * 
	 * @see android.location.LocationListener#onStatusChanged(String, int, Bundle)
	 * 
	 * @param provider String value of the provider.
	 * @param status The status value for the map.
	 * @param extras Bundle value which is not of any specific type and are incoming.
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}


	/** Method which is called when the application window i.e activity window is closed or minimized 
	 * 
	 *  @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		super.onPause();

		// Starting the RootService
		Intent rootServiceIntent = new Intent(MainActivity.this, RootService.class);
		startService(rootServiceIntent);

		Log.v(MAINACTIVITY_TAG, "Starting RootService");
	}


	/**
	 * Method to add menu for the Activity.
	 * 
	 * This method is used to add the menu items to the activity page i.e Settings option for the Activity.
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(Menu)
	 * 
	 * @param menu The menu varaible that is used to add options for the Menu table.
	 * @return Menu Items
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Adding Preferences menu item
		menu.add("Preferences").setIcon(android.R.drawable.ic_menu_preferences);

		// Adding About menu item
		menu.add("About").setIcon(android.R.drawable.ic_menu_info_details);

		//Adding Exit menu item
		menu.add("Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Method to add listener to the menu item selection panel
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(MenuItem)
	 * 
	 * @param item The menu item which is clicked.
	 * @return Option Item which was selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getTitle().equals("Preferences"))
		{
			// Starting the Anti-Theft class Preferences Activity
			Intent menuIntent = new Intent(MainActivity.this, AntiTheftPreferences.class);
			menuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(menuIntent);

			Log.v(MAINACTIVITY_TAG, "Starting AntiTheft Preferences Activity");
		}

		if(item.getTitle().equals("About"))
		{

			// Building Alert Dialog for the About menu item
			AlertDialog.Builder alert_about = new AlertDialog.Builder(this);
			alert_about.setTitle("About this Application");
			alert_about.setIcon(android.R.drawable.ic_dialog_alert);
			alert_about.setMessage("Spy Droid is an Android Application developed by Srikanth Rao, BCA final year, Veerashaiva College ...");
			alert_about.setNeutralButton("OK", new DialogInterface.OnClickListener() {

				/**
				 * Method to listen the clicks for the Alert Dialog
				 * 
				 * @param DialogInterface the dialog on which the click listener is established
				 * @param which The item number or the index value of the dialog interface
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// Closing the Alert Dialog
					dialog.dismiss();

				}
			});

			// Creating Alert Dialog
			alert_about.create();

			// Displaying the Alert Dialog
			alert_about.show();

			Log.v(MAINACTIVITY_TAG, "Displaying About Alert Dialog");
		}

		if(item.getTitle().equals("Exit"))
		{
			super.finish();

			Log.v(MAINACTIVITY_TAG, "Activity Finished");
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 *  Predefined method to check whether the route in the map is displayed or not.
	 *  
	 *  @see com.google.android.maps.MapActivity#isRouteDisplayed
	 *  
	 */
	@Override
	protected boolean isRouteDisplayed() {


		return false;
	}



}



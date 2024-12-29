package com.innolabs.spydroid;

import java.io.Serializable;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * 	Activity used to show the Splash Screen.
 *
 *	This is an activity which shows up first when a user opens the application.
 *
 * @see android.app.Activity
 *
 *	@author Srikanth Rao
 *	@version 1.03
 */

public class SplashActvity extends Activity implements Serializable {

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String SPLASH_TAG = "SPY DROID - SPLASH ACTIVITY";

	/** Auto-generated constant value. */
	private static final long serialVersionUID = 6770698151835349986L;

	/**
	 *  Boolean to check whether the runtime is the first time after the installation or not.
	 */
	boolean isFirstInstallation = true;

	/**
	 * Shared Preference used to store the @link isFirstInstallation value for the next run.
	 */
	SharedPreferences sd_sharedprefs = null;

	/**Shared Preference used to store the @link email, @link password, and @link mobile number to which the spy data will be sent. */ 
	SharedPreferences sd_sharedprefsMail = null;

	/** Shared Preference editor to edit the values in Shared Preferneces. */
	SharedPreferences.Editor sd_sharedprefseditor = null;

	/** Variable to store email value. */
	String email = null;

	/**Variable to store the mobile phone number value. */
	String mobile = null;

	/**Variable to type Registration Dialog initiated to null. */
	RegistrationDialog registrationDialog = null;

	/**ImageView element used to add the background which is used to listening the touch interactions of the user. */
	ImageView iv;

	/**
	 * Method to initialize the view and set listeners for touch interactions.
	 * 
	 * @param savedInstanceState Bundle data which is saved and retrieved when an application is paused and re-opened.
	 * 
	 * @see android.app.Activity#onCreate()
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		Log.v(SPLASH_TAG, "Content View set to : R.layout.splash");

		iv = (ImageView) findViewById(R.id.imageView1);

		Log.v(SPLASH_TAG, "Image View Registered");

		Log.v(SPLASH_TAG, "Shared Preferences INSTALLATION_FLAG and SD_Prefs created");

		sd_sharedprefs = this.getSharedPreferences("INSTALLATION_FLAG", MODE_PRIVATE);
		sd_sharedprefsMail = this.getSharedPreferences("SD_Prefs", MODE_PRIVATE);

		//New Registration dialog initiated.
		registrationDialog = new RegistrationDialog();

		Log.v(SPLASH_TAG, "Registration Dialog Instantiated");

		Log.v(SPLASH_TAG, "Setting OnClick Listener to Image View");

		// Setting on Click listener for the ImageView 
		iv.setOnClickListener(new OnClickListener() {

			/** 
			 * Method to add some action on click of the image in ImageView 
			 * 
			 * @see android.view.View.OnClickListener#onClick(View)
			 * 
			 * @param The view which is checked for the clicks.
			 */
			@Override
			public void onClick(View v) {

				Log.v(SPLASH_TAG, "Image View Clicked");

				isFirstInstallation = sd_sharedprefs.getBoolean("KEY_FLAG", isFirstInstallation);

				Log.v(SPLASH_TAG, "isFirstInstallation saved to Shared Preferences");

				// Checking if isFirstInstallation is TRUE
				if(isFirstInstallation == true)
				{					
					
					// Setting isFirstInstallation to FALSE
					isFirstInstallation = false;

					Log.v(SPLASH_TAG, "isFirstInstalltion : "+isFirstInstallation);

					// Saving the value of isFirstInstallation to the Shared Preference
					sd_sharedprefseditor = sd_sharedprefs.edit();
					sd_sharedprefseditor.putBoolean("KEY_FLAG", isFirstInstallation);
					sd_sharedprefseditor.commit();

					Log.v(SPLASH_TAG, "Shared Preference saved the isFirstInstalltion value");

					// Opening the Registration Dialog
					registrationDialog.Registration_Dialog(SplashActvity.this);

					Log.v(SPLASH_TAG, "Registration Dialog displayed");

					Toast.makeText(getApplicationContext(), "This is displayed only the First time.", Toast.LENGTH_LONG).show();

				}
				else 
				{					
					
					// Getting the Email and Mobile number values
					email = sd_sharedprefsMail.getString("sd_email", null);
					mobile = sd_sharedprefsMail.getString("sd_mobile", null);

					Log.v(SPLASH_TAG, "Getting values from Shared Preferences : email = "+email+" mobile = "+mobile);

					// Checking if they are null
					if(email == null || mobile == null)
					{
						// Changing the isFirstInstallation key to TRUE again
						isFirstInstallation = true;

						Log.v(SPLASH_TAG, "Changing the isFirstInstallation to : "+isFirstInstallation);

						// Saving the changed isFirstInstallation key to the Shared Preference
						sd_sharedprefseditor = sd_sharedprefs.edit();
						sd_sharedprefseditor.putBoolean("KEY_FLAG", isFirstInstallation);
						sd_sharedprefseditor.commit();

						Log.v(SPLASH_TAG, "Saving the new value of isFirstInstallation to Shared Preferences");

						return;
					}

					// Checking if email and mobile values are not entered
					if(email.contentEquals("") ||  mobile.contentEquals(""))
					{
						// Displaying the Registration Dialog again to complete
						registrationDialog.Registration_Dialog(SplashActvity.this);

						Log.v(SPLASH_TAG, "Displayed the Registration Dialog again");

					}
					else
					{
						// Starting Main Activity
						Intent mainActivityIntent = new Intent(SplashActvity.this, MainActivity.class);
						Log.v(SPLASH_TAG, "Starting Main Activity");
						startActivity(mainActivityIntent);

					}
				}

			}
		});


	}

}

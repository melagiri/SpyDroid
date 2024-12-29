package com.innolabs.spydroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

/**
 * Activity to show the Anti-Theft Preferences.
 * 
 * This window displays the Settings or Preferences for the Anti-Theft module which is installed on the device.
 * 
 * @author Srikanth Rao
 * @version 1.03
 */
public class AntiTheftPreferences extends Activity implements OnClickListener {

	/** Toggle button for enabling or disabling the Anti-Theft Management */
	ToggleButton tgl_btn_reporting;

	/** Check box for enabling or disabling the SMS Reporting */
	CheckBox chk_box_sms_reporting;

	/** Check box for enabling or disabling the Email Reporting */
	CheckBox chk_box_email_reporting;

	/** Text View for showing the SMS alert number */
	TextView tv_sms_alert_number;

	/** Text View for showing the Email alert address */
	TextView tv_email_alert_address;

	/**  Button to change the SMS alert number */
	Button btn_sms_alert_change;

	/** Button to change the Email alert address */
	Button btn_email_alert_change;

	/** String value to store the reporting phone number */
	String reporting_number = "Enter Alert Number";

	/** String value to store the reporting email address */
	String reporting_email = "Enter Alert Email" ;

	/** Shared Preference to store the Preference values */
	SharedPreferences antiTheftSharedPrefs = null;

	/** Shared Preference editor variable to edit the shared preferences */
	SharedPreferences.Editor antiTheftSharedPrefsEditor = null;

	/** Boolean value to store the check value of the Toggle Button */
	public static boolean toggle_btn_value;

	/** Boolean value to store the check value of the SMS Reporting Check box */
	public static boolean check_btn_sms;

	/** Boolean value to store the check value of the Emial Reporting Check box */
	public static boolean check_btn_email;

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String PREFERENCE_TAG = "SPY DROID - ANTITHEFT PREFERENCE ACTIVITY";

	/**
	 * Method to initialize and add some view to the user interface.
	 * 
	 * @param Bundle Which is saved and retrieved when an application is paused and re-opened.
	 * 
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Log.v(PREFERENCE_TAG, "Anti Theft Preference Activity Started");
		
		// Setting the content view to the preferences xml page 
		setContentView(R.layout.preferences);

		Log.v(PREFERENCE_TAG, "Content View set to R.layout.preferences");

		// Initializing Views
		intializeViews();

		Log.v(PREFERENCE_TAG, "Views Registered");

		// Retrieving the Shared Preferences to store the values
		antiTheftSharedPrefs = getSharedPreferences("Before_Theft_Prefs", MODE_PRIVATE);

		// Getting the boolean values for the toggle button and check boxes
		toggle_btn_value = antiTheftSharedPrefs.getBoolean("ENABLE_REPORTING", false);
		check_btn_sms = antiTheftSharedPrefs.getBoolean("ALERT_SMS_CHECKBOX", false);
		check_btn_email = antiTheftSharedPrefs.getBoolean("ALERT_EMAIL_CHECKBOX", false);

		// Getting the string values for email address and phone number 
		reporting_number = antiTheftSharedPrefs.getString("ALERT_NUMBER", null);
		reporting_email = antiTheftSharedPrefs.getString("ALERT_EMAIL", null);

		Log.v(PREFERENCE_TAG, "Getting values from Shared Preferences");

		// Setting the Shared Preferences values to the toggle button and check boxes
		tgl_btn_reporting.setChecked(toggle_btn_value);
		chk_box_sms_reporting.setChecked(check_btn_sms);
		chk_box_email_reporting.setChecked(check_btn_email);

		// Setting the Shared Preferences values to the Text Views
		tv_sms_alert_number.setText(reporting_number);
		tv_email_alert_address.setText(reporting_email);

		// Setting onCheckChanged Listener
		tgl_btn_reporting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			/**
			 * Method to do some action on check change of the ToggleButton
			 * 
			 * @param buttonView The Toggle button value
			 * @param isChecked Boolean value to retrieve to check to what value the toggle button has been changed
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				Log.v(PREFERENCE_TAG, "OnCheckedChange Listener for Toggle Button");

				// Checking if Toggle button is checked
				if (isChecked == true) {

					// Assigning the toggle button value to TRUE
					toggle_btn_value = true;

					Log.v(PREFERENCE_TAG, "Toggle Button Checked = TRUE");

					// Assigning the sms reporting check box to TRUE
					chk_box_sms_reporting.setEnabled(true);

					Log.v(PREFERENCE_TAG, "Check Box for SMS Enabled");

					// Assigning the email reporting check box to TRUE
					chk_box_email_reporting.setEnabled(true);

					Log.v(PREFERENCE_TAG, "Check Box for EMAIL Enabled");

					// Setting the boolean values for the Check box booleans
					check_btn_sms = true;
					check_btn_email = true;

					Log.v(PREFERENCE_TAG, "Changed the constant values of SMS Check Button to "+check_btn_sms+" and Email Check Button to"+check_btn_email);

				}
				else 
				{
					// Assigning the toggle button value to FALSE
					toggle_btn_value = false;

					Log.v(PREFERENCE_TAG, "Toggle Button Checked = FALSE");

					// Assigning the sms reporting check box to FALSE
					chk_box_sms_reporting.setEnabled(false);

					Log.v(PREFERENCE_TAG, "Check box for SMS Disabled");

					// Assigning the email reporting check box to FALSE
					chk_box_email_reporting.setEnabled(false);

					Log.v(PREFERENCE_TAG, "Check box for Email Disabled");

					// Setting the boolean values for the Check box booleans
					check_btn_sms = false;
					check_btn_email = false;

				}

			}
		});



		/**
		 * Setting the onCheck changed listener for the SMS reporting check box 
		 */
		chk_box_sms_reporting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			/**
			 * Method to do some action on check change of the SMS reporting Checkbox
			 * 
			 * @param buttonView The Checkbox button value
			 * @param isChecked Boolean value to retrieve to check to what value the checkbox button has been changed
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// Checking if the Checkbox value is TRUE
				if(isChecked == true)
				{
					// Assigning the check box button value to TRUE
					check_btn_sms = true;

					// Setting the text view element enabled
					tv_sms_alert_number.setEnabled(true);

					Log.v(PREFERENCE_TAG, "SMS Text View Enabled");

					// Setting the button to visible state
					btn_sms_alert_change.setVisibility(1);

					Log.v(PREFERENCE_TAG, "SMS Change Button Visible");
				}
				else
				{
					// Assigning the check box button value to FALSE
					check_btn_sms = false;

					// Setting the text view element disabled
					tv_sms_alert_number.setEnabled(false);

					Log.v(PREFERENCE_TAG, "SMS Text View Disabled");

					// Setting the button to invisible state
					btn_sms_alert_change.setVisibility(0);

					Log.v(PREFERENCE_TAG, "SMS Change Button Invisible");
				}

			}
		});


		/**
		 * Setting the onCheck changed listener for the Email reporting check box 
		 */
		chk_box_email_reporting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			/**
			 * Method to do some action on check change of the Email Reporting Checkbox
			 * 
			 * @param buttonView The Checkbox button value
			 * @param isChecked Boolean value to retrieve to check to what value the checkbox button has been changed
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				// Checking if the Checkbox value is TRUE
				if(isChecked == true)
				{
					// Assigning the check box button value to TRUE
					check_btn_email = true;

					// Setting the text view element enabled
					tv_email_alert_address.setEnabled(true);

					Log.v(PREFERENCE_TAG, "Email Text View Enabled");

					// Setting the button to visible state
					btn_email_alert_change.setVisibility(1);

					Log.v(PREFERENCE_TAG, "Email Change Button Visible");
				}
				else
				{
					// Assigning the check box button value to FALSE
					check_btn_email = false;

					// Setting the text view element disabled
					tv_email_alert_address.setEnabled(false);

					Log.v(PREFERENCE_TAG, "Email Text View Disabled");

					// Setting the button to invisible state
					btn_email_alert_change.setVisibility(0);

					Log.v(PREFERENCE_TAG, "Email Change Button Invisible");
				}

			}
		});

		// Setting OnClick Listener to the SMS alert number Changer button
		btn_sms_alert_change.setOnClickListener(this);

		// Setting onClick Listener to the Email alert address Changer button
		btn_email_alert_change.setOnClickListener(this);

		//Setting the value of reporting number String to the text view Text
		reporting_number = tv_sms_alert_number.getText().toString();

		//Setting the value of reporting email String to the text view Text
		reporting_email = tv_email_alert_address.getText().toString();

		Log.v(PREFERENCE_TAG, "Reporting Number : "+reporting_number+" Reporting Email : "+reporting_email);


	}

	/** Method which is called when the application window i.e activity window is closed or minimized 
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		super.onPause();

		// Opening Shared Preference "Before_Theft_Prefs"
		antiTheftSharedPrefs = getSharedPreferences("Before_Theft_Prefs", MODE_PRIVATE);

		Log.v(PREFERENCE_TAG, "Before_Theft_Prefs intiated with antiTheftSharedPrefs");

		// Making the Shared Preference editable
		antiTheftSharedPrefsEditor = antiTheftSharedPrefs.edit();

		Log.v(PREFERENCE_TAG, "antiTheftSharedPrefs made Editable");

		// Saving the Values to the Shared Preferences
		reporting_number = tv_sms_alert_number.getText().toString();
		reporting_email = tv_email_alert_address.getText().toString();

		antiTheftSharedPrefsEditor.putBoolean("ENABLE_REPORTING", toggle_btn_value);

		Log.v(PREFERENCE_TAG, "ENABLE_REPORTING : "+toggle_btn_value);

		antiTheftSharedPrefsEditor.putBoolean("ALERT_SMS_CHECKBOX", check_btn_sms);

		Log.v(PREFERENCE_TAG, "ALERT_SMS_CHECKBOX : "+check_btn_sms);

		antiTheftSharedPrefsEditor.putBoolean("ALERT_EMAIL_CHECKBOX", check_btn_email);

		Log.v(PREFERENCE_TAG, "ALERT_EMAIL_CHECKBOX : "+check_btn_email);

		antiTheftSharedPrefsEditor.putString("ALERT_NUMBER", reporting_number);

		Log.v(PREFERENCE_TAG, "ALERT_NUMBER : "+reporting_number);

		antiTheftSharedPrefsEditor.putString("ALERT_EMAIL", reporting_email);

		Log.v(PREFERENCE_TAG, "ALERT_EMAIL : "+reporting_email);

		antiTheftSharedPrefsEditor.commit();

		Log.v(PREFERENCE_TAG, "Preferences Saved");
	}

	/**
	 * Method to register the views inside the Activity for which actions will be given
	 */
	private void intializeViews() {

		Log.v(PREFERENCE_TAG, "Registering Views");

		// Registering views

		tgl_btn_reporting = (ToggleButton) findViewById(R.id.toggleButton1);

		chk_box_sms_reporting = (CheckBox) findViewById(R.id.checkBox1);
		chk_box_email_reporting = (CheckBox) findViewById(R.id.checkBox2);

		tv_sms_alert_number = (TextView) findViewById(R.id.sms_alert_number);
		tv_email_alert_address = (TextView) findViewById(R.id.email_alert_address);

		btn_sms_alert_change = (Button) findViewById(R.id.btn_sms_alert_change);
		btn_email_alert_change = (Button) findViewById(R.id.btn_email_alert_change);


	}

	/**
	 * Method to do some action on Click activity of the element in the Activity window
	 * 
	 * @param v The view which is clicked
	 */
	@Override
	public void onClick(View v) {

		// Getting the view which was clicked
		switch (v.getId()) {

		case R.id.btn_sms_alert_change:

			Log.v(PREFERENCE_TAG, "Buiding SMS Alert Number Alert Dialog");

			// Building Alert Dialog
			AlertDialog.Builder alert_sms = new AlertDialog.Builder(this);

			// Setting Title and Icon for the Alert Dialog
			alert_sms.setTitle("SMS Alert Number");
			alert_sms.setIcon(android.R.drawable.ic_dialog_email);

			// Creating a Text View view to display "Enter the Number to be Alerted : "
			TextView tv_mobile_number = new TextView(this);
			tv_mobile_number.setText("Enter the Number to be Alerted :");
			tv_mobile_number.setTextColor(Color.WHITE);
			LayoutParams tv_mobile_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv_mobile_number.setLayoutParams(tv_mobile_params);

			// Creating a Edit Text field for the user to input the Phone number
			final EditText et_mobile_number = new EditText(this);
			LayoutParams et_mobile_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			et_mobile_number.setLayoutParams(et_mobile_params);

			// Setting the input type of the edit text to type of Phone number
			et_mobile_number.setInputType(InputType.TYPE_CLASS_PHONE);

			// Creating a linear layout with Vertical orientation 
			LinearLayout sms_linear_layout = new LinearLayout(this);
			sms_linear_layout.setOrientation(LinearLayout.VERTICAL);

			// Adding Views into the linear layout
			sms_linear_layout.addView(tv_mobile_number);
			sms_linear_layout.addView(et_mobile_number);

			// Setting the view of the Alert Dialog to the Linear layout
			alert_sms.setView(sms_linear_layout);

			// Adding a button to the Alert Dialog
			alert_sms.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

				/**
				 * Method to do some action on Click of the Button in SMS Number Alert Box
				 * 
				 * @param dialog The alert dialog for which the click listener is set
				 * @param which The index value of the button which was clicked
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// Checking if the field is not left Empty
					if(et_mobile_number.getText().toString() != null)
					{
						// Setting the textview Alert Number to the Edit text retrieved text
						tv_sms_alert_number.setText(et_mobile_number.getText().toString());
					}

				}
			});

			// Creating the Alert Dialog
			alert_sms.create();

			// Displaying the Alert Dialog
			alert_sms.show();

			Log.v(PREFERENCE_TAG, "SMS Alert Number Dialog Displayed");

			break;

		case R.id.btn_email_alert_change:

			Log.v(PREFERENCE_TAG, "Building Email Alert Address Dialog");

			// Building Alert Dialog
			AlertDialog.Builder alert_email = new AlertDialog.Builder(this);

			// Setting Title and Icon for the Alert Dialog
			alert_email.setTitle("Email Alert Address");
			alert_email.setIcon(android.R.drawable.ic_dialog_email);

			// Creating a Text View view to display "Enter the Email Address to be Alerted : "
			TextView tv_email = new TextView(this);
			tv_email.setText("Enter the Email to be Alerted :");
			tv_email.setTextColor(Color.WHITE);
			LayoutParams tv_email_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			tv_email.setLayoutParams(tv_email_params);

			// Creating a Edit Text field for the user to input the Email address
			final EditText et_email = new EditText(this);
			LayoutParams et_email_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			et_email.setLayoutParams(et_email_params);

			// Setting the input type of the edit text to type of Email address
			et_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

			// Creating a linear layout with Vertical orientation
			LinearLayout email_linear_layout = new LinearLayout(this);
			email_linear_layout.setOrientation(LinearLayout.VERTICAL);

			// Adding Views into the linear layout
			email_linear_layout.addView(tv_email);
			email_linear_layout.addView(et_email);

			// Setting the view of the Alert Dialog to the Linear layout
			alert_email.setView(email_linear_layout);

			// Adding a button to the Alert Dialog
			alert_email.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

				/**
				 * Method to do some action on Click of the Button in Email Address Alert Box
				 * 
				 * @param dialog The alert dialog for which the click listener is set
				 * @param which The index value of the button which was clicked
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {

					// Checking if the field is not left Empty
					if(et_email.getText().toString() != null)
					{
						// Setting the textview Alert Number to the Edit text retrieved text
						tv_email_alert_address.setText(et_email.getText().toString());
					}


				}
			});

			// Creating the Alert Dialog
			alert_email.create();

			// Displaying the Alert Dialog
			alert_email.show();

			Log.v(PREFERENCE_TAG, "Email Alert Address Dialog Displayed");

			break;

		default:

			break;
		}

	}

}

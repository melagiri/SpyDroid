package com.innolabs.spydroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/** An alert dialog for user registration purpose. */
public class RegistrationDialog {

	/** String value to show the TAG for the activity in the LOG CAT */
	public static String REGDIALOG_TAG = "SPY DROID - REGISTRATION DIALOG";

	/** Shared preferences to save the Registration values */
	SharedPreferences sd_sharedprefs = null;

	/** Shared preferences editor variable to edit the Shared Preferences. */
	SharedPreferences.Editor sd_sharedprefseditor = null;

	/**
	 * Registration Dialog method is an Alert Dialog to get the registration values from the user only for the first time after installtion.
	 * 
	 * @param context The context of the Class i.e Here the Splash Activity's context.
	 * 
	 */
	@SuppressLint("NewApi")
	public void Registration_Dialog(final Context context) {

		Log.v(REGDIALOG_TAG, "Registration Dialog : Building Alert Dialog");

		// Building Alert Dialog
		AlertDialog.Builder reg_alert_dialog = new AlertDialog.Builder(context);
		reg_alert_dialog.setIcon(android.R.drawable.ic_dialog_info);
		reg_alert_dialog.setTitle("Registration");

		// Initiating the editor for the Shared Preference to save the registration values
		sd_sharedprefs = context.getSharedPreferences("SD_Prefs", Context.MODE_PRIVATE);
		sd_sharedprefseditor = sd_sharedprefs.edit();

		// Building Text View to display = "Enter your E-mail Address : "
		TextView tv_email = new TextView(context);
		tv_email.setText("Enter your E-mail Address  :");
		tv_email.setTextColor(Color.WHITE);
		LayoutParams tv_email_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tv_email.setLayoutParams(tv_email_params);

		// Building Edit Text view to add a input box for the user to fill the email address
		final EditText et_email = new EditText(context);
		LayoutParams et_email_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		et_email.setLayoutParams(et_email_params);

		// Setting the input type to the type of email address
		et_email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		// Building Text View to display = "Enter your Mobile number : "
		TextView tv_mobile_number = new TextView(context);
		tv_mobile_number.setText("Enter your Mobile Number");
		tv_mobile_number.setTextColor(Color.WHITE);
		LayoutParams tv_mobile_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tv_mobile_number.setLayoutParams(tv_mobile_params);

		// Building Edit Text view to add a input box for the user to fill the mobile number
		final EditText et_mobile_number = new EditText(context);
		LayoutParams et_mobile_params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		et_mobile_number.setLayoutParams(et_mobile_params);

		// Setting the input type to the type of Phone Number
		et_mobile_number.setInputType(InputType.TYPE_CLASS_PHONE);

		// Creating a linear layout with Vertical orientation
		LinearLayout linear_layout = new LinearLayout(context);
		linear_layout.setOrientation(LinearLayout.VERTICAL);

		// Adding Views according to their index into the Linear layout
		linear_layout.addView(tv_email);
		linear_layout.addView(et_email);
		linear_layout.addView(tv_mobile_number);
		linear_layout.addView(et_mobile_number);

		// Setting the view for the Registration Alert Dialog to Linear Layout 
		reg_alert_dialog.setView(linear_layout);

		// Adding a button for the alert dialog
		reg_alert_dialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {

			/**
			 * Method to do some actions on listening to the click of the button in the Alert Dialog
			 * 
			 * @see android.content.DialogInterface.OnClickListener#onClick(DialogInterface, int)
			 * 
			 * @param dialog The Alert Dialog interface in which the button for which the click listener is enabled
			 * @param which The integer index value of the view which is clicked
			 */
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// Storing the values from the edit text fields to strings
				String string_email = et_email.getText().toString().trim();
				String string_mobile = et_mobile_number.getText().toString().trim();

				Log.v(REGDIALOG_TAG, "OnClick : Saving the Edit Text values to Strings : email = "+string_email+" mobile = "+string_mobile);

				// Adding the string values to the Shared Preferences 
				sd_sharedprefseditor.putString("sd_email", string_email);
				sd_sharedprefseditor.putString("sd_mobile", string_mobile);
				sd_sharedprefseditor.commit();

				Log.v(REGDIALOG_TAG, "Saved the values of Strings to Shared Preferences");

				// Checking whether all the edit text fields are filled or left empty
				if(et_email.length() > 0 && et_mobile_number.length() > 0) {

					// Starting the Main Activity class
					Intent mainActivityIntent = new Intent(context, MainActivity.class);
					Toast.makeText(context, "Registration completed Successfully !", Toast.LENGTH_LONG).show();
					context.startActivity(mainActivityIntent);

					Log.v(REGDIALOG_TAG, "Starting Main Activity");

				} else {

					// Warning the user to fill all the fields
					Toast.makeText(context, "Please fill all the fields !", Toast.LENGTH_LONG).show();
				}

			}
		});

		// Displaying the Alert Dialog
		reg_alert_dialog.show();

		Log.v(REGDIALOG_TAG, "Registration Dialog Displayed");
	}

}

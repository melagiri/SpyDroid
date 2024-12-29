package com.innolabs.spydroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DataBase is a generic class for the SQLite Database creation in Android and is used to access the SQLite Database features with parameters and making it as Generic for all Database access types in the Application
 * 
 * @author Srikanth Rao
 * 
 * @see android.database.sqlite.SQLiteOpenHelper
 *
 */
public class DataBase extends SQLiteOpenHelper {

	/** String value used as a primary key for all the database tables created using @link DataBase class */
	public String SlNo = null;
	
	/** Static variable for the Context of the Database */
	static Context dbcontext;
	
	/** String value to show the TAG for the activity in the LOG CAT */
	public static String DATABASE_TAG = "SPY DROID - DATABASE";
	
	/**
	 * Simple Constructor for the DataBase class
	 * 
	 * @param context The context through which the database is accessed
	 * @param name The name of the Database that is to be accessed
	 */
	public DataBase(Context context, String name) {
		super(context, name, null, 2);
		dbcontext = context;
	}

	/**
	 * onCreate is a method in DataBase used to execSQL commands as rawQueries
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// db.execSQL("CREATE TABLE"+"CALLS"+"("+number+" TEXT,"+time+"TEXT,"+duration+",TEXT)");
		// db.execSQL("CREATE TABLE "+tablename+" (number TEXT, msg TEXT);" );
	}

	/**
	 * Method used to create a new table if it does not exist in a Database
	 * 
	 * @param table String value of the Table name
	 * @param Values String value for the Table Column names
	 */
	public void createTable(String table, String Values) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("CREATE TABLE IF NOT EXISTS " + table + "(" + Values + ")");
		
		Log.v(DATABASE_TAG, "Creating Table if not exists : "+table+" with values : "+Values);
		
	}

	/**
	 * onUpgrade method is called when the database is upgraded and need to be update in our application
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * Method called to insert values into a database table
	 * 
	 * @param table String value of the table name in which the data must be inserted
	 * @param contentval Content Value object i.e data that is to be inserted into the database table
	 */
	public void insertValues(String table, ContentValues contentval) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.insert(table, null, contentval);
		
		Log.v(DATABASE_TAG, "Content Values inserted into : "+table);
	}

	/**
	 * Delete values method called to delete the database tables
	 */
	public void deleteValues(String tableName) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE "+tableName);
		
		Log.v(DATABASE_TAG, "Dropping table : "+tableName);
		
	}

	/**
	 * Method to retreive the rows from a database table
	 * @param table Table name from which the rows must be returned
	 * @return Return the rows of the database table which was requested
	 */
	public Cursor getValues(String table) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		Log.v(DATABASE_TAG, "Performing RAW query : SELECT * FROM "+table);
		
		return db.rawQuery("SELECT * FROM " + table, null);
	}

	/**
	 * Delete Record method is called if it is needed delete a particular row from a database table
	 * @param table table name from which the row must be deleted
	 * @param id the key i.e primary key through which the row is accessed
	 */
	public void deleteRecord(String table, String id) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		String ids[] = { id };

		db.delete(table, "id" + "=?", ids);
		
		Log.v(DATABASE_TAG, "Deleting row from database table : "+table+ " where id = "+ids);
	}

	/**
	 * Method used to execute raw queries to the database
	 * 
	 * @param sql SQL command 
	 * @return null 
	 * 
	 */
	public Cursor execSQL(String sql) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(sql);
		
		Log.v(DATABASE_TAG, "Executing database SQL query : "+sql);
		
		return null;
	}
	
	/**
	 * Method used to delete all the rows from a database table
	 * @param tableName String value of table name from which all the rows must be deleted
	 */
	public void deleteTableElements(String tableName) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(tableName, null, null);
		
		Log.v(DATABASE_TAG, "Deleting rows from table : "+tableName);
		
	}
	

}

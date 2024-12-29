package com.innolabs.spydroid;

import java.io.IOException;

/**
 * Xml Builder is a class which creates XML files of the SPY data by getting the logs from the Database in which the SPY data is stored.
 * 
 * @author Srikanth Rao
 *
 */
public class XmlBuilder {

	/** Static string for adding the XML Stanza in the XML file */
	private static final String OPEN_XML_STANZA = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	/** Static string for adding the closing tick for ending the tag in XML file */
	private static final String CLOSE_WITH_TICK = "'>";

	/** Static string for opening database tag in the XML file */
	private static final String DB_OPEN = "<database name='";

	/** Static string for closing database tag in the XML file */
	private static final String DB_CLOSE = "</database>";

	/** Static string for opening database table name tag in the XML file */
	private static final String TABLE_OPEN = "<table name='";

	/** Static string for closing the database table name tag in the XMl file */
	private static final String TABLE_CLOSE = "</table>";

	/** Static string for opening a database table row tag in the XML file */
	private static final String ROW_OPEN = "<row>";

	/** Static string for closing the database table row tag in the XML file */
	private static final String ROW_CLOSE = "</row>";

	/** Static string for opening the column item tag in the XML file */
	private static final String COL_OPEN = "<col name='";

	/** Static string for closing the column item tag in the XML file */
	private static final String COL_CLOSE = "</col>";

	/** String Builder file to get the String from Database and append them into the XML file */
	private final StringBuilder sb;

	/** 
	 * XML Builder Constructor to initiate the String Builder 
	 * 
	 * @throws IOException For catching Input Output Exceptions if occured during creation of the XML file 
	 */
	public XmlBuilder() throws IOException {

		// Initializing the String Builder
		sb = new StringBuilder();
	}

	/**
	 * Method to Start creation of XML file by adding STANZA file and Main Database TAG into the XML file 
	 * 
	 * @param dbName Database Name for which the XML file is created
	 */
	void start(final String dbName) {

		//Adding the XML Stanza to the file
		sb.append(XmlBuilder.OPEN_XML_STANZA);
		
		// Adding the Main Tag with Database Name in the XML file
		sb.append(XmlBuilder.DB_OPEN + dbName + XmlBuilder.CLOSE_WITH_TICK);
	}

	/**
	 * Method that ends the Database Tag in the XML file
	 * @return String value which is appended i.e End tag for Database tag
	 * @throws IOException Exception to handle I/O runtime errors
	 */
	String end() throws IOException {
		
		// Adding DB Close Tag
		sb.append(XmlBuilder.DB_CLOSE);
		
		// Returning appended String
		return sb.toString();
	}

	/**
	 * Method to add Table name tag into the XML file
	 * @param tableName The table name that will be added as the tag into the XML file
	 */
	void openTable(final String tableName) {
		
		// Appending Table name tag to the XML file
		sb.append(XmlBuilder.TABLE_OPEN + tableName + XmlBuilder.CLOSE_WITH_TICK);
	}

	/**
	 * Method to add Table name closing tag into the XML file
	 */
	void closeTable() {
		
		// Appending Table name closing tag to the XML file
		sb.append(XmlBuilder.TABLE_CLOSE);
	}

	/**
	 * Method to add row name tag into the XML file
	 */
	void openRow() {
		
		// Appending row name tag to the XML file
		sb.append(XmlBuilder.ROW_OPEN);
	}

	/**
	 * Method to add Table name closing tag into the XML file
	 */
	void closeRow() {
		
		// Appending row close tag to the XMl file
		sb.append(XmlBuilder.ROW_CLOSE);
	}

	/**
	 * Method to add Column tag file to the XML file
	 * @param name String value of the Column name 
	 * @param val Value field to add the actual value i.e the SPY data elements
	 * @throws IOException Catches Input Output Exceptions while appending or creating the XML file
	 */
	void addColumn(final String name, final String val) throws IOException {
		
		// Appending Column name, Closing it with a tick and adding the value for the column tag
		sb.append(XmlBuilder.COL_OPEN + name + XmlBuilder.CLOSE_WITH_TICK + val + XmlBuilder.COL_CLOSE);
	}

}
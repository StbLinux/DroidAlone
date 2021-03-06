/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package com.fede;


import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DbAdapter {
  private static final String DATABASE_NAME = "homeAloneDb.db";
  
  private static final int DATABASE_VERSION = 7;
  
  //Events
  	private static final String EVENT_TABLE = "Events";
  	public static final String EVENT_DESCRIPTION_KEY = "Desc";
	public static final int EVENT_DESCRIPTION_COLUMN = 1;
	public static final String EVENT_TIME_KEY = "Time";
	public static final int EVENT_TIME_COLUMN = 2;
	public static final String SHORT_DESC_KEY = "ShortDesc";
	public static final int SHORT_DESC_COLUMN = 3;
	public static final String ROW_ID = "_id";
		
	
	
	// Missed calls
	private static final String CALL_TABLE = "Calls";
  	public static final String CALL_NUMBER_DESC_KEY = "Number";
	public static final int CALL_NUMBER_DESC_COLUMN = 1;
	public static final String CALL_TIME_KEY = "Time";
	public static final int CALL_TIME_COLUMN = 2;
	
	
	
  

  
  //private static final String CREATE_INDEX_RANGE_END = "create unique index idx_end_range on " + 
  //RANGE_TABLE + " (" + END_RANGE_KEY + ")";
  
  private static final String DATABASE_EVENT_CREATE = "create table " + 
  EVENT_TABLE + " (" + ROW_ID + 
    " integer primary key autoincrement, " +
    EVENT_DESCRIPTION_KEY + " string, " + 
    EVENT_TIME_KEY + " integer, " +
    SHORT_DESC_KEY + " string);";
  
  private static final String DATABASE_CALLS_CREATE = "create table " + 
  CALL_TABLE + " (" + ROW_ID + 
  " integer primary key autoincrement, " +
  CALL_NUMBER_DESC_KEY + " text, " + 
  EVENT_TIME_KEY + " integer);"; 
    			
    			
  // Variable to hold the database instance
  private SQLiteDatabase db;
  // Context of the application using the database.
  private final Context context;
  // Database open/upgrade helper
  private myDbHelper dbHelper;

  public DbAdapter(Context _context) {
    context = _context;
    dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

  }

  public DbAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
  }

  public void close() {
	  db.close();
  }

  //CALLS
  
//Beware that if you add a number with a heading "+" it will not be stored
  public long addCall(String number)	
  {
	  
		Date d = new Date();
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(CALL_NUMBER_DESC_KEY, number);
  	    contentValues.put(CALL_TIME_KEY, d.getTime());
  	    return db.insert(CALL_TABLE, null, contentValues);
  }
  
    

  public boolean removeCall(Long _rowIndex) {
	  return db.delete(CALL_TABLE, ROW_ID + "=" + _rowIndex, null) > 0;
  }

  public boolean removeAllCalls()
  {
		return db.delete(CALL_TABLE, null, null) > 0;
  }
   
  
  public Cursor getAllCalls () {
	String orderBy = CALL_TIME_KEY + " desc";
    return db.query(CALL_TABLE, new String[] {ROW_ID, 
    											  CALL_NUMBER_DESC_KEY, 
    											  CALL_TIME_KEY}, 
                    null, null, null, null, orderBy);
  }

  // EVENTS  
  public long addEvent(String event, String shortDesc, Date date)
  {
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(EVENT_DESCRIPTION_KEY, event);
  	    contentValues.put(EVENT_TIME_KEY, date.getTime());
  	    contentValues.put(SHORT_DESC_KEY, shortDesc);
  	    return db.insert(EVENT_TABLE, null, contentValues);
  }
  
  public long addEvent(String event, String shortDesc)
  {
	Date d = new Date();
	return addEvent(event, shortDesc, d);
  }
  

  public boolean removeEvent(Long _rowIndex) {
	  return db.delete(EVENT_TABLE, ROW_ID + "=" + _rowIndex, null) > 0;
  }

  public boolean removeAllEvents()
  {
		return db.delete(EVENT_TABLE, null, null) > 0;
  }
   
  
  public Cursor getAllEvents () {
	String orderBy = EVENT_TIME_KEY + " desc";
    return db.query(EVENT_TABLE, new String[] {ROW_ID, 
    											  EVENT_DESCRIPTION_KEY, 
    											  EVENT_TIME_KEY,
    											  SHORT_DESC_KEY}, 
                    null, null, null, null, orderBy);
  }


  
    
  public Cursor getEvent(long _rowIndex) {
    
    Cursor res = db.query(EVENT_TABLE, new String[] {ROW_ID, 
    		EVENT_DESCRIPTION_KEY, 
    		EVENT_TIME_KEY,
    		SHORT_DESC_KEY}, ROW_ID + " = " + _rowIndex, 
    		null, null, null, null);
    
    if(res != null){
    	res.moveToFirst();
    }
    
    return res;
  }
  
  

  private static class myDbHelper extends SQLiteOpenHelper {

    public myDbHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase _db) {      
      _db.execSQL(DATABASE_EVENT_CREATE);
      _db.execSQL(DATABASE_CALLS_CREATE);
    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " + 
                             _oldVersion + " to " +
                             _newVersion + ", which will destroy all old data");
        
      // Upgrade the existing database to conform to the new version. Multiple 
      // previous versions can be handled by comparing _oldVersion and _newVersion
      // values.

      // The simplest case is to drop the old table and create a new one.
      _db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE + ";");
      _db.execSQL("DROP TABLE IF EXISTS " + CALL_TABLE + ";");

      // Create a new one.
      onCreate(_db);
    }
  }
 
  /** Dummy object to allow class to compile */

}


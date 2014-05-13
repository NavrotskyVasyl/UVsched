package com.example.testwebserver;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScheduleDbOpenHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Schedule.db";
	
    public ScheduleDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create DB tables
		db.execSQL(ScheduleDbColumns.SQL_CREATE_USERS_TAB);
		db.execSQL(ScheduleDbColumns.SQL_CREATE_GROUPS_TAB);
		
		Log.i("TestWebServer", "Data base created!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// delete all tables and call onCreate method
		// TODO: Make more tolerant table upgrading here!
		db.execSQL(ScheduleDbColumns.SQL_DELETE_USERS_TAB);
		db.execSQL(ScheduleDbColumns.SQL_DELETE_GROUPS_TAB);
        onCreate(db);
        
        Log.i("TestWebServer", "Data base upgraded!");
	}

}

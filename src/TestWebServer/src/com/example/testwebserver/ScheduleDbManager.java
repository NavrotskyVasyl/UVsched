package com.example.testwebserver;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleDbManager {
	ScheduleDbOpenHelper mDbHelper;
	
	public ScheduleDbManager(Context context) {
		mDbHelper = new ScheduleDbOpenHelper(context);
	}
	

	
	
	
	
	
	
	public Boolean IsUserRegistered(String userName) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {ScheduleDbColumns.UserEntry._ID};
		String[] whereArgs  = {userName};
		
		Cursor c = db.query(ScheduleDbColumns.UserEntry.TABLE_NAME, 
				projection, 
				ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_NAME + " = ?", 
				whereArgs, 
				null, 
				null, 
				null);
		
		return (c.moveToFirst() != false);
	}
	
	// return userId if user log/pass is correct.
	// return -1 if incorrect.
	public long IsUserPasswordIsCorrect(String userName, String password) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_PASS_MD5, ScheduleDbColumns.UserEntry._ID};
		String[] whereArgs  = {userName};
		
		Cursor c = db.query(ScheduleDbColumns.UserEntry.TABLE_NAME, 
				projection, 
				ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_NAME + " = ?", 
				whereArgs, 
				null, 
				null, 
				null);
		
		if(c.moveToFirst() == false) return -1;
		
		String passMD5 = c.getString(0);
		
		Log.d("TestWebServer", passMD5);
		Log.d("TestWebServer", EncryptUtils.md5(password));
		
		if(passMD5.equals(EncryptUtils.md5(password))) return c.getLong(1);
		return -1;
	}
	
	public Boolean IsUserPasswordIsCorrect(long userId, String password) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_PASS_MD5};
		
		Cursor c = db.query(ScheduleDbColumns.UserEntry.TABLE_NAME, 
				projection, 
				ScheduleDbColumns.UserEntry._ID + " = " + userId, 
				null, null, null, null);
		
		if(c.moveToFirst() == false) return false;
		
		String passMD5 = c.getString(0);
		
		if(passMD5.equals(EncryptUtils.md5(password))) return true;
		return false;
	}
		
	// Return new user ID 
	// If user with this name is exist - return -1.
	public long CreateUser(String userName, String password) {
		if(IsUserRegistered(userName))
			return -1;
		
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_NAME, 		userName);
		values.put(ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_PASS_MD5, 	EncryptUtils.md5(password));

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
		         ScheduleDbColumns.UserEntry.TABLE_NAME,
		         null,
		         values);
		return newRowId;
	}
	
	
	// Change registered user password
	// Return userId or -1 if failed.
	public long ChangeUserPassword(long userId, String oldPassword, String newPassword) {
		if(IsUserPasswordIsCorrect(userId, oldPassword) == false)
			return -1;
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ScheduleDbColumns.UserEntry.COLUMN_NAME_USER_PASS_MD5, EncryptUtils.md5(newPassword));
		db.update(ScheduleDbColumns.UserEntry.TABLE_NAME, 
				values, 
				ScheduleDbColumns.UserEntry._ID + " = " + userId, 
				null);
		
		return userId;
	}
		
	
	
	
	
	
	
	
	/*
	 * Create new group.
	 * Return group unique ID.
	 * Return -1 if group with this name is already exist.
	 */
	public long CreateGroup(String groupName, String viewPassword, String editPassword) {
		if(GetGroupID(groupName) != -1)
			return -1;
		
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(ScheduleDbColumns.GroupEntry.COLUMN_NAME_GROUP_NAME, 		groupName);
		values.put(ScheduleDbColumns.GroupEntry.COLUMN_NAME_VIEW_PASS_MD5, 		EncryptUtils.md5(viewPassword));
		values.put(ScheduleDbColumns.GroupEntry.COLUMN_NAME_EDIT_PASS_MD5, 		EncryptUtils.md5(editPassword));

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(ScheduleDbColumns.GroupEntry.TABLE_NAME, null, values);
		return newRowId;
	}
	
	
	/*
	 * Return group ID if group with that name is exist.
	 * Return -1 if group with this name isn't exist.
	 */
	public long GetGroupID(String groupName) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {ScheduleDbColumns.GroupEntry._ID};
		String[] whereArgs  = {groupName};
		
		Cursor c = db.query(ScheduleDbColumns.GroupEntry.TABLE_NAME, 
				projection, 
				ScheduleDbColumns.GroupEntry.COLUMN_NAME_GROUP_NAME + " = ?", 
				whereArgs, 
				null, 
				null, 
				null);
		
		if(c.moveToFirst() == false) return -1;
		return c.getLong(0);
	}
	
	public class GroupUnit {
		public String 	name;
		public Boolean	visibleToAll;
		public long 	id;
	}
	
	/*
	 * Get list of all groups.
	 */
	public Vector<GroupUnit> GetAllGroups() {
		Vector<GroupUnit> res = new Vector<GroupUnit>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] projection = {ScheduleDbColumns.GroupEntry.COLUMN_NAME_GROUP_NAME,
							   ScheduleDbColumns.GroupEntry.COLUMN_NAME_VIEW_PASS_MD5,
							   ScheduleDbColumns.GroupEntry._ID};
		
		Cursor c = db.query(ScheduleDbColumns.GroupEntry.TABLE_NAME, 
				projection, 
				null,null,null, null, null);
		
		while (c.moveToNext()) {
		    GroupUnit gu = new GroupUnit();
		    gu.name = c.getString(0);
		    gu.visibleToAll = c.getString(1).equals(EncryptUtils.md5(""));
		    gu.id = c.getLong(2);
		    res.add(gu);
		}
		
		return res;
	}
	
}

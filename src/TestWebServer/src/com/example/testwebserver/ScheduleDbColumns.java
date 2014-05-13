package com.example.testwebserver;

import android.provider.BaseColumns;

public class ScheduleDbColumns {
	public ScheduleDbColumns() { /* do nothing */ }
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	
	public static abstract class UserEntry implements BaseColumns {
		public static final String TABLE_NAME 			 		= "UsersTab";
        public static final String COLUMN_NAME_USER_NAME 		= "name";
        public static final String COLUMN_NAME_USER_PASS_MD5 	= "passwordMD5";
	}
	
	public static abstract class GroupEntry implements BaseColumns {
		public static final String TABLE_NAME 			 		= "GroupsTab";
        public static final String COLUMN_NAME_GROUP_NAME 		= "name";
        public static final String COLUMN_NAME_VIEW_PASS_MD5 	= "vpassMD5";
        public static final String COLUMN_NAME_EDIT_PASS_MD5 	= "epassMD5";
	}
	
	
	public static final String SQL_CREATE_USERS_TAB =
	    "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
		UserEntry._ID + " INTEGER PRIMARY KEY," +
	    UserEntry.COLUMN_NAME_USER_NAME 	+ TEXT_TYPE + COMMA_SEP +
	    UserEntry.COLUMN_NAME_USER_PASS_MD5 + TEXT_TYPE +
	    " )";
	
	public static final String SQL_CREATE_GROUPS_TAB =
	    "CREATE TABLE " + GroupEntry.TABLE_NAME + " (" +
		GroupEntry._ID + " INTEGER PRIMARY KEY," +
	    GroupEntry.COLUMN_NAME_GROUP_NAME 		+ TEXT_TYPE + COMMA_SEP +
	    GroupEntry.COLUMN_NAME_VIEW_PASS_MD5 	+ TEXT_TYPE + COMMA_SEP +
	    GroupEntry.COLUMN_NAME_EDIT_PASS_MD5 	+ TEXT_TYPE +
	    " )";
	
	public static final String SQL_DELETE_USERS_TAB =
		    "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
	
	public static final String SQL_DELETE_GROUPS_TAB =
		    "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
}

package com.schautup.db;

interface ScheduleTbl {
	static final String ID = "_id";
	static final String TYPE = "_type";
	static final String HOUR = "_hour";
	static final String MINUTE = "_minute";
	static final String EDIT_TIME = "_edited_time";
	static final String TABLE_NAME = "schedules";

	static final String SQL_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TYPE + " INTEGER, " +
					HOUR + " INTEGER, " + MINUTE + " INTEGER, " + EDIT_TIME + " INTEGER" + ");";
}

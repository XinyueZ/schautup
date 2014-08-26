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


	static final String STMT_INSERT =
			"INSERT INTO " + TABLE_NAME + " (" + TYPE + "," + HOUR + "," + MINUTE + "," + EDIT_TIME + ")" +
					" VALUES (?,?,?,?);";

	static final String STMT_UPDATE =
			"UPDATE " + TABLE_NAME + " SET " + TYPE + " = ?, " + HOUR + " = ?, " + MINUTE + " = ?, " + EDIT_TIME +
					" = ? " + "WHERE " + ID + " = ?;";

	static final String STMT_SELECT_BY_ALL = "SELECT * FROM " + TABLE_NAME + ";";

	static final String STMT_SELECT_BY_TYPE_HOUR_MINUTE =
			"SELECT _id FROM " + TABLE_NAME + " WHERE " + TYPE + " = ? AND " + HOUR + " = ? AND " + MINUTE + " = ?;";
}

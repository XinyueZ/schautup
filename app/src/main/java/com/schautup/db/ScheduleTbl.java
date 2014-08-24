package com.schautup.db;

public interface ScheduleTbl {
	public static final String ID = "_id";
	public static final String TYPE = "_type";
	public static final String HOUR = "_hour";
	public static final String MINUTE = "_minute";
	public static final String EDIT_TIME = "_edited_time";
	public static final String TABLE_NAME = "schedules";

	public static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TYPE 	+ " INTEGER, "
			+ HOUR + " INTEGER, "
			+ MINUTE + " INTEGER, "
			+ EDIT_TIME + " INTEGER" + ");";


	public static final String STMT_INSERT = "INSERT INTO " + TABLE_NAME + " ("
			+ TYPE + ","
			+ HOUR + ","
			+ MINUTE + ","
			+ EDIT_TIME
			+ ")" + " VALUES (?,?,?,?);";

	public static final String STMT_UPDATE = "UPDATE " + TABLE_NAME + " SET "
			+ TYPE + " = ?, "
			+ HOUR + " = ?, "
			+ MINUTE + " = ?, "
			+ EDIT_TIME + " = ? "
			+ "WHERE " + ID + " = ?";
}

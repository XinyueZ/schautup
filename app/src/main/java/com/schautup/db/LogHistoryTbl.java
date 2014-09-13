package com.schautup.db;

/**
 * Table structure for all logged items.
 *
 * @author Xinyue Zhao
 */
final class LogHistoryTbl {
	static final String ID = "_id";
	static final String TYPE = "_type";
	static final String COMMENT = "_comment";
	static final String EDIT_TIME = "_edited_time";
	static final String TABLE_NAME = "log";

	//We use rowId as key for each row.
	//See. http://www.sqlite.org/autoinc.html
	/**
	 * Init new table since {@link DatabaseHelper#DATABASE_VERSION} = {@code 1}.
	 */
	static final String SQL_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + TYPE + " INTEGER, " +
					EDIT_TIME + " INTEGER" +
					");";

	/**
	 * Added new column since {@link DatabaseHelper#DATABASE_VERSION} = {@code 2}.
	 */
	static final String SQL_ALTER_ADD_COMMENT = "ALTER TABLE " + TABLE_NAME + " ADD " + COMMENT + " TEXT";
}

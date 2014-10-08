package com.schautup.db;

/**
 * Table for all filters and labels.
 * </p>
 * Labels are special filters that have not been fired to change to a schedule.
 *
 * @author Xinyue Zhao
 */
public final class FilterTbl {
	static final String ID = "_id";
	static final String NAME = "_name";
	static final String HOUR = "_hour";
	static final String MINUTE = "_minute";
	static final String RECURRENCE = "_recurrence";
	static final String TYPES = "_types";
	static final String IS_LABEL_ONLY = "_is_label_only";
	static final String EDIT_TIME = "_edited_time";
	static final String TABLE_NAME = "filters";

	//We use rowId as key for each row.
	//See. http://www.sqlite.org/autoinc.html
	/**
	 * Init new table since {@link DatabaseHelper#DATABASE_VERSION} = {@code 3}.
	 */
	static final String SQL_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + NAME + " TEXT, " +
					HOUR + " INTEGER, " + MINUTE + " INTEGER, " + RECURRENCE + " TEXT, "  + TYPES + " TEXT, " + IS_LABEL_ONLY + " INTEGER DEFAULT 0, " +
					EDIT_TIME + " INTEGER" +
					");";


	/**
	 * Added new column since {@link DatabaseHelper#DATABASE_VERSION} = {@code 4}.
	 */
	static final String SQL_ALTER_ADD_IS_LABEL_ONLY = "ALTER TABLE " + TABLE_NAME + " ADD " + IS_LABEL_ONLY + " INTEGER DEFAULT 0";
}

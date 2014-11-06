package com.schautup.db;

/**
 * Labels which are not be scheduled.
 *
 * @author Xinyue Zhao
 */
interface LabelTbl {
	static final String ID = "_id";
	static final String FILTER_ID = "_filter_id";
	static final String TYPE = "_type";
	static final String HOUR = "_hour";
	static final String MINUTE = "_minute";
	static final String RECURRENCE = "_recurrence";
	static final String RESERVE_LEFT = "_reserve_left";
	static final String RESERVE_RIGHT = "_reserve_right";
	static final String TABLE_NAME = "labels";


	/**
	 * Init new table since {@link DatabaseHelper#DATABASE_VERSION} = {@code 5}.
	 */
	static final String SQL_CREATE =
			"CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + FILTER_ID + " INTEGER, " + TYPE + " INTEGER, " +
					HOUR + " INTEGER, " + MINUTE + " INTEGER, " + RECURRENCE + " TEXT, " +	RESERVE_LEFT + " TEXT, " + RESERVE_RIGHT + " TEXT " + ");";
}

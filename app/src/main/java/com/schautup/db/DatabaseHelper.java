package com.schautup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classical helper pattern on Android DB ops.
 *
 * @author Xinyue Zhao
 */
public final class DatabaseHelper extends SQLiteOpenHelper {
	/**
	 * DB name.
	 */
	public static final String DATABASE_NAME = "schautUpDB";
	/**
	 * New version of DB.
	 * <p/>
	 * Added column {@link com.schautup.db.FilterTbl#IS_LABEL_ONLY} on {@link com.schautup.db.FilterTbl}.
	 */
	private static final int DATABASE_VERSION = 4;
	/**
	 * New version of DB.
	 * <p/>
	 * Added new table {@link com.schautup.db.FilterTbl}.
	 */
//	private static final int DATABASE_VERSION = 3;
	/**
	 * New version of DB.
	 * <p/>
	 * Added new column of "comment" on {@link com.schautup.db.LogHistoryTbl}.
	 */
//	private static final int DATABASE_VERSION = 2;

	/**
	 * Init version of DB.
	 */
	//	private static final int DATABASE_VERSION = 1;

	/**
	 * Constructor of {@link com.schautup.db.DatabaseHelper}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ScheduleTbl.SQL_CREATE);
		db.execSQL(LogHistoryTbl.SQL_CREATE);
		db.execSQL(FilterTbl.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 3 && newVersion == 4) {
			db.execSQL(FilterTbl.SQL_ALTER_ADD_IS_LABEL_ONLY);
		}
//
// else if (oldVersion == 2 && newVersion == 3) {
//			db.execSQL(FilterTbl.SQL_CREATE);
//		} else {
//			db.execSQL("DROP TABLE IF EXISTS " + ScheduleTbl.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS " + LogHistoryTbl.TABLE_NAME);
//			onCreate(db);
//		}
	}
}

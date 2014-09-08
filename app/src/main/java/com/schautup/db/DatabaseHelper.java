package com.schautup.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classical helper pattern on Android DB ops.
 *
 * @author Xinyue Zhao
 */
final class DatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "schautUpDB";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ScheduleTbl.SQL_CREATE);
		db.execSQL(LogHistoryTbl.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ScheduleTbl.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + LogHistoryTbl.TABLE_NAME);
		onCreate(db);
	}
}

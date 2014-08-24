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
	private static final String DATABASE_NAME = "schautUpDB";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper(Context _context) {
		super(_context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase _db) {
		_db.execSQL(ScheduleTbl.SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
		_db.execSQL("DROP TABLE IF EXISTS " + ScheduleTbl.TABLE_NAME);
		_db.execSQL(ScheduleTbl.SQL_CREATE);
	}
}

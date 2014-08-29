package com.schautup.db;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;
import com.schautup.exceptions.AddSameDataException;

/**
 * Defines methods that operate on database.
 * <p/>
 * <b>Singleton pattern.</b>
 * <p/>
 * <p/>
 *
 * @author Xinyue Zhao
 */
public final class DB {
	/**
	 * {@link android.content.Context}.
	 */
	private Context mContext;
	/**
	 * Impl singleton pattern.
	 */
	private static DB sInstance;
	/**
	 * Helper class that create, delete, update tables of database.
	 */
	private DatabaseHelper mDatabaseHelper;
	/**
	 * The database object.
	 */
	private SQLiteDatabase mDB;

	/**
	 * Constructor of {@link com.schautup.db.DB}. Impl singleton pattern so that it is private.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	private DB(Context cxt) {
		mContext = cxt;
	}

	/**
	 * Get instance of  {@link com.schautup.db.DB} singleton.
	 *
	 * @param _context
	 * 		{@link android.app.Application}.
	 * @return The {@link com.schautup.db.DB} singleton.
	 */
	public static DB getInstance(Application _context) {
		if (sInstance == null) {
			sInstance = new DB(_context);
		}
		return sInstance;
	}

	/**
	 * Open database.
	 */
	public synchronized void open() {
		mDatabaseHelper = new DatabaseHelper(mContext);
		mDB = mDatabaseHelper.getWritableDatabase();
	}

	/**
	 * Close database.
	 */
	public synchronized void close() {
		mDatabaseHelper.close();
	}


	/**
	 * Add a schedule into DB. {@link com.schautup.exceptions.AddSameDataException} might be caught when user tries to
	 * insert same data.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to insert.
	 * @return {@code true} if insert is success.
	 * @throws AddSameDataException
	 */
	public synchronized boolean addSchedule(ScheduleItem item) throws AddSameDataException {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		Cursor c = null;
		try {
			long rowId = -1;
			c = mDB.query(ScheduleTbl.TABLE_NAME, null, ScheduleTbl.TYPE + " = ? AND " +
					ScheduleTbl.HOUR + " = ? AND " + ScheduleTbl.MINUTE + " = ?",
					new String[] { item.getType().toCode() + "", item.getHour() + "", item.getMinute() + "" }, null,
					null, null, null);
			if (c.getCount() == 1) {
				c.moveToNext();
				throw new AddSameDataException(new ScheduleItem(c.getInt(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getInt(c.getColumnIndex(ScheduleTbl.EDIT_TIME))));
			} else {
				ContentValues v = new ContentValues();
				v.put(ScheduleTbl.TYPE, item.getType().toCode());
				v.put(ScheduleTbl.HOUR, item.getHour());
				v.put(ScheduleTbl.MINUTE, item.getMinute());
				v.put(ScheduleTbl.EDIT_TIME, System.currentTimeMillis());
				rowId = mDB.insert(ScheduleTbl.TABLE_NAME, null, v);
				success = rowId != -1;
			}
		} catch (AddSameDataException e) {
			throw e;
		} finally {
			if (c != null) {
				c.close();
			}
			close();
		}
		return success;
	}

	/**
	 * Update a schedule in DB. {@link com.schautup.exceptions.AddSameDataException} might be caught when user
	 * tries to insert same data after being udpate.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to insert.
	 * @return {@code true} if insert is success.
	 * @throws AddSameDataException
	 */
	public synchronized boolean updateSchedule(ScheduleItem item) throws AddSameDataException {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		Cursor c = null;
		try {
			long rowId = -1;
			c = mDB.query(ScheduleTbl.TABLE_NAME, null, ScheduleTbl.TYPE + " = ? AND " +
							ScheduleTbl.HOUR + " = ? AND " + ScheduleTbl.MINUTE + " = ?",
					new String[] { item.getType().toCode() + "", item.getHour() + "", item.getMinute() + "" }, null,
					null, null, null);
			if (c.getCount() >= 1) {
				c.moveToNext();
				throw new AddSameDataException(new ScheduleItem(c.getInt(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getInt(c.getColumnIndex(ScheduleTbl.EDIT_TIME))));
			} else {
				ContentValues v = new ContentValues();
				v.put(ScheduleTbl.TYPE, item.getType().toCode());
				v.put(ScheduleTbl.HOUR, item.getHour());
				v.put(ScheduleTbl.MINUTE, item.getMinute());
				v.put(ScheduleTbl.EDIT_TIME, System.currentTimeMillis());
				String[] args = new String[]{item.getId() + ""};
				rowId = mDB.update(ScheduleTbl.TABLE_NAME, v, ScheduleTbl.ID + " = ?", args);
				success = rowId != -1;
			}
		} catch (AddSameDataException e) {
			throw e;
		} finally {
			if (c != null) {
				c.close();
			}
			close();
		}
		return success;
	}


	/**
	 * Returns all {@link com.schautup.data.ScheduleItem}s from DB.
	 *
	 * @return All {@link com.schautup.data.ScheduleItem}s from DB.
	 */
	public synchronized  List<ScheduleItem> getAllSchedules() {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, null, null, null, null, null);
		ScheduleItem item = null;
		List<ScheduleItem> list = new ArrayList<ScheduleItem>();
		try {
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getInt(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getInt(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));

				list.add(item);
			}

		} finally {
			if (c != null) {
				c.close();
			}
			close();
			return list;
		}
	}
}

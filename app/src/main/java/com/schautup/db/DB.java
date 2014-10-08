package com.schautup.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.data.Filter;
import com.schautup.data.HistoryItem;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;

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
	 * Divide for all selected types for filters.
	 */
	private static final String DIV = "|";
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
	 *
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
	 * Add a schedule into DB.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to insert.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean addSchedule(ScheduleItem item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		try {
			long rowId = -1;
			//Do "insert" command.
			ContentValues v = new ContentValues();
			v.put(ScheduleTbl.TYPE, item.getType().toCode());
			v.put(ScheduleTbl.HOUR, item.getHour());
			v.put(ScheduleTbl.MINUTE, item.getMinute());
			v.put(ScheduleTbl.RECURRENCE, item.getEventRecurrence().toString());
			v.put(ScheduleTbl.RESERVE_LEFT, item.getReserveLeft());
			v.put(ScheduleTbl.RESERVE_RIGHT, item.getReserveRight());
			v.put(ScheduleTbl.EDIT_TIME, System.currentTimeMillis());
			rowId = mDB.insert(ScheduleTbl.TABLE_NAME, null, v);
			item.setId(rowId);
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}

	/**
	 * Update a schedule in DB.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to insert.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean updateSchedule(ScheduleItem item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		try {
			long rowId = -1;
			//Do "update" command.
			ContentValues v = new ContentValues();
			v.put(ScheduleTbl.TYPE, item.getType().toCode());
			v.put(ScheduleTbl.HOUR, item.getHour());
			v.put(ScheduleTbl.MINUTE, item.getMinute());
			v.put(ScheduleTbl.RECURRENCE, item.getEventRecurrence().toString());
			v.put(ScheduleTbl.RESERVE_LEFT, item.getReserveLeft());
			v.put(ScheduleTbl.RESERVE_RIGHT, item.getReserveRight());
			v.put(ScheduleTbl.EDIT_TIME, System.currentTimeMillis());
			String[] args = new String[] { item.getId() + "" };
			rowId = mDB.update(ScheduleTbl.TABLE_NAME, v, ScheduleTbl.ID + " = ?", args);
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}

	/**
	 * Log a history.
	 *
	 * @param item
	 * 		An item to log.
	 *
	 * @return {@code} The log is successfully.
	 */
	public synchronized boolean logHistory(HistoryItem item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		try {
			long rowId = -1;
			//Do "insert" command.
			ContentValues v = new ContentValues();
			v.put(LogHistoryTbl.TYPE, item.getType().toCode());
			v.put(LogHistoryTbl.COMMENT, item.getComment());
			v.put(LogHistoryTbl.EDIT_TIME, System.currentTimeMillis());
			rowId = mDB.insert(LogHistoryTbl.TABLE_NAME, null, v);
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}


	/**
	 * Remove one history from DB.
	 *
	 * @param item
	 * 		The item to remove.
	 *
	 * @return The count of rows remain in DB after removed item.
	 * <p/>
	 * Return -1 if there's error when removed data.
	 */
	public synchronized int removeHistory(HistoryItem item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		int rowsRemain = -1;
		boolean success;
		try {
			long rowId;
			String whereClause = LogHistoryTbl.ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(item.getId()) };
			rowId = mDB.delete(LogHistoryTbl.TABLE_NAME, whereClause, whereArgs);
			success = rowId > 0;
			if (success) {
				Cursor c = mDB.query(LogHistoryTbl.TABLE_NAME, new String[] { LogHistoryTbl.ID }, null, null, null,
						null, null);
				rowsRemain = c.getCount();
			} else {
				rowsRemain = -1;
			}
		} finally {
			close();
		}
		return rowsRemain;
	}

	/**
	 * Returns all {@link com.schautup.data.HistoryItem}s from DB.
	 *
	 * @return All {@link com.schautup.data.HistoryItem}s from DB.
	 */
	public synchronized List<HistoryItem> getAllHistories() {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(LogHistoryTbl.TABLE_NAME, null, null, null, null, null, LogHistoryTbl.EDIT_TIME + " DESC");
		HistoryItem item = null;
		List<HistoryItem> list = new LinkedList<HistoryItem>();
		try {
			while (c.moveToNext()) {
				item = new HistoryItem(c.getLong(c.getColumnIndex(LogHistoryTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(LogHistoryTbl.TYPE))), c.getLong(c.getColumnIndex(LogHistoryTbl.EDIT_TIME)));
				item.setComment(c.getString(c.getColumnIndex(LogHistoryTbl.COMMENT)));
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


	/**
	 * Find whether there's a stored item that has same type, hour, minute.
	 *
	 * @param type
	 * 		Type of item.
	 * @param hour
	 * 		Hour.
	 * @param minute
	 * 		Minute.
	 *
	 * @return {@code true} if there's an item that is duplicated.
	 */
	public synchronized boolean findDuplicatedItem(ScheduleType type, int hour, int minute) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean duplicated = false;
		Cursor c = null;
		try {
			c = mDB.query(ScheduleTbl.TABLE_NAME, null, ScheduleTbl.TYPE + " = ? AND " +
							ScheduleTbl.HOUR + " = ? AND " + ScheduleTbl.MINUTE + " = ?",
					new String[] { type.toCode() + "", hour + "", minute + "" }, null, null, null, null);
			duplicated = c.getCount() >= 1;
		} finally {
			if (c != null) {
				c.close();
			}
			close();
		}
		return duplicated;
	}


	/**
	 * Returns all {@link com.schautup.data.ScheduleItem}s from DB.
	 *
	 * @return All {@link com.schautup.data.ScheduleItem}s from DB.
	 */
	public synchronized List<ScheduleItem> getAllSchedules() {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, null, null, null, null, null);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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

	/**
	 * Returns all {@link com.schautup.data.ScheduleItem}s from DB order by the edited time.
	 *
	 * @param direction  "DESC" or "ASC".
	 * @return All {@link com.schautup.data.ScheduleItem}s from DB order by the edited time.
	 */
	public synchronized List<ScheduleItem> getAllSchedulesOrderByEditTime(String direction) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, null, null, null, null, ScheduleTbl.EDIT_TIME + " " + direction);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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

	/**
	 * Returns all {@link com.schautup.data.ScheduleItem}s from DB order by the time.
	 *
	 * @param direction  "DESC" or "ASC".
	 * @return All {@link com.schautup.data.ScheduleItem}s from DB order by the time.
	 */
	public synchronized List<ScheduleItem> getAllSchedulesOrderByScheduleTime(String direction) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, null, null, null, null,
				ScheduleTbl.HOUR + " " + direction + "," + ScheduleTbl.MINUTE + " " + direction);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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

	/**
	 * Returns {@link com.schautup.data.ScheduleItem}s from DB by hour , minute and day in week.
	 *
	 * @param hour
	 * 		Hour.
	 * @param minute
	 * 		Minute.
	 * @param byDay
	 * 		Recurrence day in week.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}s from DB by hour , minute and day in week.
	 */
	public synchronized List<ScheduleItem> getSchedules(int hour, int minute, String byDay) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, ScheduleTbl.HOUR + " = ? AND " + ScheduleTbl.MINUTE + " = " +
						"? AND " + ScheduleTbl.RECURRENCE + " LIKE '%BYDAY=%" + byDay + "%'",
				new String[] { hour + "", minute + "" }, null, null, null, null);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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

	/**
	 * Returns {@link com.schautup.data.ScheduleItem}s from DB by id,  and day in week.
	 *
	 * @param id
	 * 		Location id of the item in DB. 
	 * @param byDay
	 * 		Recurrence day in week.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}s from DB by id, hour , minute and day in week.
	 */
	public synchronized List<ScheduleItem> getSchedules(long id, String byDay) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null,  ScheduleTbl.RECURRENCE + " LIKE '%BYDAY=%" + byDay + "%' AND " + ScheduleTbl.ID +
						" = ?", new String[] {  id + "" }, null, null, null, null);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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


	/**
	 * Returns {@link com.schautup.data.ScheduleItem}s from DB by  hour , minute , type and recurrence which have been filtered.
	 *
	 * @param hour
	 * 		Hour.
	 * @param minute
	 * 		Minute.
	 * @param types
	 * 		A list of selected {@link com.schautup.data.ScheduleType}.
	 * @param eventRecurrence
	 * 		{@link EventRecurrence}.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}s from DB by hour , minute , type and recurrence.
	 */
	public synchronized List<ScheduleItem> getFilteredSchedules(int hour, int minute,
			SparseArrayCompat<ScheduleType> types, EventRecurrence eventRecurrence) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		Cursor c = null;
		ScheduleType type;
		ScheduleItem item;
		List<ScheduleItem> list;
		try {
			int key;
			for (int i = 0; i < types.size(); i++) {
				key = types.keyAt(i);
				type = types.get(key);
				c = mDB.query(ScheduleTbl.TABLE_NAME, null, "("+ ScheduleTbl.HOUR + " = ? AND " + ScheduleTbl.MINUTE + " = " +
								"? OR " + ScheduleTbl.RECURRENCE + " = ?) AND " + ScheduleTbl.TYPE + " = ?", new String[] { hour + "", minute + "", eventRecurrence.toString(), type.getCode() + "" }, null,
						null, null, null);
				list = new LinkedList<ScheduleItem>();
				EventRecurrence er;
				while (c.moveToNext()) {
					item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
							c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(
							ScheduleTbl.EDIT_TIME)));
					er = new EventRecurrence();
					er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
					item.setEventRecurrence(er);
					item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
					item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
					list.add(item);
				}
				items.addAll(list);
			}
		} finally {
			if (c != null) {
				c.close();
			}
			close();
			return items;
		}
	}


	/**
	 * Returns {@link com.schautup.data.ScheduleItem}s from DB by asking id.
	 *
	 * @param id
	 * 		Location id of the item in DB.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}s from DB by asking id.
	 */
	public synchronized List<ScheduleItem> getSchedules(long id) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, null, ScheduleTbl.ID + " = ?", new String[] { id + "" }, null,
				null, null, null);
		ScheduleItem item = null;
		List<ScheduleItem> list = new LinkedList<ScheduleItem>();
		try {
			EventRecurrence er;
			while (c.moveToNext()) {
				item = new ScheduleItem(c.getLong(c.getColumnIndex(ScheduleTbl.ID)), ScheduleType.fromCode(c.getInt(
						c.getColumnIndex(ScheduleTbl.TYPE))), c.getInt(c.getColumnIndex(ScheduleTbl.HOUR)), c.getInt(
						c.getColumnIndex(ScheduleTbl.MINUTE)), c.getLong(c.getColumnIndex(ScheduleTbl.EDIT_TIME)));
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(ScheduleTbl.RECURRENCE)));
				item.setEventRecurrence(er);
				item.setReserveLeft(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_LEFT)));
				item.setReserveRight(c.getString(c.getColumnIndex(ScheduleTbl.RESERVE_RIGHT)));
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

	/**
	 * Remove one schedule item from DB.
	 *
	 * @param item
	 * 		The item to remove.
	 *
	 * @return The count of rows remain in DB after removed item.
	 * <p/>
	 * Return -1 if there's error when removed data.
	 */
	public synchronized int removeSchedule(ScheduleItem item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		int rowsRemain = -1;
		boolean success;
		try {
			long rowId;
			String whereClause = ScheduleTbl.ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(item.getId()) };
			rowId = mDB.delete(ScheduleTbl.TABLE_NAME, whereClause, whereArgs);
			success = rowId > 0;
			if (success) {
				Cursor c = mDB.query(ScheduleTbl.TABLE_NAME, new String[] { ScheduleTbl.ID }, null, null, null, null,
						null);
				rowsRemain = c.getCount();
			} else {
				rowsRemain = -1;
			}
		} finally {
			close();
		}
		return rowsRemain;
	}

	/**
	 * Returns all {@link com.schautup.data.Filter}s from DB order by edited time.
	 *
	 * @return All {@link com.schautup.data.Filter}s from DB order by edited time.
	 */
	public synchronized List<Filter> getAllFilters() {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		Cursor c = mDB.query(FilterTbl.TABLE_NAME, null, FilterTbl.IS_LABEL_ONLY + " = ?",
				new String[]{"0"}, null, null, FilterTbl.EDIT_TIME + " DESC");
		Filter item = null;
		List<Filter> list = new LinkedList<Filter>();
		try {
			EventRecurrence er;
			String types;
			while (c.moveToNext()) {
				er = new EventRecurrence();
				er.parse(c.getString(c.getColumnIndex(FilterTbl.RECURRENCE)));
				item = new Filter(c.getLong(c.getColumnIndex(FilterTbl.ID)), c.getString(c.getColumnIndex(
						FilterTbl.NAME)), c.getInt(c.getColumnIndex(FilterTbl.HOUR)), c.getInt(c.getColumnIndex(
						FilterTbl.MINUTE)), er, c.getLong(c.getColumnIndex(FilterTbl.EDIT_TIME)));
				types = c.getString(c.getColumnIndex(FilterTbl.TYPES));
				String[] typesArr = types.split(DIV);
				ScheduleType sc;
				if (typesArr != null && typesArr.length > 0) {
					for (String t : typesArr) {
						if (!TextUtils.isEmpty(t) && !TextUtils.equals(DIV, t)) {
							sc = ScheduleType.fromCode(Integer.parseInt(t));
							item.getSelectedTypes().put(sc.getCode(), sc);
						}
					}
				}
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

	/**
	 * Add a filter into DB.
	 *
	 * @param item
	 * 		{@link com.schautup.data.Filter} to insert.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean addFilter(Filter item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		try {
			long rowId = -1;
			//Do "insert" command.
			ContentValues v = new ContentValues();
			SparseArrayCompat<ScheduleType> types = item.getSelectedTypes();
			StringBuilder stringBuilder = convertTypesForFilter(types);
			v.put(FilterTbl.NAME, item.getName());
			v.put(FilterTbl.HOUR, item.getHour());
			v.put(FilterTbl.MINUTE, item.getMinute());
			v.put(FilterTbl.RECURRENCE, item.getEventRecurrence().toString());
			v.put(FilterTbl.TYPES, stringBuilder.toString());
			v.put(FilterTbl.EDIT_TIME, System.currentTimeMillis());
			rowId = mDB.insert(FilterTbl.TABLE_NAME, null, v);
			item.setId(rowId);
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}


	/**
	 * Update a schedule in DB.
	 *
	 * @param item
	 * 		{@link com.schautup.data.Filter} to insert.
	 *
	 * @return {@code true} if insert is success.
	 */
	public synchronized boolean updateFilter(Filter item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		boolean success = false;
		try {
			long rowId = -1;
			//Do "update" command.
			ContentValues v = new ContentValues();
			SparseArrayCompat<ScheduleType> types = item.getSelectedTypes();
			StringBuilder stringBuilder = convertTypesForFilter(types);
			v.put(FilterTbl.NAME, item.getName());
			v.put(FilterTbl.HOUR, item.getHour());
			v.put(FilterTbl.MINUTE, item.getMinute());
			v.put(FilterTbl.RECURRENCE, item.getEventRecurrence().toString());
			v.put(FilterTbl.TYPES, stringBuilder.toString());
			String[] args = new String[] { item.getId() + "" };
			rowId = mDB.update(FilterTbl.TABLE_NAME, v, FilterTbl.ID + " = ?", args);
			success = rowId != -1;
		} finally {
			close();
		}
		return success;
	}

	/**
	 * Remove one {@link com.schautup.data.Filter} from DB.
	 *
	 * @param item
	 * 		The item to remove.
	 *
	 * @return The count of rows remain in DB after removed item.
	 * <p/>
	 * Return -1 if there's error when removed data.
	 */
	public synchronized int removeFilter(Filter item) {
		if (mDB == null || !mDB.isOpen()) {
			open();
		}
		int rowsRemain = -1;
		boolean success;
		try {
			long rowId;
			String whereClause = FilterTbl.ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(item.getId()) };
			rowId = mDB.delete(FilterTbl.TABLE_NAME, whereClause, whereArgs);
			success = rowId > 0;
			if (success) {
				Cursor c = mDB.query(FilterTbl.TABLE_NAME, new String[] { FilterTbl.ID }, null, null, null, null, null);
				rowsRemain = c.getCount();
			} else {
				rowsRemain = -1;
			}
		} finally {
			close();
		}
		return rowsRemain;
	}

	/**
	 * Helper method to convert list of selected types of a {@link com.schautup.data.Filter} to a string with sep {@link
	 * #DIV}.
	 *
	 * @param types
	 * 		A list of all {@link com.schautup.data.Filter}s.
	 *
	 * @return Selected {@link com.schautup.data.Filter}s in string with sep {@link #DIV}.
	 */
	private static StringBuilder convertTypesForFilter(SparseArrayCompat<ScheduleType> types) {
		StringBuilder stringBuilder = new StringBuilder();
		int key;
		ScheduleType type;
		for (int i = 0; i < types.size(); i++) {
			key = types.keyAt(i);
			type = types.get(key);
			stringBuilder.append(type.getCode());
			if (i != types.size() - 1) {//Last one?
				stringBuilder.append(DIV);
			}
		}
		return stringBuilder;
	}
}
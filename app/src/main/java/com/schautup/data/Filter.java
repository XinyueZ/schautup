package com.schautup.data;

import android.support.v4.util.SparseArrayCompat;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;

/**
 * A filter.
 *
 * @author Xinyue Zhao
 */
public final class Filter {
	/**
	 * Id in database.
	 */
	private long mId;
	/**
	 * Name of filter.
	 */
	private String mName;
	/**
	 * Selected hour.
	 */
	private int mHour;
	/**
	 * Selected minute.
	 */
	private int mMinute;
	/**
	 * The recurrence defined in filter.
	 */
	private EventRecurrence mEventRecurrence;
	/**
	 * Selected types.
	 */
	private SparseArrayCompat<ScheduleType> mSelectedTypes = new SparseArrayCompat<ScheduleType>();


	/**
	 * Constructor of {@link Filter}.
	 *
	 * @param id
	 * 		Id in database.
	 * @param name
	 *      Name of filter.
	 * @param hour
	 * 		Selected hour.
	 * @param minute
	 * 		Selected minute.
	 * @param eventRecurrence
	 * 		The recurrence defined in filter.
	 */
	public Filter(long id, String name, int hour, int minute, EventRecurrence eventRecurrence) {
		mId = id;
		mName = name;
		mHour = hour;
		mMinute = minute;
		mEventRecurrence = eventRecurrence;
	}

	/**
	 * Constructor of {@link Filter}.
	 * @param name
	 *      Name of filter.
	 * @param hour
	 * 		Selected hour.
	 * @param minute
	 * 		Selected minute.
	 * @param eventRecurrence
	 * 		The recurrence defined in filter.
	 */
	public Filter(String name, int hour, int minute, EventRecurrence eventRecurrence) {
		this(-1, name, hour, minute, eventRecurrence);
	}
	/**
	 * Get id in database.
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Get name of filter.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Get selected hour.
	 */
	public int getHour() {
		return mHour;
	}
	/**
	 * Get selected minute.
	 */
	public int getMinute() {
		return mMinute;
	}
	/**
	 * Get the recurrence defined in filter.
	 */
	public EventRecurrence getEventRecurrence() {
		return mEventRecurrence;
	}
	/**
	 * Get selected types.
	 * @return Selected types.
	 */
	public SparseArrayCompat<ScheduleType> getSelectedTypes() {
		return mSelectedTypes;
	}


	public Filter() {

	}

	public void setId(long _id) {
		mId = _id;
	}

	public void setName(String _name) {
		mName = _name;
	}

	public void setHour(int _hour) {
		mHour = _hour;
	}

	public void setMinute(int _minute) {
		mMinute = _minute;
	}

	public void setEventRecurrence(EventRecurrence _eventRecurrence) {
		mEventRecurrence = _eventRecurrence;
	}
}

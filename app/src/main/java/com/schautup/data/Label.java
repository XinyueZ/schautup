package com.schautup.data;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;

/**
 * A {@link Label} is a atom mark for a pre-scheduled item. It is a part of filter which has not been available.
 *
 * @author Xinyue Zhao
 */
public final class Label {
	/**
	 * The id of a label.
	 */
	private long mId;
	/**
	 * The id of a filter which has has not been available.
	 */
	private long mIdFilter;
	/**
	 * Different types scheduled.
	 */
	private ScheduleType mType;
	/**
	 * Hour.
	 */
	private int mHour;
	/**
	 * Minute.
	 */
	private int mMinute;
	/**
	 * The {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 */
	private EventRecurrence mEventRecurrence;
	/**
	 * The reserve data for some settings.
	 */
	private String mReserveLeft;
	/**
	 * The reserve data for some settings.
	 */
	private String mReserveRight;

	/**
	 * Constructor of {@link Label}.
	 * @param type Different types scheduled.
	 * @param hour The hour
	 * @param minute The minute
	 * @param recurrence The recurrence
	 * @param reserveLeft The reserve data for some settings.
	 * @param reserveRight The reserve data for some settings.
	 */
	public Label( ScheduleType type,int hour, int minute, EventRecurrence recurrence, String reserveLeft, String reserveRight) {
		this(-1, -1, type,   hour,   minute,   recurrence, reserveLeft, reserveRight);
	}


	/**
	 * Constructor of {@link Label}.
	 * @param id The id of a label.
	 * @param idFilter The id of a filter which has has not been available.
	 * @param type Different types scheduled.
	 * @param hour The hour
	 * @param minute The minute
	 * @param recurrence The recurrence
	 * @param reserveLeft The reserve data for some settings.
	 * @param reserveRight The reserve data for some settings.
	 */
	public Label(long id, long idFilter, ScheduleType type, int hour, int minute, EventRecurrence recurrence, String reserveLeft, String reserveRight) {
		mId = id;
		mIdFilter = idFilter;
		mType = type;
		mHour = hour;
		mMinute = minute;
		mEventRecurrence = recurrence;
		mReserveLeft = reserveLeft;
		mReserveRight = reserveRight;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Sets id.
	 *
	 * @param id the id
	 */
	public void setId(long id) {
		mId = id;
	}

	/**
	 * Gets id filter.
	 *
	 * @return the id filter
	 */
	public long getIdFilter() {
		return mIdFilter;
	}

	/**
	 * Sets id filter.
	 *
	 * @param idFilter the id filter
	 */
	public void setIdFilter(long idFilter) {
		mIdFilter = idFilter;
	}

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public ScheduleType getType() {
		return mType;
	}

	/**
	 * Sets type.
	 *
	 * @param type the type
	 */
	public void setType(ScheduleType type) {
		mType = type;
	}

	/**
	 * Gets hour.
	 *
	 * @return the hour
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * Sets hour.
	 *
	 * @param hour the hour
	 */
	public void setHour(int hour) {
		mHour = hour;
	}

	/**
	 * Gets minute.
	 *
	 * @return the minute
	 */
	public int getMinute() {
		return mMinute;
	}

	/**
	 * Sets minute.
	 *
	 * @param minute the minute
	 */
	public void setMinute(int minute) {
		mMinute = minute;
	}

	/**
	 * Gets event recurrence.
	 *
	 * @return the event recurrence
	 */
	public EventRecurrence getEventRecurrence() {
		return mEventRecurrence;
	}


	/**
	 * Gets reserve left.
	 *
	 * @return the reserve left
	 */
	public String getReserveLeft() {
		return mReserveLeft;
	}


	/**
	 * Gets reserve right.
	 *
	 * @return the reserve right
	 */
	public String getReserveRight() {
		return mReserveRight;
	}

}

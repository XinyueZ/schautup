package com.schautup.data;

/**
 * Data structure of a schedule item.
 */
public final class ScheduleItem {
	/**
	 * Possible Id if data instance is from DB.
	 */
	private int mId;
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
	 * Edited time.
	 */
	private long mEditedTime;

	/**
	 * Constructor of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _id
	 * 		Possible Id if data instance is from DB.
	 * @param _type
	 * 		{@link com.schautup.data.ScheduleType}. Different types scheduled.
	 * @param _hour
	 * 		Hour.
	 * @param _minute
	 * 		Minute.
	 * @param __editedTime
	 * 		Created time.
	 */
	public ScheduleItem(int _id, ScheduleType _type, int _hour, int _minute, long __editedTime) {
		mId = _id;
		mType = _type;
		mHour = _hour;
		mMinute = _minute;
		mEditedTime = __editedTime;
	}

	/**
	 * Constructor of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _type
	 * 		{@link com.schautup.data.ScheduleType}. Different types scheduled.
	 * @param _hour
	 * 		Hour.
	 * @param _minute
	 * 		Minute.
	 */
	public ScheduleItem(ScheduleType _type, int _hour, int _minute) {
		mType = _type;
		mHour = _hour;
		mMinute = _minute;
	}


	/**
	 * Constructor of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _type
	 * 		{@link com.schautup.data.ScheduleType}. Different types scheduled.
	 * @param _hour
	 * 		Hour.
	 * @param _minute
	 * 		Minute.
	 * @param __editedTime
	 * 		Created time.
	 */
	public ScheduleItem(ScheduleType _type, int _hour, int _minute, long __editedTime) {
		this(-1, _type, _hour, _minute, __editedTime);
	}

	/**
	 * Get {@link com.schautup.data.ScheduleType}.
	 *
	 * @return {@link com.schautup.data.ScheduleType}.
	 */
	public ScheduleType getType() {
		return mType;
	}

	/**
	 * Set {@link com.schautup.data.ScheduleType}.
	 *
	 * @param _type
	 * 		{@link com.schautup.data.ScheduleType}.
	 */
	public void setType(ScheduleType _type) {
		mType = _type;
	}

	/**
	 * Get Hour.
	 *
	 * @return Hour.
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * Set Hour.
	 *
	 * @param _hour
	 * 		Hour.
	 */
	public void setHour(int _hour) {
		mHour = _hour;
	}

	/**
	 * Get minute.
	 *
	 * @return Minute.
	 */
	public int getMinute() {
		return mMinute;
	}

	/**
	 * Set minute.
	 *
	 * @param _minute
	 * 		_minute.
	 */
	public void setMinute(int _minute) {
		mMinute = _minute;
	}

	/**
	 * Get edited time.
	 */
	public long getEditedTime() {
		return mEditedTime;
	}

	/**
	 * Set edited time.
	 * <p/>
	 *
	 * @param _editedTime
	 * 		The created time.
	 */
	public void setEditedTime(long _editedTime) {
		mEditedTime = _editedTime;
	}
}

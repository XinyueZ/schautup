package com.schautup.data;

/**
 * Data structure of a schedule item.
 */
public final class ScheduleItem {
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
	 * Created time.
	 */
	private long mCreationTime;

	/**
	 * Constructor of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _type
	 * 		{@link com.schautup.data.ScheduleType}. Different types scheduled.
	 * @param _hour
	 * 		Hour.
	 * @param _minute
	 * 		Minute.
	 * @param _creationTime
	 * 		Created time.
	 */
	public ScheduleItem(ScheduleType _type, int _hour, int _minute, long _creationTime) {
		mType = _type;
		mHour = _hour;
		mMinute = _minute;
		mCreationTime = _creationTime;
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
	 * Get created time.
	 */
	public long getCreationTime() {
		return mCreationTime;
	}

	/**
	 * Set created time.
	 * <p/>
	 *
	 * @param _creationTime
	 * 		The created time.
	 */
	public void setCreationTime(long _creationTime) {
		mCreationTime = _creationTime;
	}
}

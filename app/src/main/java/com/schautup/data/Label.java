package com.schautup.data;

/**
 * A {@link com.schautup.data.Label} is a atom mark for a pre-scheduled item. It is a part of filter which has not been available.
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
	 * The reserve data for some settings.
	 */
	private String mReserveLeft;
	/**
	 * The reserve data for some settings.
	 */
	private String mReserveRight;

	/**
	 * Constructor of {@link com.schautup.data.Label}.
	 * @param type Different types scheduled.
	 * @param reserveLeft The reserve data for some settings.
	 * @param reserveRight The reserve data for some settings.
	 */
	public Label( ScheduleType type, String reserveLeft, String reserveRight) {
		this(-1, -1, type, reserveLeft, reserveRight);
	}



	/**
	 * Constructor of {@link com.schautup.data.Label}.
	 * @param idFilter The id of a filter which has has not been available.
	 * @param type Different types scheduled.
	 * @param reserveLeft The reserve data for some settings.
	 * @param reserveRight The reserve data for some settings.
	 */
	public Label(long idFilter, ScheduleType type, String reserveLeft, String reserveRight) {
		this(-1, idFilter, type, reserveLeft, reserveRight);
	}

	/**
	 * Constructor of {@link com.schautup.data.Label}.
	 * @param id The id of a label.
	 * @param idFilter The id of a filter which has has not been available.
	 * @param type Different types scheduled.
	 * @param reserveLeft The reserve data for some settings.
	 * @param reserveRight The reserve data for some settings.
	 */
	public Label(long id, long idFilter, ScheduleType type, String reserveLeft, String reserveRight) {
		mId = id;
		mIdFilter = idFilter;
		mType = type;
		mReserveLeft = reserveLeft;
		mReserveRight = reserveRight;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public long getIdFilter() {
		return mIdFilter;
	}

	public void setIdFilter(long idFilter) {
		mIdFilter = idFilter;
	}

	public ScheduleType getType() {
		return mType;
	}

	public void setType(ScheduleType type) {
		mType = type;
	}

	public String getReserveLeft() {
		return mReserveLeft;
	}

	public void setReserveLeft(String reserveLeft) {
		mReserveLeft = reserveLeft;
	}

	public String getReserveRight() {
		return mReserveRight;
	}

	public void setReserveRight(String reserveRight) {
		mReserveRight = reserveRight;
	}
}

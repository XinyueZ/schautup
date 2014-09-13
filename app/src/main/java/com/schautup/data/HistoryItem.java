package com.schautup.data;

/**
 * DataSet for a logged history item.
 *
 * @author Xinyue Zhao
 */
public final class HistoryItem {
	/**
	 * The row Id of {@link com.schautup.data.HistoryItem} when the item has been inserted and fetched from DB.
	 */
	private long mId;
	/**
	 * The type of scheduled item.
	 */
	private ScheduleType mType;
	/**
	 * The time that the data was logged.
	 */
	private long mLogTime;


	/**
	 * Extra information.
	 */
	private String mComment;

	/**
	 * Constructor of {@link com.schautup.data.HistoryItem}.
	 *
	 * @param id
	 * 		The row Id  of {@link com.schautup.data.HistoryItem} when the item has been inserted and fetched from DB.
	 * @param type
	 * 		The type of scheduled item.
	 * @param logTime
	 * 		The time that the data was logged.
	 */
	public HistoryItem(long id, ScheduleType type, long logTime) {
		this(type, logTime);
		mId = id;
	}

	/**
	 * Constructor of {@link com.schautup.data.HistoryItem}.
	 *
	 * @param type
	 * 		The type of scheduled item.
	 * @param logTime
	 * 		The time that the data was logged.
	 */
	public HistoryItem(ScheduleType type, long logTime) {
		this(type);
		mLogTime = logTime;
	}

	/**
	 * Constructor of {@link com.schautup.data.HistoryItem}.
	 *
	 * @param type
	 * 		The type of scheduled item.
	 */
	public HistoryItem(ScheduleType type) {
		mType = type;
	}

	/**
	 * Get the type of scheduled item.
	 *
	 * @return {@link com.schautup.data.ScheduleType}.
	 */
	public ScheduleType getType() {
		return mType;
	}

	/**
	 * Get the time that the data was logged.
	 *
	 * @return The timestamps for the logged time.
	 */
	public long getLogTime() {
		return mLogTime;
	}

	/**
	 * Get the id of {@link com.schautup.data.HistoryItem} when the item has been inserted and fetched from DB.
	 *
	 * @return The row Id.
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Get extra information.
	 *
	 * @return Comment.
	 */
	public String getComment() {
		return mComment;
	}

	/**
	 * Set extra information.
	 */
	public void setComment(String _comment) {
		mComment = _comment;
	}
}

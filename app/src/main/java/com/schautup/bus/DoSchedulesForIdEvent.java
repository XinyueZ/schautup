package com.schautup.bus;

/**
 * Event. Do a schedule task with id.
 * <p/>
 * This event is needed by "Neutral" and "Thirsty".
 *
 * @author Xinyue Zhao
 */
public final class DoSchedulesForIdEvent {
	/**
	 * The id of task. It is the Id in DB and also the id in pending-list.
	 */
	private long mId;
	/**
	 * Flag, whether schedule for next timing, for the case that android 4.4 has no exact repeating.
	 * <p/>
	 * For "Neutral" this value should be {@code false}. For "Thirsty" is {@code true}.
	 */
	private boolean mDoNext;

	/**
	 * Constructor of {@link com.schautup.bus.DoSchedulesForIdEvent}.
	 *
	 * @param id
	 * 		The id of task. It is the Id in DB and also the id in pending-list.
	 * @param doNext
	 * 		Flag, whether schedule for next timing, for the case that android 4.4 has no exact repeating. For "Neutral"
	 * 		this value should be {@code false}. For "Thirsty" is {@code true}.
	 */
	public DoSchedulesForIdEvent(long id, boolean doNext) {
		mId = id;
		mDoNext = doNext;
	}

	/**
	 * Get id of task. It is the Id in DB and also the id in pending-list.
	 *
	 * @return The id.
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Flag, whether schedule for next timing, for the case that android 4.4 has no exact repeating.
	 * <p/>
	 * For "Neutral" this value should be {@code false}. For "Thirsty" is {@code true}.
	 *
	 * @return {@code true} when next schedule will be given to {@link android.app.AlarmManager}.
	 */
	public boolean isDoNext() {
		return mDoNext;
	}
}

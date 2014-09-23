package com.schautup.bus;

/**
 * Event. Do a schedule task with id.
 *
 * @author Xinyue Zhao
 */
public final class DoSchedulesForIdEvent {
	/**
	 * The id of task. It is the Id in DB and also the id in pending-list.
	 */
	private long mId;

	/**
	 * Constructor of {@link com.schautup.bus.DoSchedulesForIdEvent}.
	 *
	 * @param id
	 * 		The id of task. It is the Id in DB and also the id in pending-list.
	 */
	public DoSchedulesForIdEvent(long id) {
		mId = id;
	}

	/**
	 * Get id of task. It is the Id in DB and also the id in pending-list.
	 *
	 * @return The id.
	 */
	public long getId() {
		return mId;
	}
}

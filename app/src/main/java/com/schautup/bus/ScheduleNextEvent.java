package com.schautup.bus;

/**
 * To schedule next task after current task is finished.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleNextEvent {
	/**
	 * The id of {@link com.schautup.data.ScheduleItem} in DB, it is also the id of pending of this schedule in the
	 * pending-list in {@link com.schautup.scheduler.Thirsty}({@link com.schautup.scheduler.Thirsty#mScheduledIntents}).
	 */
	private long mId;

	/**
	 * Constructor of {@link com.schautup.bus.ScheduleNextEvent}.
	 *
	 * @param id
	 * 		The id of {@link com.schautup.data.ScheduleItem} in DB, it is also the id of pending of this schedule in the
	 * 		pending-list in {@link com.schautup.scheduler.Thirsty}({@link com.schautup.scheduler.Thirsty#mScheduledIntents}).
	 */
	public ScheduleNextEvent(long id) {
		mId = id;
	}

	/**
	 * Get the id of {@link com.schautup.data.ScheduleItem} in DB, it is also the id of pending of this schedule in the
	 * pending-list in {@link com.schautup.scheduler.Thirsty}({@link com.schautup.scheduler.Thirsty#mScheduledIntents}).
	 *
	 * @return The id.
	 */
	public long getId() {
		return mId;
	}
}

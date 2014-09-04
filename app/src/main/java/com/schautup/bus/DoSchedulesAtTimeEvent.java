package com.schautup.bus;

import org.joda.time.DateTime;

/**
 * Event. Do schedules at a time point.
 *
 * @author Xinyue Zhao
 */
public final class DoSchedulesAtTimeEvent {
	/**
	 * The time to do the schedule.
	 */
	private DateTime mTime;

	/**
	 * Constructor of {@link com.schautup.bus.DoSchedulesAtTimeEvent}.
	 * @param time The time to do the schedule.
	 */
	public DoSchedulesAtTimeEvent(DateTime time) {
		mTime = time;
	}

	/**
	 * Get the time to do the schedule.
	 */
	public DateTime getTime() {
		return mTime;
	}
}

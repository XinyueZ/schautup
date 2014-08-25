package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Set(edit) option of a schedule.
 *
 * @author Xinyue Zhao
 */
public final class SetOptionEvent {
	/**
	 * Data that holds this option.
	 */
	private ScheduleItem mScheduleItem;

	/**
	 * Constructor of {@link SetOptionEvent}.
	 *
	 * @param  scheduleItem
	 * 		The Data that holds this option.
	 */
	public SetOptionEvent(ScheduleItem  scheduleItem) {
		mScheduleItem =  scheduleItem;
	}

	/**
	 * Get  data that holds this option.
	 */
	public ScheduleItem getScheduleItem() {
		return mScheduleItem;
	}
}

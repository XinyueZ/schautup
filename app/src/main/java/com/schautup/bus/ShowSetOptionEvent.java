package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Event, ask to show a dialog that we can set(edit) option of a schedule.
 *
 * @author Xinyue Zhao
 */
public final class ShowSetOptionEvent {
	/**
	 * Data that holds this option.
	 */
	private ScheduleItem mScheduleItem;

	/**
	 * Constructor of {@link ShowSetOptionEvent}.
	 *
	 * @param scheduleItem
	 * 		The Data that holds this option.
	 */
	public ShowSetOptionEvent(ScheduleItem scheduleItem) {
		mScheduleItem = scheduleItem;
	}

	/**
	 * Get  data that holds this option.
	 * <p/>
	 * It might be NULL when user wanna add new data into DB.
	 *
	 * @return A {@link com.schautup.data.ScheduleItem}.
	 */
	public ScheduleItem getScheduleItem() {
		return mScheduleItem;
	}
}

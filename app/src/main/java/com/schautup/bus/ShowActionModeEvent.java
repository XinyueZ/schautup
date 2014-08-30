package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Event when wanna open the Action-mode.
 *
 * @author Xinyue Zhao
 */
public final class ShowActionModeEvent {
	/**
	 * The item that has been selected by action-mode.
	 */
	private ScheduleItem mSelectedItem;

	/**
	 * Constructor of {@link com.schautup.bus.ShowActionModeEvent}.
	 *
	 * @param scheduleItem
	 * 		The item that has been selected by action-mode.
	 */
	public ShowActionModeEvent(ScheduleItem scheduleItem) {
		mSelectedItem = scheduleItem;
	}

	/**
	 * Get the item that has been selected by action-mode.
	 *
	 * @return The item selected.
	 */
	public ScheduleItem getSelectedItem() {
		return mSelectedItem;
	}
}

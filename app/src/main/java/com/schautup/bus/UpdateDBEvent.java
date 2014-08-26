package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Event if user tries to update DB.
 */
public final class UpdateDBEvent {
	/**
	 * Item to update onto DB.
	 */
	private ScheduleItem mItem;

	/**
	 * Constructor of {@link com.schautup.bus.UpdateDBEvent}.
	 *
	 * @param _item
	 * 		{@link com.schautup.data.ScheduleItem} to update onto DB.
	 */
	public UpdateDBEvent(ScheduleItem _item) {
		mItem = _item;
	}

	/**
	 * Get {@link com.schautup.data.ScheduleItem} to update onto DB.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}
}

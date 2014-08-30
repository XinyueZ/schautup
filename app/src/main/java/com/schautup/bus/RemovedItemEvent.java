package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Removed an item on DB.
 * <p/>
 * <b>The event is different from {@link UpdateDBEvent} that it fired after DB Ops has been finished.</b>
 *
 * @author Xinyue Zhao
 */
public final class RemovedItemEvent {
	/**
	 * Item to remove on DB.
	 */
	private ScheduleItem mItem;

	/**
	 * Constructor of {@link com.schautup.bus.RemovedItemEvent}.
	 *
	 * @param item
	 * 		The  item to remove on DB.
	 */
	public RemovedItemEvent(ScheduleItem item) {
		mItem = item;
	}

	/**
	 * Get the item to remove on DB.
	 *
	 * @return The item to remove on DB.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}
}

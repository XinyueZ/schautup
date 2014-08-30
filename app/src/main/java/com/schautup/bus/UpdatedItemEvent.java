package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Added or edited an item on DB.
 * <p/>
 * <b>The event is different from {@link com.schautup.bus.UpdateDBEvent} that it fired after DB Ops has been
 * finished.</b>
 *
 * @author Xinyue Zhao
 */
public final class UpdatedItemEvent {
	/**
	 * Item to update on DB.
	 */
	private ScheduleItem mItem;

	/**
	 * Constructor of {@link com.schautup.bus.UpdatedItemEvent}.
	 *
	 * @param item
	 * 		The  item to update on DB.
	 */
	public UpdatedItemEvent(ScheduleItem item) {
		mItem = item;
	}

	/**
	 * Get the item to update on DB.
	 *
	 * @return The item to update on DB.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}
}

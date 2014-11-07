package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Added or edited an {@link com.schautup.data.ScheduleItem} on DB.
 * <p/>
 * <b>The event is different from {@link com.schautup.bus.UpdateDBEvent} that it fired after DB Ops has been
 * finished.</b>
 *
 * @author Xinyue Zhao
 */
public final class UpdatedItemEvent {
	/**
	 * {@link com.schautup.data.ScheduleItem} to update on DB.
	 */
	private ScheduleItem mItem;

	/**
	 * Constructor of {@link com.schautup.bus.UpdatedItemEvent}.
	 *
	 * @param item
	 * 		The  {@link com.schautup.data.ScheduleItem} to update on DB.
	 */
	public UpdatedItemEvent(ScheduleItem item) {
		mItem = item;
	}

	/**
	 * Get the {@link com.schautup.data.ScheduleItem} to update on DB.
	 *
	 * @return The {@link com.schautup.data.ScheduleItem} to update on DB.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}
}

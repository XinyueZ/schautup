package com.schautup.bus;

import java.util.List;

import com.schautup.data.ScheduleItem;

/**
 * Added or edited a group of {@link com.schautup.data.ScheduleItem} on DB.
 *
 * @author Xinyue Zhao
 */
public final class UpdatedItemGroupEvent {
	/**
	 * {@link com.schautup.data.ScheduleItem}s to update on DB.
	 */
	private List<ScheduleItem> mItems;

	/**
	 * Constructor of {@link com.schautup.bus.UpdatedItemGroupEvent}.
	 *
	 * @param items
	 * 		The  {@link com.schautup.data.ScheduleItem}s to update on DB.
	 */
	public UpdatedItemGroupEvent( List<ScheduleItem> items) {
		mItems = items;
	}

	/**
	 * Get the {@link com.schautup.data.ScheduleItem}s to update on DB.
	 *
	 * @return The {@link com.schautup.data.ScheduleItem}s to update on DB.
	 */
	public  List<ScheduleItem> getItems() {
		return mItems;
	}
}

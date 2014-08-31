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
	 * The count of rows remain in DB after removed item.
	 */
	private int mRowsRemain;

	/**
	 * Constructor of {@link com.schautup.bus.RemovedItemEvent}.
	 *
	 * @param item
	 * 		The  item to remove on DB.
	 * @param rowsRemain
	 * 		The count of rows remain in DB after removed item.
	 */
	public RemovedItemEvent(ScheduleItem item, int rowsRemain) {
		mItem = item;
		mRowsRemain = rowsRemain;
	}

	/**
	 * Get the item to remove on DB.
	 *
	 * @return The item to remove on DB.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}

	/**
	 * Get the count of rows remain in DB after removed item.
	 *
	 * @return count of rows remain in DB.
	 */
	public int getRowsRemain() {
		return mRowsRemain;
	}
}

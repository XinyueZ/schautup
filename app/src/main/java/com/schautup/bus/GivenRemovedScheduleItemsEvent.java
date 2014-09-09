package com.schautup.bus;

import android.support.v4.util.LongSparseArray;

import com.schautup.data.ScheduleItem;

/**
 * Give front the items that have been removed from cache.
 *
 * @author Xinyue Zhao
 */
public final class GivenRemovedScheduleItemsEvent {
	/**
	 * Items that have been removed.
	 */
	private LongSparseArray<ScheduleItem>  mItems;

	/**
	 * Constructor of {@link com.schautup.bus.GivenRemovedScheduleItemsEvent}.
	 * @param items Items that have been removed.
	 */
	public GivenRemovedScheduleItemsEvent(LongSparseArray<ScheduleItem> items) {
		mItems = items;
	}

	/**
	 * Get items that have been removed.
	 * @return Items that have been removed.
	 */
	public LongSparseArray<ScheduleItem> getItems() {
		return mItems;
	}
}

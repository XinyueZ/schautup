package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Event fired when there's a duplicated item before we update(add) one item in DB.
 *
 * @author Xinyue Zhao
 */
public final class FindDuplicatedItemEvent {
	/**
	 * The item that could be duplicated for the pre inserted one.
	 */
	private ScheduleItem mDuplicatedItem;

	/**
	 * Constructor  of {@link FindDuplicatedItemEvent}.
	 *
	 * @param duplicatedItem
	 * 		The item that could be duplicated for the pre inserted one.
	 */
	public FindDuplicatedItemEvent(ScheduleItem duplicatedItem) {
		super();
		mDuplicatedItem = duplicatedItem;
	}

	/**
	 * Get the item that could be duplicated for the pre inserted one.
	 *
	 * @return The item that could be duplicated for the pre inserted one.
	 */
	public ScheduleItem getDuplicatedItem() {
		return mDuplicatedItem;
	}
}

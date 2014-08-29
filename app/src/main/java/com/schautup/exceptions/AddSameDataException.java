package com.schautup.exceptions;

import com.schautup.data.ScheduleItem;

/**
 * Exception fired when same data will be added into DB.
 * <p/>
 * <p/>
 * For example:
 * <p/>
 * {@link com.schautup.data.ScheduleItem} [MUTE, 13, 30] has been inserted.
 * <p/>
 * When user wanna do insert with same data([MUTE, 13, 30]), {@link AddSameDataException} will be caught.
 *
 * @author Xinyue Zhao
 */
public final class AddSameDataException extends Exception {
	/**
	 * The item that could be duplicated for the pre inserted one.
	 */
	private ScheduleItem mDuplicatedItem;

	/**
	 * Constructor  of {@link AddSameDataException}.
	 *
	 * @param _duplicatedItem
	 * 		The item that could be duplicated for the pre inserted one.
	 */
	public AddSameDataException(ScheduleItem _duplicatedItem) {
		super();
		mDuplicatedItem = _duplicatedItem;
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

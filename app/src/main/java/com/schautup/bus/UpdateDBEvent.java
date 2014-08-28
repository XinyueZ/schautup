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
	 * {@code true} if we do update, {@code false} if it is a new data to insert.
	 */
	private boolean mEditMode = false;

	/**
	 * Constructor of {@link com.schautup.bus.UpdateDBEvent}.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to update onto DB.
	 * @param editMode
	 * 		{@code true} if we do update, {@code false} if it is a new data to insert.
	 */
	public UpdateDBEvent(ScheduleItem item, boolean editMode) {
		mItem = item;
		mEditMode = editMode;
	}

	/**
	 * Get {@link com.schautup.data.ScheduleItem} to update onto DB.
	 *
	 * @return {@link com.schautup.data.ScheduleItem}.
	 */
	public ScheduleItem getItem() {
		return mItem;
	}

	/**
	 * Update or insert new data.
	 *
	 * @return {@code true} if we do update, {@code false} if it is a new data to insert.
	 */
	public boolean isEditMode() {
		return mEditMode;
	}
}

package com.schautup.bus;

import com.schautup.data.ScheduleItem;

/**
 * Event fired user tries to add / edit item on DB.
 * <p/>
 * <b>The event is different from {@link com.schautup.bus.UpdatedItemEvent} that it's fired when DB Ops.
 *
 * @author Xinyue
 */
public final class UpdateDBEvent {
	/**
	 * Item to update on DB.
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
	 * 		{@link com.schautup.data.ScheduleItem} to update on DB.
	 * @param editMode
	 * 		{@code true} if we do update, {@code false} if it is a new data to insert.
	 */
	public UpdateDBEvent(ScheduleItem item, boolean editMode) {
		mItem = item;
		mEditMode = editMode;
	}

	/**
	 * Get {@link com.schautup.data.ScheduleItem} to update on DB.
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

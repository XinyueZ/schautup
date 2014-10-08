package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Event when a label which is {@link com.schautup.data.Filter#isLabel()}{@code =true} added / edit on {@link com.schautup.db.DB}.
 *
 * @author Xinyue Zhao
 */
public final class UpdateLabelEvent {
	/**
	 * A   {@link com.schautup.data.Filter} to add or edit.
	 */
	private Filter mFilter;
	/**
	 * {@code true} for edit, {@code false} for add new.
	 */
	private boolean mIsEdit;

	/**
	 * Constructor of {@link com.schautup.bus.UpdateLabelEvent}
	 *
	 * @param filter
	 * 		A   {@link com.schautup.data.Filter} to add or edit.
	 * 	@param isEdit  {@code true} for edit, {@code false} for add new.
	 */
	public UpdateLabelEvent(Filter filter, boolean isEdit) {
		mFilter = filter;
		mIsEdit = isEdit;
	}

	/**
	 * Get {@link com.schautup.data.Filter} what has been updated(add new or edited).
	 *
	 * @return A new {@link com.schautup.data.Filter}.
	 */
	public Filter getLabel() {
		return mFilter;
	}

	/**
	 * Is add new or edit.
	 * @return {@code true} for edit, {@code false} for add new.
	 */
	public boolean isEdit() {
		return mIsEdit;
	}
}

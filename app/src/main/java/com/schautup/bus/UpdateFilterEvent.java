package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Event when a filter's added / edit on {@link com.schautup.db.DB}.
 *
 * @author Xinyue Zhao
 */
public final class UpdateFilterEvent {
	/**
	 * A   {@link com.schautup.data.Filter} to add or edit.
	 */
	private Filter mFilter;
	/**
	 * {@code true} for edit, {@code false} for add new.
	 */
	private boolean mIsEdit;
	/**
	 * Works  <b>only</b> when  {@link #mIsEdit} {@code = false}.
	 * <p/>
	 * {@code true} when do not need update operation on DB, {@code false} when need. It is important when a "label" was
	 * switched to "filter" and UI should update.
	 */
	private boolean mIgnoreCheckingDB;

	/**
	 * Constructor of {@link UpdateFilterEvent}
	 *
	 * @param filter
	 * 		A   {@link com.schautup.data.Filter} to add or edit.
	 * @param isEdit
	 * 		{@code true} for edit, {@code false} for add new.
	 * @param ignoreCheckingDB
	 * 		Works   only when  {@link #mIsEdit} {@code = false}.
	 * 		<p/>
	 * 		{@code true}, if do not need update operation on DB, {@code false} if need. It is important when a "label"
	 * 		was switched to "filter" and UI should update.
	 */
	public UpdateFilterEvent(Filter filter, boolean isEdit, boolean ignoreCheckingDB) {
		mFilter = filter;
		mIsEdit = isEdit;
		mIgnoreCheckingDB = ignoreCheckingDB;
	}

	/**
	 * Get {@link com.schautup.data.Filter} what has been updated(add new or edited).
	 *
	 * @return A new {@link com.schautup.data.Filter}.
	 */
	public Filter getFilter() {
		return mFilter;
	}

	/**
	 * Is add new or edit.
	 *
	 * @return {@code true} for edit, {@code false} for add new.
	 */
	public boolean isEdit() {
		return mIsEdit;
	}

	/**
	 * To know whether update on DB is needed or not.
	 *
	 * Works  only when  {@link #mIsEdit} {@code = false}.
	 *
	 * @return  {@code true} when do not need update operation on DB, {@code false} when need. It is important when a "label" was
	 * switched to "filter" and UI should update.
	 */
	public boolean isIgnoreCheckingDB() {
		return mIgnoreCheckingDB;
	}
}

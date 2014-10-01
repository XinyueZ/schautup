package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Event when a filter's added into {@link com.schautup.db.DB}.
 *
 * @author Xinyue Zhao
 */
public final class AddedFilterEvent {
	/**
	 * A new {@link com.schautup.data.Filter}
	 */
	private Filter mNewFilter;

	/**
	 * Constructor of {@link com.schautup.bus.AddedFilterEvent}
	 *
	 * @param newFilter
	 * 		A new {@link com.schautup.data.Filter}.
	 */
	public AddedFilterEvent(Filter newFilter) {
		mNewFilter = newFilter;
	}

	/**
	 * Get {@link com.schautup.data.Filter} what has been added.
	 *
	 * @return A new {@link com.schautup.data.Filter}.
	 */
	public Filter getNewFilter() {
		return mNewFilter;
	}
}

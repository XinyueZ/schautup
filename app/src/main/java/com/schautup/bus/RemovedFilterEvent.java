package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Event when a filter is removed from {@link com.schautup.db.DB}.
 *
 * @author Xinyue Zhao
 */
public final class RemovedFilterEvent {
	/**
	 * An removed old {@link com.schautup.data.Filter}
	 */
	private Filter mNewFilter;

	/**
	 * Constructor of {@link com.schautup.bus.RemovedFilterEvent}
	 *
	 * @param newFilter
	 * 		An removed old  {@link com.schautup.data.Filter}.
	 */
	public RemovedFilterEvent(Filter newFilter) {
		mNewFilter = newFilter;
	}

	/**
	 * Get {@link com.schautup.data.Filter} what has been removed from {@link com.schautup.db.DB}.
	 *
	 * @return An removed old {@link com.schautup.data.Filter}.
	 */
	public Filter getNewFilter() {
		return mNewFilter;
	}
}

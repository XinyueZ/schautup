package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Filter what user sees.
 */
public final class FilterEvent {
	/**
	 * {@link com.schautup.data.Filter} selected.
	 */
	private Filter mFilter;

	/**
	 * Constructor of {@link com.schautup.bus.FilterEvent}.
	 * @param filter {@link com.schautup.data.Filter} selected.
	 */
	public FilterEvent(Filter filter) {
		mFilter = filter;
	}
	/**
	 * {@link com.schautup.data.Filter} selected.
	 */
	public Filter getFilter() {
		return mFilter;
	}
}

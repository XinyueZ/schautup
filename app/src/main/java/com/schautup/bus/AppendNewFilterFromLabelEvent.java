package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * To append a new {@link com.schautup.data.Filter} which was a label.
 */
public final class AppendNewFilterFromLabelEvent {
	/**
	 * New item to append.
	 */
	private Filter mFilter;

	/**
	 * Constructor of {@link com.schautup.bus.AppendNewFilterFromLabelEvent}.
	 * @param filter New item to append.
	 */
	public AppendNewFilterFromLabelEvent(Filter filter) {
		mFilter = filter;
	}

	/**
	 * The item that will be appended.
	 * @return {@link com.schautup.data.Filter} to append.
	 */
	public Filter getFilter() {
		return mFilter;
	}
}

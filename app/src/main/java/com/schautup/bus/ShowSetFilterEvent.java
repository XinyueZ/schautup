package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Edit a {@link com.schautup.data.Filter}.
 *
 * @author Xinyue Zhao
 */
public final class ShowSetFilterEvent {
	private Filter mFilter;

	public ShowSetFilterEvent(Filter _filter) {
		mFilter = _filter;
	}

	public Filter getFilter() {
		return mFilter;
	}
}

package com.schautup.bus;

import com.schautup.data.Filter;

/**
 * Edit a {@link com.schautup.data.Filter} whose {@link com.schautup.data.Filter#isLabel()}{@code =true}.
 *
 * @author Xinyue Zhao
 */
public final class ShowSetLabelEvent {
	private Filter mFilter;

	public ShowSetLabelEvent(Filter _filter) {
		mFilter = _filter;
	}

	public Filter getLabel() {
		return mFilter;
	}
}

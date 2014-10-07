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
	 * {@code true} if user selects a {@link com.schautup.data.Filter} from {@link android.support.v4.widget.DrawerLayout}.
	 * <p/>
	 * {@code false} if user selects a {@link com.schautup.data.Filter} on {@link android.widget.Spinner} on {@link
	 * android.support.v7.app.ActionBar}.
	 */
	private boolean mFromDrawer;

	/**
	 * Constructor of {@link com.schautup.bus.FilterEvent}.
	 *
	 * @param filter
	 * 		{@link com.schautup.data.Filter} selected.
	 * @param fromDrawer
	 * 		{@code true} if user selects a {@link com.schautup.data.Filter} from {@link
	 * 		android.support.v4.widget.DrawerLayout}.
	 * 		<p/>
	 * 		{@code false} if user selects a {@link com.schautup.data.Filter} on {@link android.widget.Spinner} on {@link
	 * 		android.support.v7.app.ActionBar}.
	 */
	public FilterEvent(Filter filter, boolean fromDrawer) {
		mFilter = filter;
		mFromDrawer = fromDrawer;
	}

	/**
	 * {@link com.schautup.data.Filter} selected.
	 */
	public Filter getFilter() {
		return mFilter;
	}

	/**
	 * Detect whether {@link com.schautup.data.Filter} is from {@link android.support.v4.widget.DrawerLayout} or not.
	 *
	 * @return {@code true} if user selects a {@link com.schautup.data.Filter} from {@link
	 * android.support.v4.widget.DrawerLayout}.
	 * <p/>
	 * {@code false} if user selects a {@link com.schautup.data.Filter} on {@link android.widget.Spinner} on {@link
	 * android.support.v7.app.ActionBar}.
	 */
	public boolean isFromDrawer() {
		return mFromDrawer;
	}
}

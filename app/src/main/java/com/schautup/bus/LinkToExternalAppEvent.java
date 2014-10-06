package com.schautup.bus;

import com.schautup.data.AppListItem;

/**
 * A call to an external app, open or direct to store when not be installed before.
 *
 * @author Xinyue Zhao
 */
public final class LinkToExternalAppEvent {
	/**
	 * The data-set represent an external app.
	 */
	private AppListItem mAppListItem;

	/**
	 * Constructor of {@link LinkToExternalAppEvent}
	 *
	 * @param appListItem
	 * 		The data-set represent an external app.
	 */
	public LinkToExternalAppEvent(AppListItem appListItem) {
		mAppListItem = appListItem;
	}

	/**
	 * Get the data-set represent an external app.
	 */
	public AppListItem getAppListItem() {
		return mAppListItem;
	}
}

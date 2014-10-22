package com.schautup.bus;

import android.content.pm.ResolveInfo;

/**
 * Event to show UI that shows all installed applications.
 *
 * @author Xinyue Zhao
 */
public final class ShowInstalledApplicationsListEvent {
	/**
	 * An installed application.
	 */
	private ResolveInfo mResolveInfo;

	/**
	 * Constructor of {@link com.schautup.bus.SelectedInstalledApplicationEvent}.
	 * @param resolveInfo  An installed application.
	 */
	public ShowInstalledApplicationsListEvent(ResolveInfo resolveInfo) {
		mResolveInfo = resolveInfo;

	}

	/**
	 * Get an installed application.
	 * @return An installed application.
	 */
	public ResolveInfo getResolveInfo() {
		return mResolveInfo;
	}
}

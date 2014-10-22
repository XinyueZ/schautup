package com.schautup.bus;

import android.content.pm.ResolveInfo;

/**
 * Event when user selected or pre-selected an installed application.
 *
 * @author Xinyue Zhao
 */
public final class SelectedInstalledApplicationEvent {
	/**
	 * An installed application.
	 */
	private ResolveInfo mResolveInfo;

	/**
	 * Constructor of {@link com.schautup.bus.SelectedInstalledApplicationEvent}.
	 * @param resolveInfo  An installed application.
	 */
	public SelectedInstalledApplicationEvent(ResolveInfo resolveInfo) {
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

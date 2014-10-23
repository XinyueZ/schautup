package com.schautup.bus;

import java.util.List;

/**
 * Event when the scheduled tasks that start applications should be removed, because the applications to start would be uninstalled.
 *
 * @author Xinyue Zhao
 */
public final class RemovedScheduledStartAppsEvent {
	/**
	 * The scheduled ids.
	 */
	private List<String> mIds;

	/**
	 * The constructor of {@link com.schautup.bus.RemovedScheduledStartAppsEvent}.
	 * @param ids The ids of scheduled items.
	 */
	public RemovedScheduledStartAppsEvent(List<String> ids) {
		mIds = ids;
	}

	/**
	 * Get the ids of scheduled items.
	 * @return The ids of scheduled items.
	 */
	public List<String> getIds() {
		return mIds;
	}
}

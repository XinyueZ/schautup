package com.schautup.bus;

import com.schautup.data.HistoryItem;

/**
 * Event fired after a history item has been added to DB.
 */
public final class AddedHistoryEvent {
	/**
	 * Added item.
	 */
	private HistoryItem mHistoryItem;

	/**
	 * Constructor of {@link com.schautup.bus.AddedHistoryEvent}.
	 * @param historyItem  Added item.
	 */
	public AddedHistoryEvent(HistoryItem historyItem) {
		mHistoryItem = historyItem;
	}

	/**
	 * Get added item.
	 * @return Added item.
	 */
	public HistoryItem getHistoryItem() {
		return mHistoryItem;
	}
}

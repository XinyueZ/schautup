package com.schautup.bus;

/**
 * Event when wanna open the Action-mode.
 *
 * @author Xinyue Zhao
 */
public final class ShowActionModeEvent {
	/**
	 * The item that has been selected by action-mode.
	 */
	private Object mSelectedItem;

	/**
	 * Constructor of {@link com.schautup.bus.ShowActionModeEvent}.
	 *
	 * @param item
	 * 		The item that has been selected by action-mode.
	 */
	public ShowActionModeEvent(Object item) {
		mSelectedItem = item;
	}

	/**
	 * Get the item that has been selected by action-mode.
	 *
	 * @return The item selected.
	 */
	public Object getSelectedItem() {
		return mSelectedItem;
	}
}

package com.schautup.bus;

/**
 * Event fired when the sticky must be shown.
 *
 * @author Xinyue Zhao
 */
public final class ShowStickyEvent {
	/**
	 * The message to be shown on the sticky.
	 */
	private String mMessage;

	/**
	 * Constructor of {@link com.schautup.bus.ShowActionBarEvent}
	 *
	 * @param s
	 * 		The message to be shown.
	 */
	public ShowStickyEvent(String s ) {
		mMessage = s;
	}

	/**
	 * Get the message to be shown on the sticky.
	 *
	 * @return The message to be shown.
	 */
	public String getMessage() {
		return mMessage;
	}
}

package com.schautup.bus;

import android.support.annotation.ColorRes;

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
	 * {@link android.support.annotation.ColorRes}, the color for the sticky.
	 */
	private int mColorRes;

	/**
	 * Constructor of {@link com.schautup.bus.ShowActionBarEvent}
	 *
	 * @param s
	 * 		The message to be shown.
	 * @param colorRes
	 * 		{@link android.support.annotation.ColorRes}, the color for the sticky.
	 */
	public ShowStickyEvent(String s, @ColorRes int colorRes) {
		mMessage = s;
		mColorRes = colorRes;
	}

	/**
	 * Get the message to be shown on the sticky.
	 *
	 * @return The message to be shown.
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * Get {@link android.support.annotation.ColorRes}, the color for the sticky.
	 *
	 * @return {@link android.support.annotation.ColorRes}, the color for the sticky.
	 */
	public int getColor() {
		return mColorRes;
	}
}

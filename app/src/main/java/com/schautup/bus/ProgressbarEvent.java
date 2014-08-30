package com.schautup.bus;

/**
 * Event for progress indicator.
 *
 * @author Xinyue
 */
public final class ProgressbarEvent {
	/**
	 * {@code true} if show indicator.
	 */
	private boolean mShow;

	/**
	 * Constructor of {@link com.schautup.bus.ProgressbarEvent}.
	 *
	 * @param show
	 * 		{@code true} if show indicator.
	 */
	public ProgressbarEvent(boolean show) {
		mShow = show;
	}

	/**
	 * Show or not show indicator.
	 *
	 * @return {@code true} if show indicator.
	 */
	public boolean isShow() {
		return mShow;
	}
}

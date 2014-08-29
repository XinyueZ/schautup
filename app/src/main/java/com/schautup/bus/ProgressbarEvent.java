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
	 * @param _show
	 * 		{@code true} if show indicator.
	 */
	public ProgressbarEvent(boolean _show) {
		mShow = _show;
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

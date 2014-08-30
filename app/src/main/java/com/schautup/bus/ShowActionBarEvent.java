package com.schautup.bus;

/**
 * Event, whether to show or hidden {@link android.support.v7.app.ActionBar}.
 *
 * @author Xinyue Zhao
 */
public final class ShowActionBarEvent {
	/**
	 * {@code true} if show the  {@link android.support.v7.app.ActionBar}.
	 */
	private boolean mShow;

	/**
	 * Constructor of {@link ShowActionBarEvent}.
	 *
	 * @param show
	 * 		{@code true} if show the  {@link android.support.v7.app.ActionBar}.
	 */
	public ShowActionBarEvent(boolean show) {
		mShow = show;
	}

	/**
	 * Show or hidden the {@link android.support.v7.app.ActionBar}.
	 *
	 * @return {@code true} if show the  {@link android.support.v7.app.ActionBar}.
	 */
	public boolean isShow() {
		return mShow;
	}
}

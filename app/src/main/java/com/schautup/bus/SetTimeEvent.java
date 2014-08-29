package com.schautup.bus;

/**
 * Event after time has been selected.
 *
 * @author Xinyue
 */
public final class SetTimeEvent {
	/**
	 * Selected hour.
	 */
	private int mHour;
	/**
	 * Selected minute.
	 */
	private int mMinute;

	/**
	 * Constructor of {@link com.schautup.bus.SetTimeEvent}.
	 *
	 * @param hour
	 * 		Selected hour.
	 * @param minute
	 * 		Selected minute.
	 */
	public SetTimeEvent(int hour, int minute) {
		mHour = hour;
		mMinute = minute;
	}

	/**
	 * Get selected hour.
	 *
	 * @return The selected hour.
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * Get selected minute.
	 *
	 * @return The selected minute.
	 */
	public int getMinute() {
		return mMinute;
	}
}

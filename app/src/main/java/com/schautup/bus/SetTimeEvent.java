package com.schautup.bus;

/**
 * Event after time has been selected.
 */
public final class SetTimeEvent {
	/**
	 * Selected hour.
	 */
	private String mHour;
	/**
	 * Selected minute.
	 */
	private String mMinute;

	/**
	 * Constructor of {@link com.schautup.bus.SetTimeEvent}.
	 *
	 * @param hour
	 * 		{@link java.lang.String} Selected hour.
	 * @param minute
	 * 		{@link java.lang.String} Selected minute.
	 */
	public SetTimeEvent(String hour, String minute) {
		mHour = hour;
		mMinute = minute;
	}

	/**
	 * Get selected hour.
	 *
	 * @return {@link java.lang.String} The selected hour.
	 */
	public String getHour() {
		return mHour;
	}

	/**
	 * Get selected minute.
	 *
	 * @return {@link java.lang.String} The selected minute.
	 */
	public String getMinute() {
		return mMinute;
	}
}

package com.schautup.bus;

/**
 * Event to open {@link com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog}.
 *
 * @author Xinyue
 */
public final class OpenTimePickerEvent {
	/**
	 * The start hour for the picker.
	 */
	private int mHour;
	/**
	 * The start minute for the picker.
	 */
	private int mMinute;

	/**
	 * The constructor of {@link OpenTimePickerEvent}.
	 *
	 * @param hour
	 * 		The start hour for the picker.
	 * @param minute
	 * 		The start minute for the picker.
	 */
	public OpenTimePickerEvent(int hour, int minute) {
		mHour = hour;
		mMinute = minute;
	}

	/**
	 * Get the start hour for the picker.
	 *
	 * @return The start hour for the picker.
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * Get the start minute for the picker.
	 *
	 * @return The start minute for the picker.
	 */
	public int getMinute() {
		return mMinute;
	}
}

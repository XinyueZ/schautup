package com.schautup.bus;

/**
 * Event to open a picker for setting recurrence on a schedule item.
 *
 * @author Xinyue Zhao
 */
public final class OpenRecurrencePickerEvent {
	/**
	 * Default value when {@link #mRule} is NULL.
	 */
	private static final String DEFAULT = "FREQ=WEEKLY;WKST=SU";
	/**
	 * Pre-defined rule for the {@link com.schautup.fragments.MyRecurrencePickerDialog}.
	 */
	private String mRule;

	/**
	 * Constructor of {@link com.schautup.bus.OpenRecurrencePickerEvent}.
	 * @param rule
	 */
	public OpenRecurrencePickerEvent(String rule) {
		mRule = rule;
	}

	/**
	 * Get pre-defined rule for the {@link com.schautup.fragments.MyRecurrencePickerDialog}.
	 * @return A default value will be return when the rule was not defined by calling {@link #OpenRecurrencePickerEvent(String)}.
	 */
	public String getRule() {
		return mRule == null ? DEFAULT : mRule;
	}
}

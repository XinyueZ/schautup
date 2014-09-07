package com.schautup.bus;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;

/**
 * Event after user set {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
 *
 * @author Xinyue Zhao
 */
public final class SetRecurrenceEvent {
	/**
	 * The {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 */
	private EventRecurrence mEventRecurrence;

	/**
	 * Constructor of {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 *
	 * @param eventRecurrence
	 * 		{@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 */
	public SetRecurrenceEvent(EventRecurrence eventRecurrence) {
		mEventRecurrence = eventRecurrence;
	}

	/**
	 * Get {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 *
	 * @return {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}.
	 */
	public EventRecurrence getEventRecurrence() {
		return mEventRecurrence;
	}
}

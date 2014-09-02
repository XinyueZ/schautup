package com.schautup.manager;

import android.content.Context;

/**
 * The center class that controls all schedules with time.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleManager {
	/**
	 * {@link android.content.Context}.
	 */
	private Context mContext;
	/**
	 * Singleton instance.
	 */
	private static ScheduleManager sInstance;

	/**
	 * Get singleton instance.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @return The single {@link ScheduleManager}.
	 */
	public static ScheduleManager getInstance(Context cxt) {
		if (sInstance == null) {
			sInstance = new ScheduleManager(cxt);
		}
		return sInstance;
	}

	/**
	 * Constructor of {@link ScheduleManager}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	private ScheduleManager(Context cxt) {
		mContext = cxt;
	}

	/**
	 * Start alarm service.
	 *
	 */
	public void startAllSchedules() {


	}
}

package com.schautup.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;

/**
 * An alternative to schedule tasks by using {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public final class Neutral extends Thirsty {
	@Override
	protected Class<?> doGetReceiver() {
		return NeutralReceiver.class;
	}

	@Override
	protected void doSetAlarmManager(AlarmManager mgr, long timeToAlarm, PendingIntent pendingIntent) {
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm,
													AlarmManager.INTERVAL_DAY, pendingIntent);
	}
}

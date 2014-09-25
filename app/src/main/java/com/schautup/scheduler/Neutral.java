package com.schautup.scheduler;

import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

/**
 * An alternative to schedule tasks by using {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public final class Neutral extends Thirsty {

	@Override
	protected PendingIntent doCreateAlarmPending(AlarmManager mgr, long timeToAlarm, Intent intent) {
		intent.putExtra(EXTRAS_DO_NEXT, false);
		//http://stackoverflow.com/questions/4700058/android-repeating-alarm-not-working
		PendingIntent pi = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		//http://stackoverflow.com/questions/16308783/timeunit-seconds-tomillis
		//http://stackoverflow.com/questions/6980376/convert-from-days-to-milliseconds
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm, TimeUnit.DAYS.toMillis(1), pi);
		return pi;
	}
}
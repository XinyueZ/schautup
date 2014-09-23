package com.schautup.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.schautup.bus.DoSchedulesForIdEvent;
import com.schautup.bus.ScheduleNextEvent;

import de.greenrobot.event.EventBus;

/**
 * A {@link android.content.BroadcastReceiver} for the {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public final class ThirstyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context cxt, Intent intent) {
		long taskId  =intent.getLongExtra(Thirsty.EXTRAS_ITEM_ID, -1);//Id in DB and also the id in pending-list.
		EventBus.getDefault().post(new DoSchedulesForIdEvent(taskId));
		EventBus.getDefault().post(new ScheduleNextEvent(taskId));
	}
}

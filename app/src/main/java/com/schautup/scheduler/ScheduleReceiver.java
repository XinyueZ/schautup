package com.schautup.scheduler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * A {@link android.content.BroadcastReceiver} for the {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context cxt, Intent intent) {
		long taskId = intent.getLongExtra(Thirsty.EXTRAS_ITEM_ID, -1);
		if(taskId > 0 && Thirsty.remove(cxt, taskId)) {
			// The pending has been consumed and removed, but the task will be done.
			Intent service = new Intent(cxt, ScheduleReceiverHelperService.class);
			service.putExtra(Thirsty.EXTRAS_ITEM_ID, taskId);
			service.putExtra(Thirsty.EXTRAS_DO_NEXT, intent.getBooleanExtra(Thirsty.EXTRAS_DO_NEXT, false));
			startWakefulService(cxt, service);
		}
	}
}

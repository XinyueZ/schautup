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
		// This is the Intent to deliver to our service.
		Intent service = new Intent(cxt, ScheduleReceiverHelperService.class);
		service.putExtra(Thirsty.EXTRAS_ITEM_ID, intent.getLongExtra(Thirsty.EXTRAS_ITEM_ID, -1));
		service.putExtra(Thirsty.EXTRAS_DO_NEXT, intent.getBooleanExtra(Thirsty.EXTRAS_DO_NEXT, false));
		startWakefulService(cxt, service);
	}
}

package com.schautup.scheduler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.chopping.application.LL;
import com.schautup.App;

/**
 * A {@link android.content.BroadcastReceiver} for the {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public final class AlarmReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context cxt, Intent intent) {
		long taskId = intent.getLongExtra(App.EXTRAS_ITEM_ID, -1);
		if(taskId > 0 && App.getInstance().remove(  taskId)) {
			// The pending has been consumed and removed, but the task will be done.
			boolean doNext = intent.getBooleanExtra(App.EXTRAS_DO_NEXT, false);
			LL.d("Post event to do schedule, item id: " + taskId);
			App.getInstance().doSchedules( taskId, doNext);
		}
	}
}

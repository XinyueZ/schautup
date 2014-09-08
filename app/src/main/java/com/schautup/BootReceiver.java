package com.schautup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.schautup.scheduler.ScheduleManager;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;

/**
 * Handling device boot by {@link android.content.BroadcastReceiver}.
 *
 * @author Xinyue Zhao
 */
public final class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Application cxt = (Application) context.getApplicationContext();
			Prefs prefs = Prefs.getInstance(cxt);
			if (prefs.isEULAOnceConfirmed()) {
				cxt.startService(new Intent(cxt, ScheduleManager.class));
			}
			Utils.showShortToast(context, R.string.msg_boot_start);
		}
	}
}


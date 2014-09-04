package com.schautup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.schautup.scheduler.Foreground;
import com.schautup.scheduler.Hungry;
import com.schautup.utils.Prefs;

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
			//TODO start all schedules.
			//Currently, we start "Hungry" mode as mode for development.
			//See App as well.
			if(!prefs.isPause()) {
				cxt.startService(new Intent(cxt, Hungry.class));
			}
			if (prefs.isEULAOnceConfirmed()) {
				cxt.startService(new Intent(cxt, Foreground.class));
			}
		}
	}
}


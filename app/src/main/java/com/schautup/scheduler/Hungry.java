package com.schautup.scheduler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.schautup.utils.ParallelTask;

import org.joda.time.DateTime;

/**
 * A {@link android.app.Service} that controls a {@link android.content.BroadcastReceiver} to handle OS's time-tick (pro
 * minute).
 *
 * @author Xinyue Zhao
 */
public final class Hungry extends Service {
	/**
	 * We wanna every-minute-event, this is the {@link android.content.IntentFilter} for every minute from system.
	 */
	private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
	/**
	 * We wanna event to handle for every minute, this is the {@link android.content.BroadcastReceiver} for every minute
	 * from system.
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context cxt, Intent intent) {
			ScheduleManager scheduleManager = new ScheduleManager(cxt);
			new ParallelTask<ScheduleManager, Void, Void>(false) {
				@Override
				protected Void doInBackground(ScheduleManager... params) {
					ScheduleManager sm = params[0];
					sm.workAt(DateTime.now());
					return null;
				}
			}.executeParallel(scheduleManager);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SchautUp", "Start Hungry.");
		registerReceiver(mReceiver, mIntentFilter);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		Log.d("SchautUp", "Stop Hungry.");
	}

	/**
	 * Constructor of {@link com.schautup.scheduler.Hungry}.
	 * <p/>
	 * No usage.
	 */
	public Hungry() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}

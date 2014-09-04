package com.schautup.scheduler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.schautup.bus.DoSchedulesAtTimeEvent;
import com.schautup.utils.LL;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

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
			EventBus.getDefault().post(new DoSchedulesAtTimeEvent(DateTime.now()));
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		LL.i( "Hungry#Create.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LL.i("Hungry#StartCommand.");
		registerReceiver(mReceiver, mIntentFilter);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		LL.i( "Hungry#Destroy.");
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

package com.schautup.scheduler;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.util.LongSparseArray;

import com.chopping.application.LL;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.ScheduleNextEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * An alternative to schedule tasks by using {@link android.app.AlarmManager}.
 *
 * @author Xinyue Zhao
 */
public class Thirsty extends Service {
	/**
	 * Current time zone.
	 */
	private static final String TIMEZONE = "GMT+2";
	/**
	 * Extras store the item id (also the id of pending) when event will be fired by {@link android.app.AlarmManager}.
	 */
	static final String EXTRAS_ITEM_ID = "com.schautup.scheduler.Thirsty.ItemId";

	/**
	 * Extras boolean to indicate whether the {@link com.schautup.bus.ScheduleNextEvent} should be called no not.
	 */
	static final String EXTRAS_DO_NEXT = "com.schautup.scheduler.Thirsty.do.next";
	/**
	 * Cache of all intents that are under way to do the tasks scheduled by {@link android.app.AlarmManager}.
	 */
	private static LongSparseArray<PendingIntent> sScheduledIntents = new LongSparseArray<PendingIntent>();

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.ScheduleNextEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ScheduleNextEvent}.
	 */
	public void onEvent(ScheduleNextEvent e) {
		update(e.getId());
	}


	/**
	 * Handler for {@link com.schautup.bus.GivenRemovedScheduleItemsEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.GivenRemovedScheduleItemsEvent}.
	 */
	public void onEvent(GivenRemovedScheduleItemsEvent e) {
		LongSparseArray<ScheduleItem> items = e.getItems();
		if (items != null) {
			long id;
			for (int i = 0; i < items.size(); i++) {
				id = items.keyAt(i);
				remove(this, id);
			}
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.UpdatedItemEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.UpdatedItemEvent}.
	 */
	public void onEvent(UpdatedItemEvent e) {
		ScheduleItem item = e.getItem();
		if (sScheduledIntents.get(item.getId()) != null) {//Removed the old one after update.
			remove(this, item.getId());
		}
		add(item);
	}

	//------------------------------------------------
	@Override
	public void onCreate() {
		EventBus.getDefault().register(this);
		super.onCreate();
		new ParallelTask<Void, Void, List<ScheduleItem>>(false) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				return Utils.getAllSchedules(getApplication());
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				super.onPostExecute(items);
				addAll(items);
			}
		}.executeParallel();
		LL.i(getClass().getSimpleName() + "#Create.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LL.i(getClass().getSimpleName() + "#StartCommand.");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		removeAll();
		super.onDestroy();
		LL.i(getClass().getSimpleName() + "#Destroy.");
	}


	/**
	 * Constructor of {@link com.schautup.scheduler.Thirsty}.
	 * <p/>
	 * No usage.
	 */
	public Thirsty() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/**
	 * Added a {@link java.util.List} of {@link com.schautup.data.ScheduleItem} to the pending list of {@link
	 * android.app.AlarmManager}.
	 *
	 * @param items
	 * 		{@link java.util.List} of {@link com.schautup.data.ScheduleItem}.
	 */
	private void addAll(List<ScheduleItem> items) {
		for (ScheduleItem item : items) {
			add(item);
		}
	}

	/**
	 * Added a {@link com.schautup.data.ScheduleItem} to the pending list of {@link android.app.AlarmManager}.
	 *
	 * @param item
	 * 		An item of schedule.
	 */
	private void add(ScheduleItem item) {
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// Time since boot(as the time we see the view).
		long firstTime = SystemClock.elapsedRealtime();
		// Current time point.
		long currentTime = System.currentTimeMillis();
		// The time we wanna get alarm.
		int hour = item.getHour();
		int minute = item.getMinute();
		Calendar calendar = Calendar.getInstance();
		// Important! Otherwise there's deviation.
		calendar.setTimeInMillis(System.currentTimeMillis());
		//		calendar.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long setTime = calendar.getTimeInMillis();
		if (currentTime > setTime) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			setTime = calendar.getTimeInMillis();
		}
		// Do alarm at the time point we wanna.
		// A difference between setTime and currentTime plus total time since boot equal to the time
		// point we wanna.
		long timeToAlarm = firstTime + (setTime - currentTime);
		Intent intent = new Intent(this, ScheduleReceiver.class);
		intent.putExtra(EXTRAS_ITEM_ID, item.getId());
		PendingIntent pendingIntent = doCreateAlarmPending(mgr, timeToAlarm, intent);
		sScheduledIntents.put(item.getId(), pendingIntent);
	}

	/**
	 * Removed all scheduled tasks and cancel them.
	 */
	private void removeAll() {
		long id;
		for (int i = 0; i < sScheduledIntents.size(); i++) {
			id = sScheduledIntents.keyAt(i);
			remove(this, id);
		}
	}

	/**
	 * To remove a task from pending of {@link android.app.AlarmManager}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param id
	 * 		The id pending in the list of pending.
	 *
	 * @return {@code true} if find the pending with {@code id} and has been removed. {@code false} if the pending with
	 * {@code id} can't be found.
	 */
	public synchronized  static boolean remove(Context cxt, long id) {
		AlarmManager mgr = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = sScheduledIntents.get(id);
		if (pi != null) {
			sScheduledIntents.remove(id);
			mgr.cancel(pi);
			return true;
		}
		return false;
	}

	/**
	 * To update a new pending when a pending was finished, that means the next round of schedule will be added and
	 * started.
	 *
	 * @param id
	 * 		The id pending in the list of pending.
	 */
	private void update(final long id) {
		//I think it should do a select from DB, because we can't sure
		//that whether the schedule should be continued or not.

		new ParallelTask<Void, Void, List<ScheduleItem>>(false) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				return DB.getInstance(getApplication()).getSchedules(id);
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				super.onPostExecute(items);
				ScheduleItem item = items.get(0);
				//Plane next schedule
				if (item != null) {
					// Removed old pending what has been finished and re-add.
					// It should have been removed when the helper-service work off the schedule.
					remove(Thirsty.this, item.getId());
					// Add new to pending list.
					add(item);
				}
			}
		}.executeParallel();
	}


	/**
	 * Specify the implementation of starting {@link android.app.AlarmManager}.
	 *
	 * @param mgr
	 * 		A {@link android.app.AlarmManager}.
	 * @param timeToAlarm
	 * 		First time to do the task.
	 * @param intent
	 * 		The pending that will be fired when task will be done by {@link android.app.AlarmManager} future.
	 */
	protected PendingIntent doCreateAlarmPending(AlarmManager mgr, long timeToAlarm, Intent intent) {
		intent.putExtra(EXTRAS_DO_NEXT, true);
		int reqCode = (int) System.currentTimeMillis();
		PendingIntent pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		LL.d("Pending reqCode: " + reqCode);
		if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			mgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm, pi);
		} else {
			mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm, pi);
		}
		return pi;
	}
}

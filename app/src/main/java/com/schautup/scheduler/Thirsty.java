package com.schautup.scheduler;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
	public static final String EXTRAS_ITEM_ID = "com.schautup.scheduler.Thirsty.ItemId";
	/**
	 * Cache of all intents that are under way to do the tasks scheduled by {@link android.app.AlarmManager}.
	 */
	private LongSparseArray<PendingIntent> mScheduledIntents = new LongSparseArray<PendingIntent>();

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
				remove(id);
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
		if (mScheduledIntents.get(item.getId()) != null) {//Removed the old one after update.
			remove(item.getId());
		}
		add(item);
	}

	//------------------------------------------------
	@Override
	public void onCreate() {
		EventBus.getDefault().register(this);
		super.onCreate();
		new ParallelTask<Void, Void, List<ScheduleItem>>(true) {
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
		calendar.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
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
		Intent intent = new Intent(this, doGetReceiver());
		intent.putExtra(EXTRAS_ITEM_ID, item.getId());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_ONE_SHOT);
		doSetAlarmManager(mgr, timeToAlarm, pendingIntent);
		mScheduledIntents.put(item.getId(), pendingIntent);
	}

	/**
	 * Removed all scheduled tasks and cancel them.
	 */
	private void removeAll() {
		long id;
		for (int i = 0; i < mScheduledIntents.size(); i++) {
			id = mScheduledIntents.keyAt(i);
			remove(id);
		}
	}

	/**
	 * To remove a task from pending of {@link android.app.AlarmManager}.
	 *
	 * @param id
	 * 		The id pending in the list of pending.
	 */
	private void remove(long id) {
		AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = mScheduledIntents.get(id);
		if (pi != null) {
			mScheduledIntents.remove(id);
			mgr.cancel(pi);
		}
	}

	/**
	 * To update a new pending when a pending was finished, that means the next round of schedule will be added and
	 * started.
	 *
	 * @param id
	 * 		The id pending in the list of pending.
	 */
	private void update(long id) {
		//I think it should do a select from DB, because we can't sure
		//that whether the schedule should be continued or not.
		List<ScheduleItem> items = DB.getInstance(getApplication()).getSchedules(id);
		ScheduleItem item = items.get(0);
		if (item != null) {//Removed old pending what has been finished.
			remove(item.getId());
		}
		add(items.get(0));
	}

	/**
	 * Specify the {@link android.content.BroadcastReceiver} that will be called by {@link android.app.AlarmManager}.
	 *
	 * @return The type of {@link android.content.BroadcastReceiver} to get callback from {@link
	 * android.app.AlarmManager}.
	 */
	protected Class<?> doGetReceiver() {
		return ThirstyReceiver.class;
	}

	/**
	 * Specify the implementation of starting {@link android.app.AlarmManager}.
	 *
	 * @param mgr
	 * 		A {@link android.app.AlarmManager}.
	 * @param timeToAlarm
	 * 		First time to do the task.
	 * @param pendingIntent
	 * 		The pending that will be fired when task will be done by {@link android.app.AlarmManager} future.
	 */
	protected void doSetAlarmManager(AlarmManager mgr, long timeToAlarm, PendingIntent pendingIntent) {
		if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			mgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm, pendingIntent);
		} else {
			mgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeToAlarm, pendingIntent);
		}
	}
}

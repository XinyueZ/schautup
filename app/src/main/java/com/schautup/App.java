/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。

package com.schautup;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import com.chopping.application.LL;
import com.chopping.exceptions.OperationFailException;
import com.chopping.net.TaskHelper;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.Brightness;
import com.schautup.activities.MainActivity;
import com.schautup.bus.AddedHistoryEvent;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.HistoryItem;
import com.schautup.data.Level;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.scheduler.AlarmReceiver;
import com.schautup.scheduler.ScheduleManager;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;


/**
 * The app-object of the project.
 *
 * @author Xinyue Zhao
 */
public final class App extends Application {

	/**
	 * Instance of this application.
	 */
	private static App sIns;


	//----------------------------------------------------------
	// Description: A receiver for system time-tick.
	//
	// Impl of Hungry mod.
	//----------------------------------------------------------
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
			doSchedules(DateTime.now());
		}
	};


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------


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
				remove( id);
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
		if (sPendingIntents.get(item.getId()) != null) {//Removed the old one after update.
			remove( item.getId());
		}
		add(item);
	}


	//------------------------------------------------


	//----------------------------------------------------------
	// Description: Workflow controlling.
	//
	// Define methods how to start, stop, schedules, change current
	// schedule mode.
	//----------------------------------------------------------
	/**
	 * Start doing, waiting, finishing all scheduled items.
	 */
	public void work() {
		LL.i("SchautUp works");
		String mod = Prefs.getInstance(this).getScheduleMode();
		int mode = Integer.valueOf(mod.toString());
		switch (mode) {
		case 0:
			registerReceiver(mReceiver, mIntentFilter);
			LL.i("SchautUp works with Hungry.");
			break;
		case 1:
		case 2:
			initThirstyNeutral();
			LL.i(String.format("SchautUp works with %s.", mode == 1 ? "Thirsty" : "Neutral"));
			break;
		}
	}

	/**
	 * Stop doing, waiting, finishing all scheduled items.
	 */
	public void pause() {
		String mod = Prefs.getInstance(this).getScheduleMode();
		int mode = Integer.valueOf(mod.toString());
		switch (mode) {
		case 0:
			try {
				unregisterReceiver(mReceiver);
				LL.i("SchautUp paused Hungry.");
			} catch (IllegalArgumentException e) {
				//Ignore if not registered before.
			}
			break;
		case 1:
		case 2:
			removeAll();
			LL.i(String.format("SchautUp paused %s.", mode == 1 ? "Thirsty" : "Neutral"));
			break;
		}
	}

	/**
	 * Change current schedule mode in system.
	 * @param newMode New id of mode. 0-hungry, 1-thirsty, 2-Neutral.
	 */
	public void changeMode(int newMode) {
		String mod = Prefs.getInstance(this).getScheduleMode();
		int oldMode = Integer.valueOf(mod.toString());
		switch (newMode) {
		case 0:
			removeAll();
			LL.i(String.format("SchautUp paused %s.", oldMode == 1 ? "Thirsty" : "Neutral"));
			registerReceiver(mReceiver, mIntentFilter);
			LL.i("SchautUp works with Hungry.");
			break;
		case 1:
		case 2:
			if(oldMode == 0) {
				try {
					unregisterReceiver(mReceiver);
					LL.i("SchautUp paused Hungry.");
				} catch (IllegalArgumentException e) {
					//Ignore if not registered before.
				}
			} else if(oldMode == 1 || oldMode == 2) {
				removeAll();
				LL.i(String.format("SchautUp paused %s.", oldMode == 1 ? "Thirsty" : "Neutral"));
			}
			initThirstyNeutral();
			LL.i(String.format("SchautUp works with %s.", newMode == 1 ? "Thirsty" : "Neutral"));
			break;
		}
	}

	/**
	 * Initialize the data for Thirsty and Neutral modes.
	 */
	private void initThirstyNeutral() {
		new ParallelTask<Void, Void, List<ScheduleItem>>(false) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				return Utils.getAllSchedules(App.this);
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				super.onPostExecute(items);
				addAll(items);
			}
		}.executeParallel();
	}
	//----------------------------------------------------------


	/**
	 * To get application's instance.
	 *
	 * @return The instance of the application.
	 */
	public static App getInstance() {
		return sIns;
	}

	@Override
	public void onCreate() {
		sIns = this;
		EventBus.getDefault().register(this);
		super.onCreate();

		Prefs prefs = Prefs.getInstance(this);
		if (prefs.isEULAOnceConfirmed()) {
			startService(new Intent(this, ScheduleManager.class));
			work();
		}

		TaskHelper.init(getApplicationContext());
	}


	//----------------------------------------------------------
	// Description: Do tasks.
	//----------------------------------------------------------

	/**
	 * Do schedules at {@code time}.
	 *
	 * @param time
	 * 		A time point.
	 */
	private void doSchedules(DateTime time) {

		new ParallelTask<DateTime, Void, List<ScheduleItem>>(false) {
			@Override
			protected List<ScheduleItem> doInBackground(DateTime... params) {
				DateTime time = params[0];
				return DB.getInstance(App.this).getSchedules(time.getHourOfDay(), time.getMinuteOfHour(),
						Utils.dateTimeDay2String(time.getDayOfWeek()));
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				super.onPostExecute(items);
				if(items != null && items.size() > 0) {
					for (ScheduleItem item : items) {
						doScheduleTask(item);
					}
				}
			}
		}.executeParallel(time);


	}


	/**
	 * Do schedules for the item with {@code id}.
	 *
	 * @param id
	 * 		The id pending in the list of pending.
	 * @param doNext
	 * 		{@code true} if called by alarm of Thirsty mode. {@code false} if it is from neutral mode.
	 */
	public synchronized  void doSchedules(final long id, final boolean doNext) {
		new ParallelTask<Void, List<ScheduleItem>, List<ScheduleItem>>(false) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				DateTime now = DateTime.now();
				return DB.getInstance(App.this).getSchedules(id,
						Utils.dateTimeDay2String(now.getDayOfWeek()));

			}
			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				if(items != null && items.size() > 0) {
					for (ScheduleItem item : items) {
						doScheduleTask(item);
					}
				}

				if (doNext) {
					//For "Neutral", this call must be ignored. Only for "Thirsty".
					update(id);
				}
			}
		}.executeParallel();

	}


	/**
	 * Run the scheduled task.
	 *
	 * @param item
	 * 		The data-module of a schedule.
	 */
	private void doScheduleTask(ScheduleItem item) {
		HistoryItem historyItem;
		String actionContent = null;
		String comment = null;
		switch (item.getType()) {
		case MUTE:
			if (DeviceUtils.setRingMode(this, RINGER_MODE_SILENT)) {
				sendNotification(this, new Result(getString(R.string.notify_mute_simple_content), getString(
						R.string.notify_mute_headline), getString(R.string.notify_mute_content),
						R.drawable.ic_mute_notify));
			} else {
				actionContent = getString(R.string.notify_mute_simple_content);
				comment = getString(R.string.lbl_function_is_running, getString(R.string.option_mute), getString(
						R.string.lbl_on_small));
			}
			break;
		case VIBRATE:
			if (DeviceUtils.setRingMode(this, RINGER_MODE_VIBRATE)) {
				sendNotification(this, new Result(getString(R.string.notify_vibrate_simple_content), getString(
						R.string.notify_vibrate_headline), getString(R.string.notify_vibrate_content),
						R.drawable.ic_vibrate_notify));
			} else {
				actionContent = getString(R.string.notify_vibrate_simple_content);
				comment = getString(R.string.lbl_function_is_running, getString(R.string.option_vibrate), getString(
						R.string.lbl_on_small));
			}
			break;
		case SOUND:
			if (DeviceUtils.setRingMode(this, RINGER_MODE_NORMAL)) {
				sendNotification(this, new Result(getString(R.string.notify_sound_simple_content), getString(
						R.string.notify_sound_headline), getString(R.string.notify_sound_content),
						R.drawable.ic_sound_notify));
			} else {
				actionContent = getString(R.string.notify_sound_simple_content);
				comment = getString(R.string.lbl_function_is_running, getString(R.string.option_sound), getString(
						R.string.lbl_on_small));
			}
			break;
		case WIFI:
			try {
				boolean wifiSuccess;
				if (Boolean.valueOf(item.getReserveLeft())) {
					wifiSuccess = DeviceUtils.setWifiEnabled(this, true);
					if (wifiSuccess) {
						sendNotification(this, new Result(String.format(getString(R.string.notify_wifi_simple_content),
								getString(R.string.lbl_on)), getString(R.string.notify_wifi_headline), String.format(
								getString(R.string.notify_wifi_content), getString(R.string.lbl_on)),
								R.drawable.ic_wifi_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_wifi_simple_content), getString(
								R.string.lbl_on));
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_wifi),
								getString(R.string.lbl_on_small));
					}
				} else {
					wifiSuccess = DeviceUtils.setWifiEnabled(this, false);
					if (wifiSuccess) {
						sendNotification(this, new Result(String.format(getString(R.string.notify_wifi_content),
								getString(R.string.lbl_off)), getString(R.string.notify_wifi_headline), String.format(
								getString(R.string.notify_wifi_content), getString(R.string.lbl_off)),
								R.drawable.ic_no_wifi_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_wifi_content), getString(
								R.string.lbl_off));
						comment = new StringBuilder().append(getString(R.string.lbl_wifi_in_sleep)).append('\n').append(getString(R.string.lbl_function_is_running, getString(R.string.option_wifi),
								getString(R.string.lbl_off_small))).append('\n').append(getString(R.string.lbl_left_from_wifi)).toString();
					}
				}
			} catch (OperationFailException e) {
				comment = new StringBuilder().append(getString(R.string.lbl_can_not_set, getString(
						R.string.option_wifi))).append(getString(R.string.lbl_operation_fail)).toString();
			}
			break;
		case MOBILE:
			try {
				Boolean mobileDataSuccess;
				if (Boolean.valueOf(item.getReserveLeft())) {
					mobileDataSuccess = DeviceUtils.setMobileDataEnabled(this, true);
					if (mobileDataSuccess) {
						sendNotification(this, new Result(String.format(getString(
								R.string.notify_mobile_simple_content), getString(R.string.lbl_on)), getString(
								R.string.notify_mobile_headline), String.format(getString(
								R.string.notify_mobile_content), getString(R.string.lbl_on_small), getString(
								R.string.lbl_can)), R.drawable.ic_mobile_data_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_mobile_simple_content), getString(
								R.string.lbl_on));
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_mobile),
								getString(R.string.lbl_on_small)) + "\n" + getString(R.string.lbl_left_from_mobile);
					}
				} else {
					mobileDataSuccess = DeviceUtils.setMobileDataEnabled(this, false);
					if (mobileDataSuccess) {
						sendNotification(this, new Result(String.format(getString(
								R.string.notify_mobile_simple_content), getString(R.string.lbl_off)), getString(
								R.string.notify_mobile_headline), String.format(getString(
								R.string.notify_mobile_content), getString(R.string.lbl_off_small), getString(
								R.string.lbl_can_not)), R.drawable.ic_no_mobile_data_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_mobile_simple_content), getString(
								R.string.lbl_off));
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_mobile),
								getString(R.string.lbl_off_small));
					}
				}
			} catch (OperationFailException e) {
				comment = new StringBuilder().append(getString(R.string.lbl_can_not_set, getString(
						R.string.option_mobile))).append(getString(R.string.lbl_operation_fail)).toString();
			}
			break;
		case BRIGHTNESS:
			Level level = Level.fromInt(Integer.valueOf(item.getReserveLeft()));
			switch (level) {
			case MAX:
				DeviceUtils.setBrightness(this, Brightness.MAX);
				break;
			case MEDIUM:
				DeviceUtils.setBrightness(this, Brightness.MEDIUM);
				break;
			case MIN:
				DeviceUtils.setBrightness(this, Brightness.MIN);
				break;
			}
			if (level != null) {
				sendNotification(this, new Result(String.format(getString(R.string.notify_brightness_simple_content),
						getString(level.getLevelResId())), getString(R.string.notify_brightness_headline),
						String.format(getString(R.string.notify_brightness_content), getString(level.getLevelResId())),
						R.drawable.ic_brightness_notify));
			}
			break;
		case BLUETOOTH:
			try {
				boolean bluetoothSuccess;
				if (Boolean.valueOf(item.getReserveLeft())) {
					bluetoothSuccess = DeviceUtils.setBluetoothEnabled(true);
					if (bluetoothSuccess) {
						sendNotification(this, new Result(String.format(getString(
								R.string.notify_bluetooth_simple_content), getString(R.string.lbl_on)), getString(
								R.string.notify_bluetooth_headline), String.format(getString(
								R.string.notify_bluetooth_content), getString(R.string.lbl_on)),
								R.drawable.ic_bluetooth_on_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_bluetooth_simple_content), getString(
								R.string.lbl_on));
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_bluetooth),
								getString(R.string.lbl_on_small));
					}
				} else {
					bluetoothSuccess = DeviceUtils.setBluetoothEnabled(false);
					if (bluetoothSuccess) {
						sendNotification(this, new Result(String.format(getString(R.string.notify_bluetooth_content),
								getString(R.string.lbl_off)), getString(R.string.notify_bluetooth_headline),
								String.format(getString(R.string.notify_bluetooth_content), getString(
										R.string.lbl_off)), R.drawable.ic_bluetooth_off_notify));
					} else {
						actionContent = String.format(getString(R.string.notify_bluetooth_content), getString(
								R.string.lbl_off));
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_bluetooth),
								getString(R.string.lbl_off_small));
					}
				}
			} catch (OperationFailException e) {
				comment = new StringBuilder().append(getString(R.string.lbl_can_not_set, getString(
						R.string.option_bluetooth))).append(getString(R.string.lbl_operation_fail)).toString();
			}
			break;
		case STARTAPP:
			com.chopping.utils.Utils.showLongToast(this, "Coming soon...");
			break;
		case CALLABORT:
			com.chopping.utils.Utils.showLongToast(this, "Coming soon...");
			break;
		}
		DB db = DB.getInstance(this);
		historyItem = new HistoryItem(item.getType());
		historyItem.setComment(comment);
		db.logHistory(historyItem);
		if (!TextUtils.isEmpty(comment)) {
			sendNotification(this, new Result(getString(R.string.notify_can_not_set), getString(
					R.string.lbl_avoid_change), actionContent, R.drawable.ic_some_warnings_notify), comment);
		}
		EventBus.getDefault().post(new AddedHistoryEvent(historyItem));
	}

	//----------------------------------------------------------
	// Description: Notification for task completion.
	//----------------------------------------------------------


	/**
	 * A bundle that contains information about a being set schedule item.
	 *
	 * @author Xinyue Zhao
	 */
	private static class Result {
		/**
		 * Simple information about a finished scheduled item.
		 */
		private String mSimpleContent;
		/**
		 * Headline(title) of the {@link #mContent}.
		 */
		private String mHeadline;
		/**
		 * Rich information about a finished scheduled item.
		 */
		private String mContent;
		/**
		 * An icon to a finished scheduled item.
		 */
		private int mIcon;

		/**
		 * Constructor of {@link Result}.
		 *
		 * @param simpleContent
		 * 		Simple information about a finished scheduled item.
		 * @param headline
		 * 		Headline(title) of the {@link #mContent}.
		 * @param content
		 * 		Rich information about a finished scheduled item.
		 * @param icon
		 * 		An icon to a finished scheduled item.
		 */
		public Result(String simpleContent, String headline, String content, int icon) {
			mSimpleContent = simpleContent;
			mHeadline = headline;
			mContent = content;
			mIcon = icon;
		}

		/**
		 * Get simple information about a finished scheduled item.
		 */
		public String getSimpleContent() {
			return mSimpleContent;
		}

		/**
		 * Get the headline(title) of the {@link #mContent}.
		 */
		public String getHeadline() {
			return mHeadline;
		}

		/**
		 * Get rich information about a finished scheduled item.
		 */
		public String getContent() {
			return mContent;
		}

		/**
		 * Get an icon to a finished scheduled item.
		 *
		 * @return {@link android.support.annotation.DrawableRes}.
		 */
		public int getIcon() {
			return mIcon;
		}
	}


	/**
	 * Get the {@link android.support.v4.app.NotificationCompat.Builder} of {@link android.app.Notification}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param ticker
	 * 		Text shows on notification.
	 * @param smallIcon
	 * 		Icon for the notification.
	 * @param contentTitle
	 * 		Text shows above the content.
	 * @param content
	 * 		Text shows when expand notification.
	 * @param id
	 * 		The id of this notification.
	 * @param bigText
	 * 		Some more information.
	 *
	 * @return A {@link android.support.v4.app.NotificationCompat.Builder}.
	 */
	private static NotificationCompat.Builder buildNotificationCommon(Context cxt, String ticker,
			@DrawableRes int smallIcon, String contentTitle, String content, int id, String bigText) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(cxt).setWhen(System.currentTimeMillis())
				.setTicker(ticker).setAutoCancel(true).setSmallIcon(smallIcon).setLargeIcon(
						BitmapFactory.decodeResource(cxt.getResources(), R.drawable.ic_action_logo)).setContentIntent(
						createMainPendingIntent(cxt, id)).setContentTitle(contentTitle).setContentText(content);

		AudioManager audioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() == RINGER_MODE_VIBRATE) {
			builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000, 1000 });
		}
		if (audioManager.getRingerMode() == RINGER_MODE_NORMAL) {
			builder.setSound(Uri.parse(String.format("android.resource://%s/%s", cxt.getPackageName(),
					R.raw.sound_bell)));
		}
		builder.setLights(Color.BLUE, 3000, 3000);

		if (!TextUtils.isEmpty(bigText)) {
			BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
			inboxStyle.setBigContentTitle(contentTitle);
			inboxStyle.bigText(bigText);
			inboxStyle.setSummaryText(content);
			builder.setStyle(inboxStyle);
		}
		return builder;
	}

	/**
	 * Action pending for clicking the {@link android.app.Notification}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param reqCode
	 * 		The request code after clicking  the {@link android.app.Notification}.
	 *
	 * @return A {@link android.app.PendingIntent}  clicking  the {@link android.app.Notification}.
	 */
	static PendingIntent createMainPendingIntent(Context cxt, int reqCode) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(cxt, reqCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	/**
	 * Send a {@link android.app.Notification} when do schedule.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param res
	 * 		The feedback after finishing schedule.
	 * @param bigText
	 * 		More text.
	 */
	private void sendNotification(Context cxt, Result res, String bigText) {
		int notificationID = (int) System.currentTimeMillis();
		((NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationID,
				buildNotificationCommon(cxt, res.getSimpleContent(), res.getIcon(), res.getHeadline(), res.getContent(),
						notificationID, bigText).build());


	}

	/**
	 * Send a {@link android.app.Notification} when do schedule.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param res
	 * 		The feedback after finishing schedule.
	 */
	private void sendNotification(Context cxt, Result res) {
		sendNotification(cxt, res, null);
	}


	//----------------------------------------------------------
	// Description: Usage on AlarmManager
	//
	// Impl of Thirsty and Neutral modes.
	//----------------------------------------------------------

	/**
	 * Extras store the item id (also the id of pending) when event will be fired by {@link android.app.AlarmManager}.
	 */
	public static final String EXTRAS_ITEM_ID = "com.schautup.app.ItemId";

	/**
	 * Extras boolean for Thirsty mode to simulate the repeating on {@link android.app.AlarmManager}.
	 */
	public static final String EXTRAS_DO_NEXT = "com.schautup.app.do.next";
	/**
	 * Cache of all intents that are under way to do the tasks scheduled by {@link android.app.AlarmManager}.
	 */
	private static volatile LongSparseArray<PendingIntent> sPendingIntents = new LongSparseArray<PendingIntent>();


	/**
	 * Removed all scheduled tasks and cancel them.
	 */
	private void removeAll() {
		long id;
		for (int i = 0; i < sPendingIntents.size(); i++) {
			id = sPendingIntents.keyAt(i);
			remove(  id);
		}
	}

	/**
	 * To remove a task from pending of {@link android.app.AlarmManager}.
	 *
	 * @param id
	 * 		The id pending in the list of pending.
	 *
	 * @return {@code true} if find the pending with {@code id} and has been removed. {@code false} if the pending with
	 * {@code id} can't be found.
	 */
	public  synchronized boolean remove(  long id) {
		AlarmManager mgr = (AlarmManager)  getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = sPendingIntents.get(id);
		if (pi != null) {
			sPendingIntents.remove(id);
			mgr.cancel(pi);
			return true;
		}

		return false;
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
		// Current time point.
		long currentTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTime);
		calendar.set(Calendar.HOUR_OF_DAY, item.getHour());
		calendar.set(Calendar.MINUTE,  item.getMinute());
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long setTime = calendar.getTimeInMillis();
		if (currentTime >= setTime) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			setTime = calendar.getTimeInMillis();
		}
		Intent intent = new Intent(this, AlarmReceiver.class);
		intent.putExtra(EXTRAS_ITEM_ID, item.getId());
		PendingIntent pendingIntent = createAlarmPending(mgr, setTime, intent);
		sPendingIntents.put(item.getId(), pendingIntent);
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
				return DB.getInstance(App.this).getSchedules(id);
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> items) {
				super.onPostExecute(items);
				ScheduleItem item = items.get(0);
				//Plane next schedule
				if (item != null) {
					// Removed old pending what has been finished and re-add.
					// It should have been removed when the helper-service work off the schedule.
					remove(  item.getId());
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
	private PendingIntent createAlarmPending(AlarmManager mgr, long timeToAlarm, Intent intent) {
		String mod = Prefs.getInstance(this).getScheduleMode();
		int mode = Integer.valueOf(mod.toString());
		int reqCode;
		PendingIntent pi;
		switch (mode) {
		// Usage of repeating on the alarmmanger for android >= 4.4 .
		// The Thirsty mode.
		case 1:
			intent.putExtra(EXTRAS_DO_NEXT, true);
			reqCode = (int) System.currentTimeMillis();
			pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			LL.d("Pending reqCode(Thirsty): " + reqCode);
			if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
				mgr.setExact(AlarmManager.RTC_WAKEUP, timeToAlarm, pi);
			} else {
				mgr.set(AlarmManager.RTC_WAKEUP, timeToAlarm, pi);
			}
			return pi;


		//Neutral mode, for repeating on the alarmmanger below android 4.4 .
		case 2:
			intent.putExtra(EXTRAS_DO_NEXT, false);
			reqCode = (int) System.currentTimeMillis();
			//http://stackoverflow.com/questions/4700058/android-repeating-alarm-not-working
			pi = PendingIntent.getBroadcast(this, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			LL.d("Pending reqCode(Neutral): " + reqCode);
			//http://stackoverflow.com/questions/16308783/timeunit-seconds-tomillis
			//http://stackoverflow.com/questions/6980376/convert-from-days-to-milliseconds
			mgr.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm, TimeUnit.DAYS.toMillis(1), pi);
			return pi;

		}
		return null;
	}
}

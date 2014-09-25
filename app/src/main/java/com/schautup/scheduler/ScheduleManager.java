package com.schautup.scheduler;

import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.chopping.application.LL;
import com.chopping.exceptions.OperationFailException;
import com.chopping.utils.DeviceUtils;
import com.chopping.utils.DeviceUtils.Brightness;
import com.schautup.R;
import com.schautup.activities.MainActivity;
import com.schautup.activities.QuickSettingsActivity;
import com.schautup.bus.AddedHistoryEvent;
import com.schautup.bus.DoSchedulesAtTimeEvent;
import com.schautup.bus.DoSchedulesForIdEvent;
import com.schautup.bus.ScheduleManagerPauseEvent;
import com.schautup.bus.ScheduleManagerWorkEvent;
import com.schautup.bus.ScheduleNextEvent;
import com.schautup.data.HistoryItem;
import com.schautup.data.Level;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

/**
 * A {@link android.app.Service} that hosts a ongoing notification that gives user some shortcut operations to control
 * the application when the notification's clicked. It might keep the application long time in background.
 *
 * @author Xinyue Zhao
 */
public class ScheduleManager extends Service {

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.DoSchedulesAtTimeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.DoSchedulesAtTimeEvent}.
	 */
	public void onEvent(DoSchedulesAtTimeEvent e) {
		new ParallelTask<DateTime, Void, Void>(false) {
			@Override
			protected Void doInBackground(DateTime... params) {
				doSchedules(params[0]);
				return null;
			}
		}.executeParallel(e.getTime());
	}


	/**
	 * Handler for {@link com.schautup.bus.DoSchedulesForIdEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.DoSchedulesForIdEvent}.
	 */
	public void onEvent(DoSchedulesForIdEvent e) {
		new ParallelTask<DoSchedulesForIdEvent, DoSchedulesForIdEvent, DoSchedulesForIdEvent>(false) {
			@Override
			protected DoSchedulesForIdEvent doInBackground(DoSchedulesForIdEvent... params) {
				DoSchedulesForIdEvent event = params[0];
				doSchedules(event.getId());
				return event;
			}

			@Override
			protected void onPostExecute(DoSchedulesForIdEvent event) {
				super.onPostExecute(event);
				if (event.isDoNext()) {
					//For "Neutral", this call must be ignored.
					//Only for "Thirsty".
					EventBus.getDefault().post(new ScheduleNextEvent(event.getId()));
				}

			}
		}.executeParallel(e);

	}

	/**
	 * Handler for {@link com.schautup.bus.ScheduleManagerWorkEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ScheduleManagerWorkEvent}.
	 */
	public void onEvent(ScheduleManagerWorkEvent e) {
		work();
	}

	/**
	 * Handler for {@link com.schautup.bus.ScheduleManagerPauseEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ScheduleManagerPauseEvent}.
	 */
	public void onEvent(ScheduleManagerPauseEvent e) {
		pause();
	}


	//------------------------------------------------

	/**
	 * Provide an ongoing {@link android.app.Notification} that keeps the application running long time in background.
	 */
	private void sendForegroundNotification() {
		Intent intent = new Intent(this, QuickSettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis())
				.setTicker(getString(R.string.notify_foreground_simple_content, getString(R.string.app_name))).setAutoCancel(true).setSmallIcon(
						R.drawable.ic_action_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_action_logo)).setContentTitle(getString(R.string.notify_foreground_headline))
				.setContentText(getString(R.string.notify_foreground_content)).setContentIntent(pendingIntent);
		startForeground((int) System.currentTimeMillis(), builder.build());
	}


	public ScheduleManager() {
		//Do nothing.
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void onCreate() {
		EventBus.getDefault().register(this);
		super.onCreate();
		LL.i("ScheduleManager#Create.");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LL.i("ScheduleManager#StartCommand.");
		sendForegroundNotification();
		work();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		pause();
		super.onDestroy();
		LL.i("ScheduleManager#Destroy.");
	}

	/**
	 * {@link com.schautup.scheduler.ScheduleManager} works.
	 */
	private void work() {
		LL.i("ScheduleManager#work");
		String mod = Prefs.getInstance(getApplication()).getScheduleMode();
		switch (Integer.valueOf(mod.toString())) {
		case 0:
			startService(new Intent(this, Hungry.class));
			break;
		case 1:
			startService(new Intent(this, Thirsty.class));
			break;
		case 2:
			startService(new Intent(this, Neutral.class));
			break;
		}
	}

	/**
	 * {@link com.schautup.scheduler.ScheduleManager} do pause.
	 */
	private void pause() {
		LL.i("ScheduleManager#pause");
		String mod = Prefs.getInstance(getApplication()).getScheduleMode();
		switch (Integer.valueOf(mod.toString())) {
		case 0:
			stopService(new Intent(this, Hungry.class));
			break;
		case 1:
			stopService(new Intent(this, Thirsty.class));
			break;
		case 2:
			stopService(new Intent(this, Neutral.class));
			break;
		}
	}

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
	 *
	 * @return A {@link android.support.v4.app.NotificationCompat.Builder}.
	 */
	private static NotificationCompat.Builder buildNotificationCommon(Context cxt, String ticker,
			@DrawableRes int smallIcon, String contentTitle, String content, int id) {
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
		builder.setLights(Color.RED, 3000, 3000);
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
	 */
	private void sendNotification(Context cxt, Result res) {
		int notificationID = (int) System.currentTimeMillis();
		((NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationID,
				buildNotificationCommon(cxt, res.getSimpleContent(), res.getIcon(), res.getHeadline(), res.getContent(),
						notificationID).build());


	}

	/**
	 * Do schedules at {@code time}.
	 *
	 * @param time
	 * 		A time point.
	 */
	private void doSchedules(DateTime time) {
		List<ScheduleItem> items = DB.getInstance(getApplication()).getSchedules(time.getHourOfDay(),
				time.getMinuteOfHour(), Utils.dateTimeDay2String(time.getDayOfWeek()));

		for (ScheduleItem item : items) {
			LL.d("Doing task by " + item.getId() );
			doScheduleTask(item);
		}
	}


	/**
	 * Do schedules for the item with {@code id}.
	 *
	 * @param id
	 * 		The id of a {@link com.schautup.data.ScheduleItem} in the pending-list and also in the location in DB.
	 */
	private void doSchedules(long id) {
		DateTime now = DateTime.now();
		List<ScheduleItem> items = DB.getInstance(getApplication()).getSchedules(id, now.getHourOfDay(),
				now.getMinuteOfHour(), Utils.dateTimeDay2String(now.getDayOfWeek()));

		for (ScheduleItem item : items) {
			 doScheduleTask(item);
		}
	}


	/**
	 * Run the scheduled task.
	 * @param item The data-module of a schedule.
	 */
	private void doScheduleTask(ScheduleItem item) {
		HistoryItem historyItem;
		String comment =   null;
		switch (item.getType()) {
		case MUTE:
			if (DeviceUtils.setRingMode(this, RINGER_MODE_SILENT)) {
				sendNotification(this, new Result(getString(R.string.notify_mute_simple_content), getString(
						R.string.notify_mute_headline), getString(R.string.notify_mute_content),
						R.drawable.ic_mute_notify));
			} else {
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
						sendNotification(this, new Result(String.format(getString(
								R.string.notify_wifi_simple_content), getString(R.string.lbl_on)), getString(
								R.string.notify_wifi_headline), String.format(getString(
								R.string.notify_wifi_content), getString(R.string.lbl_on)),
								R.drawable.ic_wifi_notify));
					} else {
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_wifi),
								getString(R.string.lbl_on_small));
					}
				} else {
					wifiSuccess = DeviceUtils.setWifiEnabled(this, false);
					if (wifiSuccess) {
						sendNotification(this, new Result(String.format(getString(R.string.notify_wifi_content),
								getString(R.string.lbl_off)), getString(R.string.notify_wifi_headline),
								String.format(getString(R.string.notify_wifi_content), getString(R.string.lbl_off)),
								R.drawable.ic_no_wifi_notify));
					} else {
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_wifi),
								getString(R.string.lbl_off_small)) + "<p/>" + getString(R.string.lbl_left_from_wifi);
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
						comment = getString(R.string.lbl_function_is_running, getString(R.string.option_mobile),
								getString(R.string.lbl_on_small));
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
			if(level != null) {
				sendNotification(this, new Result(String.format(getString(
						R.string.notify_brightness_simple_content), getString(level.getLevelResId())), getString(
						R.string.notify_brightness_headline), String.format(getString(
						R.string.notify_brightness_content), getString(level.getLevelResId())),
						R.drawable.ic_brightness_notify));
			}
			break;
		}
		DB db = DB.getInstance(getApplication());
		historyItem = new HistoryItem(item.getType());
		historyItem.setComment(comment);
		db.logHistory(historyItem);
		EventBus.getDefault().post(new AddedHistoryEvent(historyItem));
	}

}

package com.schautup.scheduler;

import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.schautup.App;
import com.schautup.MainActivity;
import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;

import org.joda.time.DateTime;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

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
		public void onReceive(final Context cxt, Intent intent) {
			new ParallelTask<Void, Void, Void>(false) {
				@Override
				protected Void doInBackground(Void... params) {
					DateTime now = DateTime.now();
					List<ScheduleItem> items = DB.getInstance((App) cxt.getApplicationContext()).getSchedules(
							now.getHourOfDay(), now.getMinuteOfHour());
					AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					int notificationID;
					for (ScheduleItem item : items) {
						switch (item.getType()) {
						case MUTE:
							audioManager.setRingerMode(RINGER_MODE_SILENT);


							notificationID = (int) System.currentTimeMillis();
							((NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationID,
									buildNotificationCommon(
											cxt,
											cxt.getString(R.string.option_mute),
											R.drawable.ic_mute_notify,
											cxt.getString(R.string.option_mute),
											notificationID).build());
							break;
						case VIBRATE:
							audioManager.setRingerMode(RINGER_MODE_VIBRATE);


							notificationID = (int) System.currentTimeMillis();
							((NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationID,
									buildNotificationCommon(
											cxt,
											cxt.getString(R.string.option_vibrate),
											R.drawable.ic_vibrate_notify,
											cxt.getString(R.string.option_vibrate),
											notificationID).build());
							break;
						case SOUND:
							audioManager.setRingerMode(RINGER_MODE_NORMAL);


							notificationID = (int) System.currentTimeMillis();
							((NotificationManager) cxt.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationID,
									buildNotificationCommon(
											cxt,
											cxt.getString(R.string.option_sound),
											R.drawable.ic_sound_notify,
											cxt.getString(R.string.option_sound),
											notificationID).build());
							break;
						}
					}
					return null;
				}
			};
		}

		/**
		 * Get the {@link android.support.v4.app.NotificationCompat.Builder} of {@link
		 * android.app.Notification}.
		 *
		 * @param cxt
		 * 		{@link android.content.Context}.
		 * @param ticker
		 * 		Text shows on notification.
		 * @param smallIcon
		 * 		Icon for the notification.
		 * @param content
		 * 		Text shows when expand notification.
		 * @param id
		 * 		The id of this notification.
		 * @return A {@link android.support.v4.app.NotificationCompat.Builder}.
		 */
		private NotificationCompat.Builder buildNotificationCommon(Context cxt, String ticker,
				@DrawableRes int smallIcon, String content, int id) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(cxt).setWhen(System.currentTimeMillis())
					.setTicker(ticker).setAutoCancel(true).setSmallIcon(smallIcon).setContentIntent(
							createMainPendingIntent(cxt, id)).setContentTitle(content);

			builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
			builder.setLights(Color.RED, 3000, 3000);
			//builder.setSound(Uri.parse(tone));

			return builder;
		}

		/**
		 * Action pending for clicking the {@link android.app.Notification}.
		 * @param cxt {@link android.content.Context}.
		 * @param reqCode The request code after clicking  the {@link android.app.Notification}.
		 * @return A {@link android.app.PendingIntent}  clicking  the {@link android.app.Notification}.
		 */
		private PendingIntent createMainPendingIntent(Context cxt, int reqCode) {
			Intent intent = new Intent(cxt, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			return PendingIntent.getActivity(cxt, reqCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver(mReceiver, mIntentFilter);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
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

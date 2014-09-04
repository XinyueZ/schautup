package com.schautup.scheduler;

import java.lang.ref.WeakReference;
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
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.schautup.App;
import com.schautup.MainActivity;
import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;

import org.joda.time.DateTime;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

/**
 * The manager do all schedules with different methods.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleManager {
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
		 * Constructor of {@link com.schautup.scheduler.ScheduleManager.Result}.
		 *
		 * @param _simpleContent
		 * 		Simple information about a finished scheduled item.
		 * @param _headline
		 * 		Headline(title) of the {@link #mContent}.
		 * @param _content
		 * 		Rich information about a finished scheduled item.
		 * @param _icon
		 * 		An icon to a finished scheduled item.
		 */
		public Result(String _simpleContent, String _headline, String _content, int _icon) {
			mSimpleContent = _simpleContent;
			mHeadline = _headline;
			mContent = _content;
			mIcon = _icon;
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
	 * {@link android.content.Context}, but it is protected.
	 */
	private WeakReference<Context> mContextRef;


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
			@DrawableRes int smallIcon,
			String contentTitle, String content, int id) {
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
	 * Constructor of {@link com.schautup.scheduler.ScheduleManager}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public ScheduleManager(Context cxt) {
		mContextRef = new WeakReference<Context>(cxt);
	}

	/**
	 * The  {@link com.schautup.scheduler.ScheduleManager} does schedule at {@code time}.
	 *
	 * @param time
	 * 		A time point.
	 */
	public void workAt(DateTime time) {
		Context cxt = mContextRef.get();
		if (cxt != null) {
			List<ScheduleItem> items = DB.getInstance((App) cxt.getApplicationContext()).getSchedules(
					time.getHourOfDay(), time.getMinuteOfHour());
			AudioManager audioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
			for (ScheduleItem item : items) {
				switch (item.getType()) {
				case MUTE:
					audioManager.setRingerMode(RINGER_MODE_SILENT);
					sendNotification(cxt, new Result(cxt.getString(R.string.notify_mute_simple_content), cxt.getString(
							R.string.notify_mute_headline), cxt.getString(R.string.notify_mute_content),
							R.drawable.ic_mute_notify));
					break;
				case VIBRATE:
					audioManager.setRingerMode(RINGER_MODE_VIBRATE);
					sendNotification(cxt, new Result(cxt.getString(R.string.notify_vibrate_simple_content),
							cxt.getString(R.string.notify_vibrate_headline), cxt.getString(
							R.string.notify_vibrate_content), R.drawable.ic_vibrate_notify));
					break;
				case SOUND:
					audioManager.setRingerMode(RINGER_MODE_NORMAL);
					sendNotification(cxt, new Result(cxt.getString(R.string.notify_sound_simple_content), cxt.getString(
							R.string.notify_sound_headline), cxt.getString(R.string.notify_sound_content),
							R.drawable.ic_sound_notify));
					break;
				}
			}
		}
	}

}

package com.schautup.scheduler;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.schautup.R;
import com.schautup.activities.MainActivity;
import com.schautup.activities.QuickSettingsActivity;
import com.schautup.utils.Prefs;

/**
 * Tray the application on the notification center.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleManager extends Service {
	/**
	 * Extras and flag to indicate that the
	 */
	public static final String EXTRAS_STOPPED_CALL_ABORT = "com.schautup.scheduler.Stop.CallAbort";

	/**
	 * Provide an ongoing {@link android.app.Notification} that keeps the application running long time in background.
	 */
	private void sendForegroundNotification() {
		Intent intent = new Intent(this, QuickSettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		if(Prefs.getInstance(getApplication()).isRejectIncomingCall()) {
			RemoteViews statusV = new RemoteViews(getPackageName(), R.layout.inc_turn_off_reject_incoming);
			statusV.setOnClickPendingIntent(R.id.status_off_btn, buildViewClickIntent(this));
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				//http://stackoverflow.com/questions/18055721/android-custom-notification-appearance-issue
				Notification n = new Notification();
				n.contentView = statusV;
				n.icon = R.drawable.ic_action_logo;
				n.flags |= Notification.FLAG_ONGOING_EVENT;
				n.contentIntent = buildViewClickIntent(this);
				startForeground((int) System.currentTimeMillis(), n);
			} else {
				NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis())
						.setTicker(getString(R.string.notify_foreground_simple_content, getString(R.string.application_name)))
						.setAutoCancel(true).setSmallIcon(R.drawable.ic_action_logo).setContentIntent(
								pendingIntent).setContent(statusV);
				startForeground((int) System.currentTimeMillis(), builder.build());
			}
		} else {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis())
					.setTicker(getString(R.string.notify_foreground_simple_content, getString(R.string.application_name)))
					.setAutoCancel(true).setSmallIcon(R.drawable.ic_action_logo).setLargeIcon(
							BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_logo)).setContentIntent(
							pendingIntent).setContentTitle(getString(
							R.string.notify_foreground_headline)).setContentText(getString(
							R.string.notify_foreground_content));
			startForeground((int) System.currentTimeMillis(), builder.build());
		}
	}

	/**
	 * Constructor of {@link ScheduleManager}.
	 *
	 * No usage.
	 */
	public ScheduleManager() {
		//Do nothing.
	}


	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sendForegroundNotification();
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		stopForeground(true);
	}


	/**
	 * Make click event handler.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @return {@link android.app.PendingIntent} for the click event.
	 */
	private static PendingIntent buildViewClickIntent(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(EXTRAS_STOPPED_CALL_ABORT , true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}

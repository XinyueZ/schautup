package com.schautup.scheduler;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.schautup.R;
import com.schautup.activities.QuickSettingsActivity;

/**
 * Tray the application on the notification center.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleManager extends Service {
	/**
	 * Provide an ongoing {@link android.app.Notification} that keeps the application running long time in background.
	 */
	private void sendForegroundNotification() {
		Intent intent = new Intent(this, QuickSettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis())
				.setTicker(getString(R.string.notify_foreground_simple_content, getString(R.string.application_name)))
				.setAutoCancel(true).setSmallIcon(R.drawable.ic_action_logo).setLargeIcon(BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_action_logo)).setContentTitle(getString(
						R.string.notify_foreground_headline)).setContentText(getString(
						R.string.notify_foreground_content)).setContentIntent(pendingIntent);
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
	public int onStartCommand(Intent intent, int flags, int startId) {
		sendForegroundNotification();
		return super.onStartCommand(intent, flags, startId);
	}
}

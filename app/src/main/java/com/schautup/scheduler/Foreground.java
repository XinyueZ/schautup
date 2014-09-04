package com.schautup.scheduler;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.schautup.R;

/**
 * A {@link android.app.Service} that hosts a ongoing notification that gives user some shortcut operations to control
 * the application when the notification's clicked. It might keep the application long time in background.
 *
 * @author Xinyue Zhao
 */
public class Foreground extends Service {
	/**
	 * Provide a ongoing {@link android.app.Notification} that keeps the application running long time in background.
	 */
	private void sendForegroundNotification() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis())
				.setTicker(getText(R.string.notify_foreground_simple_content)).setAutoCancel(true).setSmallIcon(
						R.drawable.ic_logo_notify).setLargeIcon(BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_action_logo)).setContentTitle(getString(R.string.notify_foreground_headline))
				.setContentText(getString(R.string.notify_foreground_content)).setContentIntent(
						ScheduleManager.createMainPendingIntent(this, (int) System.currentTimeMillis()));
		startForeground((int) System.currentTimeMillis(), builder.build());
	}


	public Foreground() {
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

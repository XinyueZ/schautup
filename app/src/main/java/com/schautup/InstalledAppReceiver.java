package com.schautup;

import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.schautup.activities.MainActivity;
import com.schautup.bus.RemovedScheduledStartAppsEvent;
import com.schautup.db.DB;

import de.greenrobot.event.EventBus;

/**
 * Event that will be sent after an external App has been installed.
 *
 * @author Xinyue Zhao
 */
public final class InstalledAppReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//Info UI to refresh button status.
		Uri data = intent.getData();
		String packageName = data.getSchemeSpecificPart();
		if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			List<String> ids = DB.getInstance(context).removeScheduleForStartApplication(packageName);
			if (ids != null) {
				EventBus.getDefault().post(new RemovedScheduledStartAppsEvent(ids));


				PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
						new Intent(context, MainActivity.class).setFlags(
								Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK),
				PendingIntent.FLAG_CANCEL_CURRENT);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setWhen(
						System.currentTimeMillis()).setTicker(context.getString(R.string.notify_removed_app))
						.setContentTitle(context.getString(R.string.application_name)).setContentText(context.getString(
								R.string.notify_removed_app)).setAutoCancel(true).setSmallIcon(
								R.drawable.ic_action_logo).setLargeIcon(BitmapFactory.decodeResource(
								context.getResources(), R.drawable.ic_action_logo)).setContentIntent(pendingIntent);


				((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
						(int) System.currentTimeMillis(), builder.build());
			}
		}
	}
}

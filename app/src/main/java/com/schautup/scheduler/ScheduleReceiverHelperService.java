package com.schautup.scheduler;

import android.app.IntentService;
import android.content.Intent;

import com.chopping.application.LL;
import com.schautup.bus.DoSchedulesForIdEvent;

import de.greenrobot.event.EventBus;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in a service on a separate handler thread.
 * <p/>
 *
 * @author Android Studio
 */
public class ScheduleReceiverHelperService extends IntentService {
	/**
	 * Constructor of {@link ScheduleReceiverHelperService}.
	 * <p/>
	 * No usage.
	 */
	public ScheduleReceiverHelperService() {
		super("WakeUpHelperService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null) {
			long taskId = intent.getLongExtra(Thirsty.EXTRAS_ITEM_ID, -1);//Id in DB and also the id in pending-list.
			Thirsty.remove(this, taskId);
			boolean doNext = intent.getBooleanExtra(Thirsty.EXTRAS_DO_NEXT, false);
			LL.d("Post event to do schedule, item id: " + taskId );
			EventBus.getDefault().post(new DoSchedulesForIdEvent(taskId, doNext));
			ScheduleReceiver.completeWakefulIntent(intent);
		}
	}

}

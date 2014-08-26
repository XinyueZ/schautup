package com.schautup.utils;

import android.os.AsyncTask;
import android.os.Build;

import com.schautup.bus.ProgressbarEvent;

import de.greenrobot.event.EventBus;


public abstract class ParallelTask<T, Z, U> extends AsyncTask<T, Z, U> {
	/**
	 * Whether sending event to show progress-indicator.
	 */
	private boolean mShowIndicator = true;

	public ParallelTask(boolean showIndicator) {
		mShowIndicator = showIndicator;
	}

	public void executeParallel(T... args) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
		} else {
			execute(args);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mShowIndicator) {
			EventBus.getDefault().post(new ProgressbarEvent(true));
		}
	}

	@Override
	protected void onPostExecute(U _result) {
		if (mShowIndicator) {
			EventBus.getDefault().post(new ProgressbarEvent(false));
		}
		super.onPostExecute(_result);
	}
}

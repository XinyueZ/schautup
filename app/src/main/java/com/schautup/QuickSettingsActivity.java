package com.schautup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.schautup.bus.ScheduleManagerPauseEvent;
import com.schautup.bus.ScheduleManagerWorkEvent;
import com.schautup.utils.Prefs;

import de.greenrobot.event.EventBus;

/**
 * An {@link android.app.Activity} which shows as a dialog for user to set some options directly instead of opening the
 * application itself.
 *
 * @author Xinyue Zhao
 */
public final class QuickSettingsActivity extends Activity implements OnCheckedChangeListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_quick_settings;

	/**
	 * Show single instance of {@link QuickSettingsActivity}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, QuickSettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		CompoundButton cb = (CompoundButton) findViewById(R.id.pause_resume_cb);
		cb.setChecked(!Prefs.getInstance(getApplication()).isPause());
		cb.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Prefs.getInstance(getApplication()).setPause(!isChecked);
		if (isChecked) {
			//work
			EventBus.getDefault().post(new ScheduleManagerWorkEvent());
		} else {
			//pause
			EventBus.getDefault().post(new ScheduleManagerPauseEvent());
		}
	}

	/**
	 * Resume the task of application.
	 *
	 * @param view
	 * 		Do nothing.
	 */
	public void openApp(View view) {
		MainActivity.showInstance(this);
	}
}
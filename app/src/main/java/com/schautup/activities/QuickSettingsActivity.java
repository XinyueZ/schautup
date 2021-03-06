package com.schautup.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.schautup.App;
import com.schautup.R;
import com.schautup.utils.Prefs;

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
		SwitchCompat cb = (SwitchCompat) findViewById(R.id.pause_resume_cb);
		cb.setShowText(true);
		cb.setChecked(!Prefs.getInstance(getApplication()).isPause());
		cb.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Prefs.getInstance(getApplication()).setPause(!isChecked);
		if (isChecked) {
			App.getInstance().work();
		} else {
			App.getInstance().pause();
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

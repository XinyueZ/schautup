package com.schautup.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBarPreferenceActivity;
import android.view.MenuItem;

import com.schautup.BootReceiver;
import com.schautup.R;
import com.schautup.scheduler.ScheduleManager;
import com.schautup.utils.Prefs;

/**
 * Setting .
 */
public final class SettingsActivity extends ActionBarPreferenceActivity implements
		Preference.OnPreferenceChangeListener {
	/**
	 * Tricky to avoid first call.
	 */
	private boolean mInitModeList;
	/**
	 * Show an instance of SettingsActivity.
	 *
	 * @param context
	 * 		A context object.
	 */
	public static void showInstance(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setIcon(R.drawable.ic_action_settings);
		addPreferencesFromResource(R.xml.settings);

		//Schedule running mode.
		ListPreference modeList = (ListPreference) findPreference(Prefs.KEY_SCHEDULE_MODE);
		modeList.setOnPreferenceChangeListener(this);
		modeList.setValue(Prefs.getInstance(getApplication()).getScheduleMode());
		onPreferenceChange(modeList, Prefs.getInstance(getApplication()).getScheduleMode());

		//Run at boot or not.
		CheckBoxPreference runBoot = (CheckBoxPreference) findPreference(Prefs.KEY_RUN_BOOT);
		runBoot.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(Prefs.KEY_SCHEDULE_MODE)) {
			//Select different mode to do the schedules.
			String title = null;
			String summary = null;
			switch (Integer.valueOf(newValue.toString())) {
			case 0:
				title = getString(R.string.settings_hungry_mode);
				summary = getString(R.string.settings_hungry_mode_desc);
				break;
			case 1:
				title = getString(R.string.settings_thirsty_mode);
				summary = getString(R.string.settings_thirsty_mode_desc);
				break;
			case 2:
				title = getString(R.string.settings_neutral_mode);
				summary = getString(R.string.settings_neutral_mode_desc);
				break;
			}
			preference.setTitle(title);
			preference.setSummary(summary);
			if(mInitModeList) {
				stopService(new Intent(getApplication(), ScheduleManager.class));
				startService(new Intent(getApplication(), ScheduleManager.class));
			} else {
				 mInitModeList = true;
			}
		} else if (preference.getKey().equals(Prefs.KEY_RUN_BOOT)) {
			//On or Off that begins doing schedules at the boot of device.
			ComponentName receiver = new ComponentName(getApplication(), BootReceiver.class);
			PackageManager pm = getApplication().getPackageManager();

			if (Boolean.valueOf(newValue.toString())) {
				pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			} else {
				pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

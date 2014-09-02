package com.schautup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.ActionBarPreferenceActivity;

import com.schautup.utils.Prefs;

/**
 * Setting .
 */
public final class SettingsActivity extends ActionBarPreferenceActivity implements
		Preference.OnPreferenceChangeListener {


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
		addPreferencesFromResource(R.xml.settings);
		((ListPreference) findPreference(Prefs.KEY_SCHEDULE_MODE)).setOnPreferenceChangeListener(this);
		((ListPreference) findPreference(Prefs.KEY_SCHEDULE_MODE)).setValue(Prefs.getInstance(getApplication())
				.getScheduleMode());
		onPreferenceChange((ListPreference) findPreference(Prefs.KEY_SCHEDULE_MODE), Prefs.getInstance(getApplication())
				.getScheduleMode());
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
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
		return true;
	}
}

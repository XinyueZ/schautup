package com.schautup.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import com.schautup.App;
import com.schautup.BootReceiver;
import com.schautup.R;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;
import com.schautup.utils.uihelper.SystemUiHelper;

/**
 * Setting .
 */
public final class SettingsActivity extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {
	/**
	 * Tricky to avoid first call.
	 */
	private boolean mInitModeList;
	/**
	 * The uiHelper classes from <a href="https://gist.github.com/chrisbanes/73de18faffca571f7292">Chris Banes</a>
	 */
	private SystemUiHelper mSystemUiHelper;
	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mSystemUiHelper.hide();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		mSystemUiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE, 0);
		mSystemUiHelper.hide();
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		mToolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, null, false);
		addContentView(mToolbar, new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mToolbar.setTitle(R.string.menu_settings);
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		String val;
		//Schedule running mode.
		ListPreference modeList = (ListPreference) findPreference(Prefs.KEY_SCHEDULE_MODE);
		modeList.setOnPreferenceChangeListener(this);
		val = Prefs.getInstance(getApplication()).getScheduleMode();
		modeList.setValue(val);
		onPreferenceChange(modeList, val);

		//Sort
		ListPreference sortByEdit = (ListPreference) findPreference(Prefs.KEY_SORTED_BY_EDIT);
		sortByEdit.setOnPreferenceChangeListener(this);
		val = Prefs.getInstance(getApplication()).isSortedByLastEdit();
		sortByEdit.setValue(val);
		onPreferenceChange(sortByEdit, val);


		//Sort direction
		ListPreference sortDirection = (ListPreference) findPreference(Prefs.KEY_SORTED_DIRECTION);
		sortDirection.setOnPreferenceChangeListener(this);
		val = Prefs.getInstance(getApplication()).getSortedDirection();
		sortDirection.setValue(val);
		onPreferenceChange(sortDirection, val);

		//Run at boot or not.
		CheckBoxPreference runBoot = (CheckBoxPreference) findPreference(Prefs.KEY_RUN_BOOT);
		runBoot.setOnPreferenceChangeListener(this);


		((MarginLayoutParams)findViewById(android.R.id.list).getLayoutParams()).topMargin= Utils.getActionBarHeight(
				this);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(Prefs.KEY_SCHEDULE_MODE)) {
			//Select different mode to do the schedules.
			String title = null;
			String summary = null;
			boolean isKitkat = android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
			int newMode = Integer.valueOf(newValue.toString());
			switch (newMode) {
			case 0:
				title = getString(R.string.settings_hungry_mode);
				summary = getString(R.string.settings_hungry_mode_desc);

				preference.setSummary(String.format("%s", summary));
				break;
			case 1:
				title = getString(R.string.settings_thirsty_mode);
				summary = getString(R.string.settings_thirsty_mode_desc);

				preference.setSummary(String.format("%s %s", summary, isKitkat ? getString(
						R.string.settings_current_os) : ""));
				break;
			case 2:
				title = getString(R.string.settings_neutral_mode);
				summary = getString(R.string.settings_neutral_mode_desc);

				preference.setSummary(String.format("%s %s", summary, !isKitkat ? getString(
						R.string.settings_current_os) : ""));
				break;
			}
			preference.setTitle(title);
			if (mInitModeList) {
				App.getInstance().changeMode(newMode);
			} else {
				mInitModeList = true;
			}
		} else if (preference.getKey().equals(Prefs.KEY_SORTED_BY_EDIT)) {
			String title = null;
			switch (Integer.valueOf(newValue.toString())) {
			case 0:
				title = getString(R.string.menu_sort_by_edited_time);
				break;
			case 1:
				title = getString(R.string.menu_sort_by_schedule);
				break;
			}
			preference.setTitle(title);
		} else if (preference.getKey().equals(Prefs.KEY_SORTED_DIRECTION)) {
			int pos = Integer.valueOf(newValue.toString());
			String[] aa = getResources().getStringArray(R.array.settings_sort_direction);
			preference.setTitle(  aa[pos] );
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

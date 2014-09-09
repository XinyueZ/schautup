package com.schautup.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * App's preferences.
 *
 * @author Xinyue Zhao
 */
public final class Prefs {
	/**
	 * Hold on default preference from {@link android.preference.PreferenceManager}.
	 */
	private SharedPreferences mPrefs = null;
	/**
	 * Impl singleton pattern.
	 */
	private static Prefs sInstance;

	//----------------------------------------------------------
	// Description: App's attributes
	//----------------------------------------------------------
	/**
	 * Storage. For last screen view, list or grid view.
	 * <p/>
	 * It should be a boolean, when {@code true}, it is a list-view.
	 */
	private static final String KEY_LAST_VIEW = "key_last_view";
	/**
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key_eula_shown";
	/**
	 * Storage. Whether the tip of "long press to remove a schedule" has been shown before or not.
	 * <p/>
	 * {@code true} if has been shown.
	 */
	private static final String KEY_TIP_LONG_PRESS_RMV_SCHEDULE = "key_long_press_rmv_schedule";
	/**
	 * Storage. Whether the tip of "long press to remove a log" has been shown before or not.
	 * <p/>
	 * {@code true} if has been shown.
	 */
	private static final String KEY_TIP_LONG_PRESS_RMV_LOG = "key_long_press_rmv_log";
	/**
	 * Storage. For schedule modes.
	 */
	public static final String KEY_SCHEDULE_MODE = "key_schedule_mode";
	/**
	 * Storage. Whether to pause or resume schedules.
	 * <p/>
	 * {@code true} if pause.
	 */
	private static final String KEY_PAUSE_RESUME = "key_pause_resume";
	/**
	 * Storage. Weather the application works since boot.
	 */
	public static final String KEY_RUN_BOOT = "key_run_boot";
	//----------------------------------------------------------

	private Prefs(Context cxt) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
	}


	/**
	 * Get instance of  {@link com.schautup.utils.Prefs} singleton.
	 *
	 * @param _context
	 * 		{@link android.app.Application}.
	 *
	 * @return The {@link com.schautup.utils.Prefs} singleton.
	 */
	public static Prefs getInstance(Application _context) {
		if (sInstance == null) {
			sInstance = new Prefs(_context);
		}
		return sInstance;
	}

	//----------------------------------------------------------
	// Description: Getters / Setters of different types.
	//----------------------------------------------------------
	private String getString(String key, String defValue) {
		return mPrefs.getString(key, defValue);
	}


	private boolean setString(String key, String value) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putString(key, value);
		return edit.commit();
	}


	private boolean getBoolean(String key, boolean defValue) {
		return mPrefs.getBoolean(key, defValue);
	}


	private boolean setBoolean(String key, boolean value) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putBoolean(key, value);
		return edit.commit();
	}


	private int getInt(String key, int defValue) {
		return mPrefs.getInt(key, defValue);
	}


	private boolean setInt(String key, int value) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putInt(key, value);
		return edit.commit();
	}


	private long getLong(String key, long defValue) {
		return mPrefs.getLong(key, defValue);
	}


	private boolean setLong(String key, long value) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putLong(key, value);
		return edit.commit();
	}


	private float getFloat(String key, float defValue) {
		return mPrefs.getFloat(key, defValue);
	}


	private boolean setFloat(String key, float value) {
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putFloat(key, value);
		return edit.commit();
	}


	private boolean contains(String key) {
		return mPrefs.contains(key);
	}

	//----------------------------------------------------------

	/**
	 * Is last view before user closes App a list-view?
	 *
	 * @return {@code true} if a list-view, {@link false} is a grid-view.
	 */
	public boolean isLastAListView() {
		return getBoolean(KEY_LAST_VIEW, false);
	}

	/**
	 * Set last view before user closes App a list-view?
	 *
	 * @param isListView
	 * 		{@code true} if a list-view, {@link false} is a grid-view.
	 */
	public void setLastAListView(boolean isListView) {
		setBoolean(KEY_LAST_VIEW, isListView);
	}


	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}

	/**
	 * Get whether the tip of "long press to remove schedule" has been shown before or not.
	 * <p/>
	 *
	 * @return {@code true} if has been shown.
	 */
	public boolean isTipLongPressRmvScheduleShown() {
		return getBoolean(KEY_TIP_LONG_PRESS_RMV_SCHEDULE, false);
	}

	/**
	 * Set whether the tip of "long press to remove schedule" has been shown before or not.
	 * <p/>
	 *
	 * @param isShown
	 * 		{@code true} if has been shown.
	 */
	public void setTipLongPressRmvScheduleShown(boolean isShown) {
		setBoolean(KEY_TIP_LONG_PRESS_RMV_SCHEDULE, isShown);
	}

	/**
	 * Get whether the tip of "long press to remove log" has been shown before or not.
	 * <p/>
	 *
	 * @return {@code true} if has been shown.
	 */
	public boolean isTipLongPressRmvLogHistoryShown() {
		return getBoolean(KEY_TIP_LONG_PRESS_RMV_LOG, false);
	}

	/**
	 * Set whether the tip of "long press to remove log" has been shown before or not.
	 * <p/>
	 *
	 * @param isShown
	 * 		{@code true} if has been shown.
	 */
	public void setTipLongPressRmvLogHistoryShown(boolean isShown) {
		setBoolean(KEY_TIP_LONG_PRESS_RMV_LOG, isShown);
	}


	/**
	 * Set current schedule mode.
	 *
	 * @param modeValue
	 * 		The mode value.
	 */
	public void setScheduleMode(String modeValue) {
		setString(KEY_SCHEDULE_MODE, modeValue);
	}

	/**
	 * Get current schedule mode, default is "1" the "thirsty" mode.
	 *
	 * @return The current schedule mode.
	 */
	public String getScheduleMode() {
		return getString(KEY_SCHEDULE_MODE, "1");
	}

	/**
	 * {@code true} if pause the schedules.
	 *
	 * @return {@code true} if pause the schedules. <b>Default is {@code false}.</b>
	 */
	public boolean isPause() {
		return getBoolean(KEY_PAUSE_RESUME, false);
	}

	/**
	 * Set storage. Whether to pause or resume schedules.
	 * <p/>
	 *
	 * @param pause
	 * 		{@code true} if pause.
	 */
	public void setPause(boolean pause) {
		setBoolean(KEY_PAUSE_RESUME, pause);
	}

	/**
	 * {@coee true}. Receiver for boot is available.
	 *
	 * @return {@code true}. Receiver for boot is available.
	 */
	public boolean isRunBoot() {
		return getBoolean(KEY_RUN_BOOT, true);
	}

	/**
	 * To allow receiver for boot to be available.
	 *
	 * @param runBoot
	 * 		{@code true}. Receiver for boot is available.
	 */
	public void setRunBoot(Boolean runBoot) {

	}
}

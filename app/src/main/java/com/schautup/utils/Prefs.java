package com.schautup.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION_CODES;

import com.chopping.application.BasicPrefs;

/**
 * App's preferences.
 *
 * @author Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/**
	 * Impl singleton pattern.
	 */
	private static Prefs sInstance;
	/**
	 * Sort direction ascending.
	 */
	public static final String ASCENDING = "ASC";
	/**
	 * Sort direction descending.
	 */
	public static final String DESCENDING = "DESC";
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
	 * Storage. Whether the application works since boot.
	 */
	public static final String KEY_RUN_BOOT = "key_run_boot";
	/**
	 * Url to the web-site of the app.
	 */
	private static final String APP_WEB_HOME = "app_web_home";
	/**
	 * Storage. Whether the list sort type is by last edit or not. {@code true} if sorted by last edit.
	 */
	public static final String KEY_SORTED_BY_EDIT = "key_sorted_by_edit";
	/**
	 * Storage. Sort direction: descending or ascending.
	 */
	public static final String KEY_SORTED_DIRECTION = "key_sorted_direction";
	/**
	 * Storage. Total and max for subitems on drawer, labels, filters define.
	 */
	private static final String KEY_MAX_SUBITEMS = "key_max_subitems";
	//----------------------------------------------------------


	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
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
	 * Get current schedule mode, default is "1" the "thirsty" mode.
	 *
	 * @return The current schedule mode.
	 */
	public String getScheduleMode() {
		return getString(KEY_SCHEDULE_MODE, android.os.Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT ? "1" : "2");
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
	 * Get url to the web-site of the app.
	 *
	 * @return Url to home.
	 */
	public String getAppWebHome() {
		return getString(APP_WEB_HOME, null);
	}

	/**
	 * Get whether the list sort type is by last edit or not. {@code true} if sorted by last edit.
	 */
	public String isSortedByLastEdit() {
		return getString(KEY_SORTED_BY_EDIT, "0");
	}


	/**
	 * Get total and max for subitems on drawer, labels, filters define.
	 *
	 * @return subitems on drawer, labels, filters define.
	 */
	public int getMaxSubItems() {
		return getInt(KEY_MAX_SUBITEMS, 5);
	}



	/**
	 * Get sort direction: descending or ascending.
	 * @return 0 or 1 ({@link #DESCENDING}, {@link #ASCENDING}).
	 */
	public String getSortedDirection() {
		return getString(KEY_SORTED_DIRECTION, "0" );
	}
}

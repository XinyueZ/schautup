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
	private static final String KEY_LAST_VIEW = "key.last.view";

	//----------------------------------------------------------

	private Prefs(Context cxt) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
	}


	/**
	 * Get instance of  {@link com.schautup.utils.Prefs} singleton.
	 *
	 * @param _context
	 * 		{@link android.app.Application}.
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
		return getBoolean(KEY_LAST_VIEW, true);
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
}

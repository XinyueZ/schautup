package com.schautup.data;

import com.schautup.R;

/**
 * Defines max, min, medium levels for some device properties.
 *
 * @author Xinyue Zhao
 */
public enum Level {
	MAX(1, R.string.lbl_max, R.string.lbl_max_short),
	MEDIUM(0, R.string.lbl_medium, R.string.lbl_medium_short),
	MIN(-1, R.string.lbl_min, R.string.lbl_min_short);

	/**
	 * Level in integer format.
	 */
	private int mLevel;


	/**
	 * Level in {@link java.lang.String} format - {@link android.support.annotation.StringRes}.
	 */
	private int mLevelResId;
	/**
	 * Level in {@link java.lang.String} short format - {@link android.support.annotation.StringRes}.
	 */
	private int mLevelShortResId;

	/**
	 * Constructor of {@link com.schautup.data.Level}.
	 *
	 * @param level
	 * @param levelResId
	 * @param levelShortResId
	 */
	Level(int level, int levelResId, int levelShortResId) {
		mLevel = level;
		mLevelResId = levelResId;
		mLevelShortResId = levelShortResId;
	}

	/**
	 * Get a {@link com.schautup.data.Level} from integer.
	 *
	 * @param code
	 * 		The max(1), medium(0), min(-1).
	 *
	 * @return A {@link com.schautup.data.Level}.
	 */
	public static Level fromInt(int code) {
		switch (code) {
		case 1:
			return MAX;
		case 0:
			return MEDIUM;
		case -1:
			return MIN;
		default:
			return null;
		}
	}

	/**
	 * Convert to integer format: max(1), medium(0), min(-1).
	 *
	 * @return Integer format of {@link com.schautup.data.Level}.
	 */
	public int toCode() {
		return mLevel;
	}

	/**
	 * Level in {@link java.lang.String} format - {@link android.support.annotation.StringRes}.
	 */
	public int getLevelResId() {
		return mLevelResId;
	}

	/**
	 * Level in {@link java.lang.String} short format - {@link android.support.annotation.StringRes}.
	 */
	public int getLevelShortResId() {
		return mLevelShortResId;
	}
}

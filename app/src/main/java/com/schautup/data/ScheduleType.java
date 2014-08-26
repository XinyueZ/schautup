package com.schautup.data;

import com.schautup.R;

/**
 * Enum Mute, Vibrate, Sound.
 *
 * @author Xinyue Zhao
 */
public enum ScheduleType {
	MUTE(0, R.drawable.ic_mute), VIBRATE(1, R.drawable.ic_vibrate), SOUND(2, R.drawable.ic_sound);

	/**
	 * Code of type.
	 */
	private int mCode;
	/**
	 * ResId of status icon.
	 */
	private int mIconDrawResId;

	/**
	 * Constructor of ScheduleType.
	 *
	 * @param _iconDrawResId
	 * 		The resId of status icon.
	 */
	ScheduleType(int _code, int _iconDrawResId) {
		mCode = _code;
		mIconDrawResId = _iconDrawResId;
	}

	/**
	 * Get resId of status icon.
	 *
	 * @return resId of status icon.
	 */
	public int getIconDrawResId() {
		return mIconDrawResId;
	}

	/**
	 * Get code of type.
	 */
	public int getCode() {
		return mCode;
	}

	/**
	 * Convert code to {@link com.schautup.data.ScheduleType}. It should return {@code null} when a unknown code is
	 * passed.
	 *
	 * @param code
	 * 		The code represent a {@link com.schautup.data.ScheduleType}.
	 * @return {@link com.schautup.data.ScheduleType}, <b>{@code null} when a unknown code.</b>
	 */
	public static ScheduleType fromCode(int code) {
		switch (code) {
		case 0:
			return MUTE;
		case 1:
			return VIBRATE;
		case 2:
			return SOUND;
		default:
			return null;
		}
	}

	/**
	 * Get code of type.
	 * @return code.
	 */
	public int toCode() {
		return mCode;
	}
}

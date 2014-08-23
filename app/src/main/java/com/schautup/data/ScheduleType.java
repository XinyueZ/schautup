package com.schautup.data;

import com.schautup.R;

/**
 * Enum Mute, Vibrate, Sound.
 *
 * @author Xinyue Zhao
 */
public enum ScheduleType {
	MUTE(R.drawable.ic_mute), VIBRATE(R.drawable.ic_vibrate), SOUND(R.drawable.ic_sound);
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
	ScheduleType(int _iconDrawResId) {
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
}

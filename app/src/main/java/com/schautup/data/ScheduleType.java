package com.schautup.data;

import com.schautup.R;

/**
 * Enum Mute, Vibrate, Sound.
 *
 * @author Xinyue Zhao
 */
public enum ScheduleType {
	MUTE(0, R.drawable.ic_mute, R.string.type_mute), VIBRATE(1, R.drawable.ic_vibrate, R.string.type_vibrate), SOUND(2,
			R.drawable.ic_sound, R.string.type_sound), WIFI(3, R.drawable.ic_wifi_btn, R.string.type_wifi),
	MOBILE(4, R.drawable.ic_mobile_data_btn, R.string.type_mobile_data), BRIGHTNESS(5, R.drawable.ic_brightness_btn,
			R.string.type_brightness), BLUETOOTH(6, R.drawable.ic_bluetooth_btn, R.string.type_bluetooth), STARTAPP(7,
			R.drawable.ic_app_list_btn, R.string.type_app_start), CALLABORT(8, R.drawable.ic_dail_abort_btn,
			R.string.type_call_abort);

	/**
	 * Code of type.
	 */
	private int mCode;
	/**
	 * ResId of status icon.
	 */
	private int mIconDrawResId;
	/**
	 * ResId of the name;
	 */
	private int mNameResId;

	/**
	 * Constructor of ScheduleType.
	 *
	 * @param code
	 * 		Code.
	 * @param iconDrawResId
	 * 		The resId of status icon.
	 * @param nameResId
	 * 		The resId of name.
	 */
	ScheduleType(int code, int iconDrawResId, int nameResId) {
		mCode = code;
		mIconDrawResId = iconDrawResId;
		mNameResId = nameResId;
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
	 *
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
		case 3:
			return WIFI;
		case 4:
			return MOBILE;
		case 5:
			return BRIGHTNESS;
		case 6:
			return BLUETOOTH;
		case 7:
			return STARTAPP;
		case 8:
			return CALLABORT;
		default:
			return null;
		}
	}

	/**
	 * Get code of type.
	 *
	 * @return code.
	 */
	public int toCode() {
		return mCode;
	}

	/**
	 * Get name of type.
	 *
	 * @return {@link android.support.annotation.StringRes} to the name of type.
	 */
	public int getNameResId() {
		return mNameResId;
	}
}

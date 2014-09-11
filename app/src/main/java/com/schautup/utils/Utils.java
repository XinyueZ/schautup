package com.schautup.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.views.BadgeView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;
import static android.text.format.DateUtils.FORMAT_SHOW_YEAR;
import static android.text.format.DateUtils.formatDateTime;

/**
 * Util tool.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	public static final String ALPHA =
			(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ? "alpha" : "Alpha";

	/**
	 * Convert value.
	 * <p/>
	 * For example:
	 * <p/>
	 * value := 1 return 01
	 * <p/>
	 * value := 12 return 12
	 *
	 * @param value
	 */
	public static String convertValue(int value) {
		String fmt = "%s";
		NumberFormat fmtNum = new DecimalFormat("##00");
		String ret = String.format(fmt, fmtNum.format(value));
		return ret;
	}

	/**
	 * Convert time in {@link java.lang.String} from a {@link com.schautup.data.ScheduleItem}.
	 * <p/>
	 * For example:
	 * <p/>
	 * value := 1 return 01
	 * <p/>
	 * value := 12 return 12
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem}.
	 */
	public static String convertValue(ScheduleItem item) {
		String fmt = "%s:%s";
		NumberFormat fmtNum = new DecimalFormat("##00");
		String ret = String.format(fmt, fmtNum.format(item.getHour()), fmtNum.format(item.getMinute()));
		return ret;
	}

	/**
	 * Get different {@link android.support.annotation.ColorRes} for different schedule status.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem}.
	 *
	 * @return A {@link android.support.annotation.ColorRes}.
	 */
	public static int getStatusLevelColor(Context cxt, ScheduleItem item) {
		int colorRes = R.color.level_0;
		int[] levels = { R.color.level_11, R.color.level_10, R.color.level_9, R.color.level_8, R.color.level_7,
				R.color.level_6, R.color.level_5, R.color.level_4, R.color.level_3, R.color.level_2, R.color.level_1,
				R.color.level_0 };
		Resources resources = cxt.getResources();
		DateTime now = DateTime.now();
		int hourToWait = item.getHour();
		if (hourToWait >= now.getHourOfDay()) {
			int diff = hourToWait - now.getHourOfDay();
			if (diff >= 0 && diff <= 11) {
				if (diff < 1) {
					colorRes = R.color.level_11;
				} else {
					colorRes = levels[diff - 1];
				}
			} else {
				colorRes = R.color.level_0;
			}
		} else {
			colorRes = R.color.level_0;
		}
		return resources.getColor(colorRes);
	}

	/**
	 * Standard sharing app for sharing on actionbar.
	 */
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider provider, String subject,
			String body) {
		if (provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
			provider.setShareIntent(i);
			return i;
		}
		return null;
	}


	/**
	 * Show long time toast.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param messageId
	 * 		{@link android.support.annotation.StringRes}. Message to show.
	 */
	public static void showLongToast(Context context, @StringRes int messageId) {
		Toast.makeText(context, context.getString(messageId), Toast.LENGTH_LONG).show();
	}

	/**
	 * A compatible method for setting {@link android.graphics.drawable.Drawable} object on the {@link
	 * android.view.View}.
	 * <p/>
	 * Inspired by <a href="http://stackoverflow .com/questions/11947603/setbackground-vs-setbackgrounddrawable-android">StackOverflow</a>.
	 *
	 * @param v
	 * 		The {@link android.view.View} that needs background.
	 * @param drawable
	 * 		{@link android.support.annotation.DrawableRes} for a {@link android.graphics.drawable.Drawable}.
	 */
	@SuppressLint("NewApi")
	public static void setBackgroundCompat(View v, @DrawableRes Drawable drawable) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackgroundDrawable(drawable);
		} else {
			v.setBackground(drawable);
		}
	}

	/**
	 * Show short time toast.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param messageId
	 * 		{@link android.support.annotation.StringRes}. Message to show.
	 */
	public static void showShortToast(Context context, @StringRes int messageId) {
		Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Show short time toast.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param message
	 * 		{@link java.lang.String}. Message to show.
	 */
	public static void showLongToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Show short time toast.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param message
	 * 		{@link java.lang.String}. Message to show.
	 */
	public static void showShortToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Converts one of the internal day constants (SU, MO, etc.) to the two-letter string representing that constant.
	 *
	 * @param day
	 * 		one the internal constants SU, MO, etc.
	 *
	 * @return the two-letter string for the day ("SU", "MO", etc.)
	 *
	 * @throws IllegalArgumentException
	 * 		Thrown if the day argument is not one of the defined day constants.
	 */
	public static String day2String(int day) {
		switch (day) {
		case DateTimeConstants.SUNDAY:
			return "SU";
		case DateTimeConstants.MONDAY:
			return "MO";
		case DateTimeConstants.TUESDAY:
			return "TU";
		case DateTimeConstants.WEDNESDAY:
			return "WE";
		case DateTimeConstants.THURSDAY:
			return "TH";
		case DateTimeConstants.FRIDAY:
			return "FR";
		case DateTimeConstants.SATURDAY:
			return "SA";
		default:
			throw new IllegalArgumentException("bad day argument: " + day);
		}
	}


	/**
	 * Get {@link ScreenSize} with different {@code displayIndex} .
	 *
	 * @param cxt
	 * 		{@link android.content.Context} .
	 * @param displayIndex
	 * 		The index of display.
	 *
	 * @return A {@link ScreenSize}.
	 */
	public static ScreenSize getScreenSize(Context cxt, int displayIndex) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display[] displays = DisplayManagerCompat.getInstance(cxt).getDisplays();
		Display display = displays[displayIndex];
		display.getMetrics(displaymetrics);
		return new ScreenSize(displaymetrics.widthPixels, displaymetrics.heightPixels);
	}

	/**
	 * Convert a timestamps to a readable date in string.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param timestamps
	 * 		A long value for a timestamps.
	 *
	 * @return A date string format.
	 */
	public static String convertTimestamps2dateString(Context cxt, long timestamps) {
		return formatDateTime(cxt, timestamps, FORMAT_SHOW_YEAR | FORMAT_SHOW_DATE |
				FORMAT_SHOW_TIME | FORMAT_ABBREV_MONTH);
	}

	/**
	 * Turn on/off the mobile data.
	 * <p/>
	 * <b>Unofficial implementation.</b>
	 * <p/>
	 * See. <a href="http://stackoverflow.com/questions/12535101/how-can-i-turn-off-3g-data-programmatically-on
	 * -android">StackOverflow</a>
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param enabled
	 * 		{@code true} Turn on, {@code false} turn off.
	 *
	 * @return {@code true} if change is success.
	 */
	public static boolean setMobileDataEnabled(Context context, boolean enabled) {
		boolean success = false;
		try {
			final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(
					Context.CONNECTIVITY_SERVICE);
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod(
					"setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
			success = true;
		} catch (Exception ex) {
			LL.w(ex.toString());
			success = false;
		}
		return success;
	}

	/**
	 * Turn on/off the wifi. Call {@link android.net.wifi.WifiManager#setWifiEnabled(boolean)} directly.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param enabled
	 * 		{@code true} Turn on, {@code false} turn off.
	 *
	 * @return {@code true} if change is success.
	 */
	public static boolean setWifiEnabled(Context context, boolean enabled) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.setWifiEnabled(enabled);
	}

	/**
	 * Set different ring mode. Call {@link android.media.AudioManager#setRingerMode(int)}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param mode
	 * 		Different mode:<li>{@link android.media.AudioManager#RINGER_MODE_SILENT} for {@link
	 * 		com.schautup.data.ScheduleType#MUTE}</li> <li>{@link android.media.AudioManager#RINGER_MODE_VIBRATE} for {@link
	 * 		com.schautup.data.ScheduleType#VIBRATE}</li><li>{@link android.media.AudioManager#RINGER_MODE_NORMAL} for
	 * 		{@link com.schautup.data.ScheduleType#SOUND}</li>
	 */
	public static void setRingMode(Context cxt, int mode) {
		AudioManager audioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setRingerMode(mode);
	}

	/**
	 * Screen-size in pixels.
	 */
	public static class ScreenSize {
		public int Width;
		public int Height;

		public ScreenSize(int _width, int _height) {
			Width = _width;
			Height = _height;
		}
	}

	/**
	 * Helper method that shows and animates text on a {@link com.schautup.views.BadgeView}.
	 *
	 * @param context
	 * 		{@link android.content.Context}.
	 * @param view
	 * 		The target {@link com.schautup.views.BadgeView}.
	 * @param text
	 * 		The info to show.
	 */
	public static void showBadgeView(Context context, BadgeView view, String text) {
		view.setVisibility(View.VISIBLE);
		view.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
		view.setBadgeMargin(5);
		view.setText(text);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		view.show(AnimationUtils.loadAnimation(context, R.anim.scale_in));
	}

	/**
	 * Helper method that converts true/false to on/off in text.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param bool
	 * 		The boolean value.
	 *
	 * @return The on/off in text.
	 */
	public static String convertBooleanToOnOff(Context cxt, boolean bool) {
		return cxt.getString(bool ? R.string.lbl_on_small : R.string.lbl_off_small);
	}

	/**
	 * Util method that fetch the string from {@link java.lang.Object}'s {@link Object#toString()}.
	 * <p/>
	 * Convenient way to ignore null when {@code boolObj} is null.
	 * @param boolObj A object that wants to get string.
	 * @return null if {@code booObj} is null, not null when it is not null and calls {@link Object#toString()}.
	 */
	public static String toString(Object boolObj) {
		return boolObj == null ? null : boolObj.toString();
	}
}

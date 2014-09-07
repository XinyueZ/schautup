package com.schautup.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import com.schautup.R;
import com.schautup.data.ScheduleItem;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

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
	 * @param day one the internal constants SU, MO, etc.
	 * @return the two-letter string for the day ("SU", "MO", etc.)
	 * @throws IllegalArgumentException Thrown if the day argument is not one of the defined day constants.
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
}

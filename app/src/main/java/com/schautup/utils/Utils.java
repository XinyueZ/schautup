package com.schautup.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.schautup.R;
import com.schautup.data.ScheduleItem;

import org.joda.time.DateTime;

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
}

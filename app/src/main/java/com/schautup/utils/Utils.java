package com.schautup.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
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
	 * Compare with {@link org.joda.time.DateTimeConstants}. Converts day in week to two-letter string .
	 *
	 * @param day
	 * 		Day in week.
	 *
	 * @return the two-letter string for the day ("SU", "MO", etc.)
	 *
	 * @throws IllegalArgumentException
	 * 		Thrown if the day argument is not one of the defined day constants.
	 */
	public static String dateTimeDay2String(int day) {
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
	 * Compare with {@link com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence}. Converts day in week to
	 * local text .
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 * @param day
	 * 		Day in week.
	 *
	 * @return The day in week in local text.
	 *
	 * @throws IllegalArgumentException
	 * 		Thrown if the day argument is not one of the defined day constants.
	 */
	public static String recurrenceDay2String(Context cxt, int day) {
		switch (day) {
		case EventRecurrence.SU:
			return cxt.getString(R.string.lbl_su);
		case EventRecurrence.MO:
			return cxt.getString(R.string.lbl_mo);
		case EventRecurrence.TU:
			return cxt.getString(R.string.lbl_tu);
		case EventRecurrence.WE:
			return cxt.getString(R.string.lbl_we);
		case EventRecurrence.TH:
			return cxt.getString(R.string.lbl_th);
		case EventRecurrence.FR:
			return cxt.getString(R.string.lbl_fr);
		case EventRecurrence.SA:
			return cxt.getString(R.string.lbl_sa);
		default:
			throw new IllegalArgumentException("bad day argument: " + day);
		}
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
	public static String convertTimestamps2DateString(Context cxt, long timestamps) {
		return formatDateTime(cxt, timestamps, FORMAT_SHOW_YEAR | FORMAT_SHOW_DATE |
				FORMAT_SHOW_TIME | FORMAT_ABBREV_MONTH);
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
	 *
	 * @param boolObj
	 * 		A object that wants to get string.
	 *
	 * @return null if {@code booObj} is null, not null when it is not null and calls {@link Object#toString()}.
	 */
	public static String toString(Object boolObj) {
		return boolObj == null ? null : boolObj.toString();
	}

	/**
	 * Util method to fetch all scheduled items from DB according to the sorting type.
	 *
	 * @param app
	 * 		{@link android.app.Application}.
	 *
	 * @return The {@link java.util.List} of {@link com.schautup.data.ScheduleItem}.
	 */
	public static List<ScheduleItem> getAllSchedules(Application app) {
		if (Prefs.getInstance(app).isSortedByLastEdit().equals("0")) {
			return DB.getInstance(app).getAllSchedulesOrderByEditTime();
		} else {
			return DB.getInstance(app).getAllSchedulesOrderByScheduleTime();
		}
	}

	/**
	 * Show information about pre-selected recurrence.
	 *
	 * @param activity
	 * @param eventRecurrence
	 * @param bgv
	 */
	public static void showRecurrenceBadge(Activity activity, EventRecurrence eventRecurrence, BadgeView bgv) {
		if (eventRecurrence != null && eventRecurrence.byday != null) {
			if (eventRecurrence.bydayCount > 1) {
				showBadgeView(activity, bgv, "..");
			} else {
				int sel = eventRecurrence.byday[0];
				showBadgeView(activity, bgv, recurrenceDay2String(activity, sel));
			}
		} else {
			String todayInWeek = dateTimeDay2String(DateTime.now().getDayOfWeek());
			eventRecurrence = new EventRecurrence();
			eventRecurrence.parse("FREQ=WEEKLY;WKST=SU;BYDAY=" + todayInWeek);
			int sel = eventRecurrence.byday[0];
			showBadgeView(activity, bgv, recurrenceDay2String(activity, sel));
		}
	}
}

package com.schautup.data;

import android.support.v4.util.SparseArrayCompat;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;

/**
 * A filter.
 *
 * @author Xinyue Zhao
 */
public final class Filter {
	/**
	 * Id in database.
	 */
	private long mId = -1;
	/**
	 * Name of filter.
	 */
	private String mName;
	/**
	 * Selected hour.
	 */
	private int mHour;
	/**
	 * Selected minute.
	 */
	private int mMinute;
	/**
	 * The recurrence defined in filter.
	 */
	private EventRecurrence mEventRecurrence;
	/**
	 * Selected types.
	 */
	private SparseArrayCompat<ScheduleType> mSelectedTypes = new SparseArrayCompat<ScheduleType>();


	/**
	 * Labels are special filters that have not been fired to change to a schedule.
	 * <p/>
	 * {@code true} if the filter has not been a real "filter". A label has been created, that means the label has not
	 * been fired and added as a schedule.
	 * <p/>
	 * {@code false} default that a filter has been created directly.
	 */
	private boolean mLabel = false;
	/**
	 * Edited time.
	 */
	private long mEditedTime;

	/**
	 * Constructor of {@link Filter}.
	 *
	 * @param id
	 * 		Id in database.
	 * @param name
	 * 		Name of filter.
	 * @param hour
	 * 		Selected hour.
	 * @param minute
	 * 		Selected minute.
	 * @param eventRecurrence
	 * 		The recurrence defined in filter.
	 * @param editTime
	 * 		Edited time in {@link com.schautup.db.DB}.
	 */
	public Filter(long id, String name, int hour, int minute, EventRecurrence eventRecurrence, long editTime) {
		mId = id;
		mName = name;
		mHour = hour;
		mMinute = minute;
		mEventRecurrence = eventRecurrence;
		mEditedTime = editTime;
	}

	/**
	 * Constructor of {@link Filter}.
	 * @param name
	 *      Name of filter.
	 * @param hour
	 * 		Selected hour.
	 * @param minute
	 * 		Selected minute.
	 * @param eventRecurrence
	 * 		The recurrence defined in filter.
	 */
	//	public Filter(String name, int hour, int minute, EventRecurrence eventRecurrence) {
	//		this(-1, name, hour, minute, eventRecurrence, System.currentTimeMillis());
	//	}

	/**
	 * Get id in database.
	 */
	public long getId() {
		return mId;
	}

	/**
	 * Get name of filter.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Get selected hour.
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * Get selected minute.
	 */
	public int getMinute() {
		return mMinute;
	}

	/**
	 * Get the recurrence defined in filter.
	 */
	public EventRecurrence getEventRecurrence() {
		return mEventRecurrence;
	}

	/**
	 * Get last edit time.
	 * @return time in long.
	 */
	public long getEditedTime() {
		return mEditedTime;
	}

	/**
	 * Get selected types.
	 *
	 * @return Selected types.
	 */
	public SparseArrayCompat<ScheduleType> getSelectedTypes() {
		return mSelectedTypes;
	}

	/**
	 * Default constructor of {@link com.schautup.data.Filter}.
	 */
	public Filter() {
		mEditedTime = System.currentTimeMillis();
	}

	/**
	 * Clone value from {@code filterToClone}.
	 * @param filterToClone {@link com.schautup.data.Filter} that provides data for {@code this} {@link com.schautup.data.Filter}.
	 */
	public void clone(Filter filterToClone) {
		this.setId(filterToClone.getId());
		this.setName(filterToClone.getName());
		this.setHour(filterToClone.getHour());
		this.setMinute(filterToClone.getMinute());
		this.setEventRecurrence(filterToClone.getEventRecurrence());
		this.setSelectedTypes(filterToClone.getSelectedTypes());
		this.setEditedTime(filterToClone.getEditedTime());
	}

	/**
	 * Set id when from {@link com.schautup.db.DB}.
	 * @param id Location of {@link com.schautup.data.Filter} in {@link com.schautup.db.DB}.
	 */
	public void setId(long id) {
		mId = id;
	}

	public void setName(String name) {
		mName = name;
	}

	public void setHour(int hour) {
		mHour = hour;
	}

	public void setMinute(int minute) {
		mMinute = minute;
	}

	public void setEventRecurrence(EventRecurrence eventRecurrence) {
		mEventRecurrence = eventRecurrence;
	}

	public void setSelectedTypes(SparseArrayCompat<ScheduleType> selectedTypes) {
		mSelectedTypes = selectedTypes;
	}

	public void setEditedTime(long editedTime) {
		mEditedTime = editedTime;
	}

	@Override
	public String toString() {
		return getName();
	}


	/**
	 * Labels are special filters that have not been fired to change to a schedule.
	 * <p/>
	 * {@code true} if the filter has not been a real "filter". A label has been created, that means the label has not
	 * been fired and added as a schedule.
	 * <p/>
	 * {@code false} default that a filter has been created directly.
	 *
	 * @return {@code true}: a label not a filter.
	 */
	public boolean isLabel() {
		return mLabel;
	}

	/**
	 * Set {@code true} if the filter has not been a real "filter". A label has been created, that means the label has not
	 * been fired and added as a schedule.
	 * <p/>
	 * {@code false} default that a filter has been created directly.
	 *
	 * @param label  {@code true}: a label not a filter.
	 */
	public void setLabel(boolean label) {
		mLabel = label;
	}
}

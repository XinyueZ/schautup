package com.schautup.adapters;

import java.util.List;

import android.widget.BaseAdapter;

import com.schautup.data.ScheduleItem;

/**
 * Abstract impl {@link android.widget.BaseAdapter} for all {@link android.widget.ListView}, {@link
 * android.widget.GridView}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseScheduleAdapter extends BaseAdapter {
	/**
	 * Data source.
	 */
	private List<ScheduleItem> mItemList;

	/**
	 * Set data source, list of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _itemList
	 * 		The list of {@link com.schautup.data.ScheduleItem}.
	 */
	public void setItemList(List<ScheduleItem> _itemList) {
		mItemList = _itemList;
	}

	/**
	 * Get data source, list of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @return The list of {@link com.schautup.data.ScheduleItem}.
	 */
	protected List<ScheduleItem> getItemList() {
		return mItemList;
	}
}

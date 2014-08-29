package com.schautup.adapters;

import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.utils.Utils;

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
	 * The item that duplicates a current already shown item in {@link #mItemList}.
	 */
	private ScheduleItem mDuplicatedItem;

	/**
	 * Get data source, list of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @return The list of {@link com.schautup.data.ScheduleItem}.
	 */
	protected List<ScheduleItem> getItemList() {
		return mItemList;
	}

	/**
	 * Set data source, list of {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param _itemList
	 * 		The list of {@link com.schautup.data.ScheduleItem}.
	 */
	public void setItemList(List<ScheduleItem> _itemList) {
		mItemList = _itemList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder h;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
			h = createViewHolder(convertView);
			convertView.setTag(h);
		} else {
			h = (ViewHolder) convertView.getTag();
		}
		ScheduleItem item = getItemList().get(position);
		h.mStatusLevelV.setBackgroundColor(Utils.getStatusLevelColor(parent.getContext(), item));
		h.mStatusIv.setImageResource(item.getType().getIconDrawResId());
		h.mStatusTv.setText(Utils.convertValue(item));
		if(mDuplicatedItem != null && item.getId() == mDuplicatedItem.getId() ) {
			convertView.setBackgroundResource(R.drawable.anim_list_warning);
			((AnimationDrawable) convertView.getBackground()).start();
		} else {
			convertView.setBackgroundResource(R.color.bg_list_item);
		}
		return convertView;
	}

	/**
	 * Get {@link android.support.annotation.LayoutRes} of list.
	 *
	 * @return {@link android.support.annotation.LayoutRes}
	 */
	protected abstract int getLayoutId();

	/**
	 * Create a  {@link com.schautup.adapters.BaseScheduleAdapter.ViewHolder} object variant.
	 *
	 * @param convertView
	 * 		The root {@link android.view.View} of item.
	 * @return A {@link com.schautup.adapters.BaseScheduleAdapter.ViewHolder} object.
	 */
	protected abstract ViewHolder createViewHolder(View convertView);

	/**
	 * Get the index of the item whose "id" equals to {@code item} in the data-list.
	 *
	 * @param item
	 * 		The item to search.
	 * @return The index(position) of the item. If not found <b>return -1</b>
	 */
	public int getItemPosition(ScheduleItem item) {
		int index = -1;
		int pos = 0;
		for (ScheduleItem i : mItemList) {
			if (i.getId() == item.getId()) {
				index = pos;
				break;
			}
			pos++;
		}
		return index;
	}

	/**
	 * Show a warning on the already shown item that might be duplicated by {@code item}.
	 *
	 * @param item
	 * 		The item that might be inserted into DB or update in DB but it was rejected.
	 */
	public void showWarningOnItem(ScheduleItem item) {
		mDuplicatedItem = item;
		notifyDataSetChanged();
	}

	/**
	 * ViewHolder patter for {@link com.schautup.R.layout#item_schedule_grid}.
	 */
	protected static class ViewHolder {
		private View mStatusLevelV;
		private ImageView mStatusIv;
		private TextView mStatusTv;

		protected ViewHolder(View convertView) {
			mStatusLevelV = convertView.findViewById(R.id.status_level_v);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
		}
	}
}

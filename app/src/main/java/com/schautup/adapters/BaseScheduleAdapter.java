package com.schautup.adapters;

import java.util.LinkedList;
import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
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
	 * {@link android.support.annotation.DrawableRes}, animation-list when an item needs show warning.
	 */
	private Drawable mWarningDrawable;

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
	 * <p/>
	 * It's better to pass a  {@link java.util.LinkedList}.
	 *
	 * @param _itemList
	 * 		The list of {@link com.schautup.data.ScheduleItem}.
	 */
	public void setItemList(List<ScheduleItem> _itemList) {
		mItemList = _itemList;
	}


	@Override
	public int getCount() {
		return mItemList == null ? 0 : mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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
		if (mWarningDrawable != null && mDuplicatedItem != null && item.getId() == mDuplicatedItem.getId()) {
			Utils.setBackgroundCompat(convertView, mWarningDrawable);
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
	 * @return The index(position) of the item. If not found <b>return -1</b>.
	 */
	public int getItemPosition(ScheduleItem item) {
		if (mItemList == null) {
			return -1;
		}
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
	 * Get the object of {@link com.schautup.data.ScheduleItem} whose "id" equals to {@code item} in the data-list.
	 *
	 * @param item
	 * 		The item to search.
	 * @return The object of the item. If not found <b>return null</b>.
	 */
	public ScheduleItem findItem(ScheduleItem item) {
		if (mItemList == null) {
			return null;
		}
		ScheduleItem ret = null;
		for (ScheduleItem i : mItemList) {
			if (i.getId() == item.getId()) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * Add item into cached data of this {@link android.widget.Adapter}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 * <p/>
	 * It will also create an internal {@link java.util.LinkedList} when there's no cache {@link java.util.List}
	 * initialized.
	 *
	 * @param item
	 * 		The item to add.
	 */
	public void addItem(ScheduleItem item) {
		if (mItemList == null) {
			mItemList = new LinkedList<ScheduleItem>();
		}
		mItemList.add(item);
		notifyDataSetChanged();
	}

	/**
	 * Edit a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param itemFound
	 * 		The item that has been cached.
	 * @param newItem
	 * 		The item to edit.
	 */
	public void editItem(ScheduleItem itemFound, ScheduleItem newItem) {
		itemFound.setId(newItem.getId());
		itemFound.setType(newItem.getType());
		itemFound.setHour(newItem.getHour());
		itemFound.setMinute(newItem.getMinute());
		itemFound.setEditedTime(newItem.getEditedTime());
		notifyDataSetChanged();
	}

	/**
	 * Show a warning on the already shown item that might be duplicated by {@code item}.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param item
	 * 		The item that might be inserted into DB or update in DB but it was rejected.
	 * @param warningDrawable
	 * 		The animation-list when an item needs show warning.
	 */
	public void showWarningOnItem(ScheduleItem item, @DrawableRes Drawable warningDrawable) {
		mDuplicatedItem = item;
		mWarningDrawable = warningDrawable;
		notifyDataSetChanged();
	}

	/**
	 * Clear warning animation if possible.
	 */
	public void clearWarning() {
		if (mDuplicatedItem != null) {
			mDuplicatedItem = null;
			mWarningDrawable = null;
			notifyDataSetChanged();
		}
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

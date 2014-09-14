package com.schautup.adapters;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.R;
import com.schautup.data.ScheduleItem;
import com.schautup.utils.Utils;
import com.schautup.views.BadgeView;

/**
 * Abstract impl {@link android.widget.BaseAdapter} for all {@link android.widget.ListView}, {@link
 * android.widget.GridView}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseScheduleListAdapter extends BaseActionModeListAdapter<ScheduleItem> {
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
	public List<ScheduleItem> getItemList() {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
			vh = createViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		final ScheduleItem item = getItemList().get(position);
		vh.mStatusLevelV.setBackgroundColor(Utils.getStatusLevelColor(parent.getContext(), item));
		vh.mStatusIv.setImageResource(item.getType().getIconDrawResId());
		vh.mStatusTv.setText(Utils.convertValue(item));
		if (mWarningDrawable != null && mDuplicatedItem != null && item.getId() == mDuplicatedItem.getId()) {
			Utils.setBackgroundCompat(convertView, mWarningDrawable);
			((AnimationDrawable) convertView.getBackground()).start();
		} else {
			convertView.setBackgroundResource(R.drawable.selector_item_bg);
		}
		if(!TextUtils.isEmpty(item.getReserveLeft()) && !TextUtils.isEmpty(item.getReserveRight())) {
			if(TextUtils.equals("boolean", item.getReserveRight())) {
				boolean bool = Boolean.valueOf(item.getReserveLeft());
				Utils.showBadgeView(parent.getContext(),
						vh.mInfoBgv, Utils.convertBooleanToOnOff(parent.getContext(), bool));
			}
		} else {
			vh.mInfoBgv.setVisibility(View.GONE);
		}

		vh.mSuTv.setSelected(false);vh.mSuTv.setTypeface(null, Typeface.NORMAL);
		vh.mMoTv.setSelected(false);vh.mMoTv.setTypeface(null, Typeface.NORMAL);
		vh.mTuTv.setSelected(false);vh.mTuTv.setTypeface(null, Typeface.NORMAL);
		vh.mWeTv.setSelected(false);vh.mWeTv.setTypeface(null, Typeface.NORMAL);
		vh.mThTv.setSelected(false);vh.mThTv.setTypeface(null, Typeface.NORMAL);
		vh.mFrTv.setSelected(false);vh.mFrTv.setTypeface(null, Typeface.NORMAL);
		vh.mSaTv.setSelected(false);vh.mSaTv.setTypeface(null, Typeface.NORMAL);
		EventRecurrence er = item.getEventRecurrence();
		int[] byday = er.byday;
		for (int i : byday) {
			switch (i) {
			case EventRecurrence.SU:
				vh.mSuTv.setSelected(true);
				vh.mSuTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.MO:
				vh.mMoTv.setSelected(true);
				vh.mMoTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.TU:
				vh.mTuTv.setSelected(true);
				vh.mTuTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.WE:
				vh.mWeTv.setSelected(true);
				vh.mWeTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.TH:
				vh.mThTv.setSelected(true);
				vh.mThTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.FR:
				vh.mFrTv.setSelected(true);
				vh.mFrTv.setTypeface(null, Typeface.BOLD);
				break;
			case EventRecurrence.SA:
				vh.mSaTv.setSelected(true);
				vh.mSaTv.setTypeface(null, Typeface.BOLD);
				break;
			}
		}
		super.getView(position, convertView, parent);
		return convertView;
	}

	/**
	 * Get {@link android.support.annotation.LayoutRes} of list.
	 *
	 * @return {@link android.support.annotation.LayoutRes}
	 */
	protected abstract int getLayoutId();

	/**
	 * Create a  {@link BaseScheduleListAdapter.ViewHolder} object variant.
	 *
	 * @param convertView
	 * 		The root {@link android.view.View} of item.
	 *
	 * @return A {@link BaseScheduleListAdapter.ViewHolder} object.
	 */
	protected abstract ViewHolder createViewHolder(View convertView);

	/**
	 * Get the index of the item whose "id" equals to {@code item} in the data-list.
	 *
	 * @param item
	 * 		The item to search.
	 *
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
	 *
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
		itemFound.setReserveLeft(newItem.getReserveLeft());
		itemFound.setReserveRight(newItem.getReserveRight());
		itemFound.setEventRecurrence(newItem.getEventRecurrence());
		notifyDataSetChanged();
	}

	/**
	 * Remove a found item which has been cached by this {@link android.widget.Adapter}.
	 * <p/>
	 * When {@code itemFound} is null, nothing happens.
	 * <p/>
	 * It calls <b>{@link #notifyDataSetChanged()}</b> internally.
	 *
	 * @param itemFound
	 * 		The item that has been cached.
	 * @param itemFound
	 * 		The item to remove.
	 */
	public void removeItem(ScheduleItem itemFound) {
		if (itemFound != null) {
			for (ScheduleItem i : mItemList) {
				if (i.getId() == itemFound.getId()) {
					mItemList.remove(i);
					notifyDataSetChanged();
					break;
				}
			}
		}
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

	@Override
	protected List<ScheduleItem> getDataSource() {
		return mItemList;
	}

	@Override
	protected long getItemKey(ScheduleItem item) {
		return item.getId();
	}

	/**
	 * ViewHolder patter for {@link com.schautup.R.layout#item_schedule_grid}.
	 *
	 * @author Xinyue Zhao
	 * */
	protected static class ViewHolder extends ViewHolderActionMode{
		private View mStatusLevelV;
		private BadgeView mInfoBgv;
		private ImageView mStatusIv;
		private TextView mStatusTv;

		private TextView mSuTv;
		private TextView mMoTv;
		private TextView mTuTv;
		private TextView mWeTv;
		private TextView mThTv;
		private TextView mFrTv;
		private TextView mSaTv;

		protected ViewHolder(View convertView) {
			super(convertView);
			mStatusLevelV = convertView.findViewById(R.id.status_level_v);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mInfoBgv = (BadgeView) convertView.findViewById(R.id.info_bgv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);

			mSuTv = (TextView) convertView.findViewById(R.id.su_tv);
			mMoTv = (TextView) convertView.findViewById(R.id.mo_tv);
			mTuTv = (TextView) convertView.findViewById(R.id.tu_tv);
			mWeTv = (TextView) convertView.findViewById(R.id.we_tv);
			mThTv = (TextView) convertView.findViewById(R.id.th_tv);
			mFrTv = (TextView) convertView.findViewById(R.id.fr_tv);
			mSaTv = (TextView) convertView.findViewById(R.id.sa_tv);
		}
	}
}

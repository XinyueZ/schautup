package com.schautup.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.Utils;
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

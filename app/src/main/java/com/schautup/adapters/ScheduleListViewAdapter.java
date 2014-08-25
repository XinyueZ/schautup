package com.schautup.adapters;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.Utils;
import com.schautup.bus.SetOptionEvent;
import com.schautup.data.ScheduleItem;

import de.greenrobot.event.EventBus;

/**
 * The adapter for main {@link android.widget.ListView}.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleListViewAdapter extends BaseAdapter {
	private static final int LAYOUT = R.layout.item_schedule_lv;
	/**
	 * Data source.
	 */
	private List<ScheduleItem> mItemList;


	/**
	 * Constructor of {@link #ScheduleListViewAdapter}.
	 *
	 * @param itemList
	 * 		Data source of {@link android.widget.ListView}.
	 */
	public ScheduleListViewAdapter(List<ScheduleItem> itemList) {
		mItemList = itemList;
	}

	@Override
	public int getCount() {
		return mItemList.size();
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
			convertView = LayoutInflater.from(parent.getContext()).inflate(LAYOUT, parent, false);
			h = new ViewHolder(convertView);
			convertView.setTag(h);
		} else {
			h = (ViewHolder) convertView.getTag();
		}
		final ScheduleItem item = mItemList.get(position);
		h.mStatusIv.setImageResource(item.getType().getIconDrawResId());
		h.mStatusTv.setText(Utils.timeFromItem(item));
		h.mOptionBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new SetOptionEvent(item));
			}
		});

		return convertView;
	}

	/**
	 * ViewHolder patter for item_schedule_lv.
	 */
	private static class ViewHolder {
		private ImageView mStatusIv;
		private TextView mStatusTv;
		private ImageButton mOptionBtn;

		private ViewHolder(View convertView) {
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mOptionBtn = (ImageButton) convertView.findViewById(R.id.status_option_btn);
		}
	}

}

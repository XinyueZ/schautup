package com.schautup.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public final class ScheduleGridViewAdapter extends BaseScheduleAdapter {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_schedule_grid;


	@Override
	public int getCount() {
		return getItemList().size();
	}

	@Override
	public Object getItem(int position) {
		return getItemList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder h;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(ITEM_LAYOUT, parent, false);
			h = new ViewHolder(convertView);
			convertView.setTag(h);
		} else {
			h = (ViewHolder) convertView.getTag();
		}
		final ScheduleItem item = getItemList().get(position);
		h.mStatusIv.setImageResource(item.getType().getIconDrawResId());
		h.mStatusTv.setText(Utils.timeFromItem(item));
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new SetOptionEvent(item));
			}
		});

		return convertView;
	}

	/**
	 * ViewHolder patter for item_schedule_grid.
	 */
	private static class ViewHolder {
		private ImageView mStatusIv;
		private TextView mStatusTv;

		private ViewHolder(View convertView) {
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
		}
	}

}

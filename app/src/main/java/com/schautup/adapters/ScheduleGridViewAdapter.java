package com.schautup.adapters;

import android.view.View;
import android.view.ViewGroup;

import com.schautup.R;
import com.schautup.bus.ShowSetOptionEvent;

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
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ShowSetOptionEvent(getItemList().get(position)));
			}
		});

		return convertView;
	}

	@Override
	protected int getLayoutId() {
		return ITEM_LAYOUT;
	}

	@Override
	protected ViewHolder createViewHolder(View convertView) {
		return new ViewHolder(convertView);
	}
}

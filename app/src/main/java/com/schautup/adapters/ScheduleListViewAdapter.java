package com.schautup.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;

import de.greenrobot.event.EventBus;

/**
 * The adapter for main {@link android.widget.ListView}.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleListViewAdapter extends BaseScheduleAdapter {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_schedule_lv;


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.mOptionBtn.setOnClickListener(new AnimImageButton.OnAnimImageButtonClickedListener() {
					@Override
					public void onClick() {
						EventBus.getDefault().postSticky(new ShowSetOptionEvent(getItemList().get(position)));
					}
				});

		viewHolder.mOptionBtn.setVisibility(!isActionMode() ? View.VISIBLE : View.GONE);
		viewHolder.mEditedTimeTv.setText(Utils.convertTimestamps2dateString(parent.getContext(), getItemList().get(position)
				.getEditedTime()));
		return convertView;
	}


	@Override
	protected int getLayoutId() {
		return ITEM_LAYOUT;
	}

	@Override
	protected BaseScheduleAdapter.ViewHolder createViewHolder(View convertView) {
		return new ViewHolder(convertView);
	}

	/**
	 * ViewHolder patter for {@link com.schautup.R.layout#item_schedule_lv}.
	 *
	 * @author Xinyue Zhao
	 */
	private static class ViewHolder extends BaseScheduleAdapter.ViewHolder {
		private ImageButton mOptionBtn;
		private TextView mEditedTimeTv;

		private ViewHolder(View convertView) {
			super(convertView);
			mOptionBtn = (ImageButton) convertView.findViewById(R.id.status_option_btn);
			mEditedTimeTv = (TextView) convertView.findViewById(R.id.edited_at_tv);
		}
	}

}

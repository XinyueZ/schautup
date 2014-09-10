package com.schautup.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.data.HistoryItem;
import com.schautup.data.ScheduleType;
import com.schautup.utils.Utils;

/**
 * {@link android.widget.Adapter} for list of all histories.
 *
 * @author Xinyue Zhao
 */
public class HistoryListAdapter extends BaseActionModeAdapter<HistoryItem> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_log_history_lv;
	/**
	 * Data-source.
	 */
	private List<HistoryItem> mHistoryItems;


	/**
	 * Constructor of {@link HistoryListAdapter}.
	 *
	 * @param historyItems
	 * 		Data-source.
	 */
	public HistoryListAdapter(List<HistoryItem> historyItems) {
		setData(historyItems);
	}

	public void setData(List<HistoryItem> historyItems) {
		mHistoryItems = historyItems;
	}

	@Override
	public int getCount() {
		return mHistoryItems != null ? mHistoryItems.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mHistoryItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		Context cxt = parent.getContext();
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		HistoryItem item = mHistoryItems.get(position);
		ScheduleType type = item.getType();
		vh.mStatusIv.setImageResource(type.getIconDrawResId());
		vh.mStatusTv.setText(cxt.getString(type.getNameResId()));
		vh.mLoggedTimeTv.setText(cxt.getString(R.string.lbl_log_at,
				Utils.convertTimestamps2dateString(parent.getContext(),
						item.getLogTime())));

		super.getView(position, convertView, parent);
		return convertView;
	}


	@Override
	protected List<HistoryItem> getDataSource() {
		return mHistoryItems;
	}

	@Override
	protected long getItemKey(HistoryItem item) {
		return item.getId();
	}

	/**
	 * ViewHolder pattern for the item of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static class ViewHolder extends ViewHolderActionMode {
		/**
		 * Show symbol of {@link com.schautup.data.ScheduleType}.
		 */
		private ImageView mStatusIv;
		/**
		 * Show name of {@link com.schautup.data.ScheduleType}.
		 */
		private TextView mStatusTv;
		/**
		 * Show logged time.
		 */
		private TextView mLoggedTimeTv;

		/**
		 * Constructor of {@link com.schautup.adapters.HistoryListAdapter.ViewHolder}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		private ViewHolder(View convertView) {
			super(convertView);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mLoggedTimeTv = (TextView) convertView.findViewById(R.id.log_at_tv);
		}
	}

}

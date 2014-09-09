package com.schautup.adapters;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.data.HistoryItem;
import com.schautup.data.ScheduleType;

/**
 * {@link android.widget.Adapter} for list of all histories.
 *
 * @author Xinyue Zhao
 */
public class HistoryListAdapter extends BaseAdapter implements OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_log_history_lv;
	/**
	 * Items that will be removed.
	 */
	private SparseArrayCompat<HistoryItem> mToRmvItems;
	/**
	 * Data-source.
	 */
	private List<HistoryItem> mHistoryItems;

	/**
	 * {@code true} if the the {@link android.widget.ListView} is under the ActionMode;
	 */
	private boolean mActionMode;

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
		return mHistoryItems.size();
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
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(item.getLogTime());
		vh.mLoggedTimeTv.setText(cxt.getString(R.string.lbl_log_at, c.getTime().toLocaleString()));
		vh.mDeleteCb.setVisibility(mActionMode ? View.VISIBLE : View.GONE);
		if (mActionMode) {
			//Data-set will be used in onClick.
			vh.mDeleteCb.setTag(item);
			vh.mDeleteCb.setOnClickListener(this);
		}
		return convertView;
	}

	/**
	 * The {@link android.widget.ListView} begins in ActionMode.
	 */
	public void actionModeBegin() {
		mToRmvItems = new SparseArrayCompat<HistoryItem>();
		mActionMode = true;
		notifyDataSetChanged();
	}

	/**
	 * The {@link android.widget.ListView} ends from ActionMode.
	 */
	public void actionModeEnd() {
		mToRmvItems.clear();
		mToRmvItems = null;
		mActionMode = false;
		notifyDataSetChanged();
	}

	/**
	 * Get {@link java.util.List} of {@link com.schautup.data.HistoryItem}.
	 */
	public List<HistoryItem> getHistoryItems() {
		return mHistoryItems;
	}


	@Override
	public void onClick(View v) {
		if (mToRmvItems != null) {
			HistoryItem item = (HistoryItem) v.getTag();
			mToRmvItems.put(item.getId(), item);
		}
	}

	/**
	 * Removed all selected items from cached {@link java.util.List}.
	 *
	 * @return The {@link com.schautup.data.HistoryItem}s that have been removed from cache. Return {@code null} when no
	 * removal happened.
	 */
	public SparseArrayCompat<HistoryItem> removeItems() {
		if (mToRmvItems != null) {
			int key = 0;
			HistoryItem item;
			for (int i = 0; i < mToRmvItems.size(); i++) {
				key = mToRmvItems.keyAt(i);
				item = mToRmvItems.get(key);
				mHistoryItems.remove(item);
			}
			return mToRmvItems;
		}
		return null;
	}

	/**
	 * ViewHolder pattern for the item of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static class ViewHolder {
		/**
		 * Check is shown when wanna delete.
		 */
		private CheckBox mDeleteCb;
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
			mDeleteCb = (CheckBox) convertView.findViewById(R.id.delete_cb);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mLoggedTimeTv = (TextView) convertView.findViewById(R.id.log_at_tv);
		}
	}

}

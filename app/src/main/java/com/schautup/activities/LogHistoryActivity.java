package com.schautup.activities;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.data.HistoryItem;
import com.schautup.data.ScheduleType;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;

/**
 * A list shows all histories that the Schautup has done.
 *
 * @author Xinyue Zhao
 */
public final class LogHistoryActivity extends BaseActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_log_history;

	/**
	 * The progress indicator.
	 */
	private SwipeRefreshLayout mRefreshLayout;

	/**
	 * {@link android.widget.ListView} for all histories.
	 */
	private ListView mLv;
	/**
	 * Indicator when there's no data.
	 */
	private View mNoDataV;
	/**
	 * {@link android.widget.Adapter} for {@link #mLv}.
	 */
	private HistoryListAdapter mAdapter;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.ProgressbarEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ProgressbarEvent}.
	 */
	public void onEvent(ProgressbarEvent e) {
		mRefreshLayout.setRefreshing(e.isShow());
	}

	//------------------------------------------------


	/**
	 * Show single instance of {@link com.schautup.activities.LogHistoryActivity}
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, LogHistoryActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mNoDataV = findViewById(R.id.no_history_tv);
		mLv = (ListView) findViewById(R.id.history_lv);

		// Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.prg_0, R.color.prg_1, R.color.prg_2, R.color.prg_3);
	}

	@Override
	public void onResume() {
		super.onResume();
		new ParallelTask<Void, List<HistoryItem>, List<HistoryItem>>(true) {
			@Override
			protected List<HistoryItem> doInBackground(Void... params) {
				return DB.getInstance(getApplication()).getAllHistories();
			}

			@Override
			protected void onPostExecute(List<HistoryItem> result) {
				super.onPostExecute(result);
				if (result.size() > 0) {
					mLv.setVisibility(View.VISIBLE);
					mNoDataV.setVisibility(View.GONE);

					if (mAdapter != null) {
						mAdapter.setData(result);
						mAdapter.notifyDataSetChanged();
					} else {
						mAdapter = new HistoryListAdapter(result);
						mLv.setAdapter(mAdapter);
					}
				} else {
					mLv.setVisibility(View.GONE);
					mNoDataV.setVisibility(View.VISIBLE);
				}
			}
		}.executeParallel();
	}

	private static class HistoryListAdapter extends BaseAdapter {
		/**
		 * Main layout for this component.
		 */
		private static final int ITEM_LAYOUT = R.layout.item_log_history_lv;
		/**
		 * Data-source.
		 */
		private List<HistoryItem> mHistoryItems;

		/**
		 * Constructor of {@link com.schautup.activities.LogHistoryActivity.HistoryListAdapter}.
		 *
		 * @param historyItems
		 * 		Data-source.
		 */
		private HistoryListAdapter(List<HistoryItem> historyItems) {
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
			return convertView;
		}
	}

	/**
	 * ViewHolder pattern for the item of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static class ViewHolder {
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
		 * Constructor of {@link com.schautup.activities.LogHistoryActivity.ViewHolder}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		private ViewHolder(View convertView) {
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mLoggedTimeTv = (TextView) convertView.findViewById(R.id.log_at_tv);
		}
	}
}

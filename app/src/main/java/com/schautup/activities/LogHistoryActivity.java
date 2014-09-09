package com.schautup.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.schautup.R;
import com.schautup.adapters.HistoryListAdapter;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.data.HistoryItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;

import de.greenrobot.event.EventBus;

/**
 * A list shows all histories that the Schautup has done.
 *
 * @author Xinyue Zhao
 */
public final class LogHistoryActivity extends BaseActivity implements OnItemLongClickListener, Callback {
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
	/**
	 * Menu for the Action-Mode.
	 */
	private static final int ACTION_MODE_MENU = R.menu.action_mode;
	/**
	 * Status of ActionMode.
	 */
	private ActionMode mActionMode;


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

		//Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.prg_0, R.color.prg_1, R.color.prg_2, R.color.prg_3);

		//Long press to the ActionMode.
		mLv.setOnItemLongClickListener(this);
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

	@Override
	public void onPause() {
		super.onPause();
		if (mActionMode != null) {
			mActionMode.finish();
			if (mAdapter != null) {
				mAdapter.actionModeEnd();
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		//Starting the ActionMode.
		startSupportActionMode(this);
		mAdapter.actionModeBegin();
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
		mActionMode = actionMode;
		actionMode.getMenuInflater().inflate(ACTION_MODE_MENU, menu);
		mLv.setOnItemLongClickListener(null);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_delete: {
			new ParallelTask<Void, Void, SparseArrayCompat<HistoryItem>>(true) {
				@Override
				protected SparseArrayCompat<HistoryItem> doInBackground(Void... params) {
					DB db = DB.getInstance(getApplication());
					int key;
					HistoryItem item;
					SparseArrayCompat<HistoryItem> removedItems = mAdapter.removeItems();
					for (int i = 0; removedItems != null && i < removedItems.size(); i++) {
						key = removedItems.keyAt(i);
						item = removedItems.get(key);
						db.removeHistory(item);
					}
					return removedItems;
				}

				@Override
				protected void onPostExecute(SparseArrayCompat<HistoryItem> result) {
					super.onPostExecute(result);
					if (result == null) {
						mAdapter.notifyDataSetChanged();
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_success),
								getResources().getColor(R.color.warning_green_1)));
					} else {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_fail),
								getResources().getColor(R.color.warning_red_1)));
					}
					actionMode.finish();
				}
			}.executeParallel();
			break;
		}
		default:
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode actionMode) {
		mAdapter.actionModeEnd();
		mActionMode = null;
		mLv.setOnItemLongClickListener(null);
	}
}

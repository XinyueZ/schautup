package com.schautup.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.adapters.HistoryListAdapter;
import com.schautup.bus.AddedHistoryEvent;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.data.HistoryItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;

import de.greenrobot.event.EventBus;

/**
 * A list shows all histories that the Schautup has done.
 *
 * @author Xinyue Zhao
 */
public final class LogHistoryActivity extends BaseActivity implements OnScrollListener, OnItemLongClickListener,
		Callback, AnimationListener, OnRefreshListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_log_history;
	/**
	 * Header layout.
	 */
	private static final int LAYOUT_HEADER = R.layout.inc_lv_header;
	/**
	 * The progress indicator.
	 */
	private SwipeRefreshLayout mRefreshLayout;

	/**
	 * {@link android.widget.ExpandableListView} for all histories.
	 */
	private ExpandableListView mLv;
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
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mLv}.
	 */
	private int mLastFirstVisibleItem;
	/**
	 * Message sticky.
	 */
	private View mStickyV;
	/**
	 * {@link android.widget.TextView} where message to be shown.
	 */
	private TextView mStickyMsgTv;
	/**
	 * {@code true} if still loading data.
	 */
	private boolean mLoading;

	/**
	 * Header of {@link android.widget.ListView}.
	 */
	private View mHeaderV;
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

	/**
	 * Handler for {@link com.schautup.bus.AddedHistoryEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.AddedHistoryEvent}.
	 */
	public void onEvent(AddedHistoryEvent e) {
		loadHistory();
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
		getSupportActionBar().setIcon(R.drawable.ic_action_log);
		mNoDataV = findViewById(R.id.no_history_tv);
		mLv = (ExpandableListView) findViewById(R.id.history_explv);
		mLv.setOnScrollListener(this);
		//Sticky message box.
		mStickyV = findViewById(R.id.sticky_fl);
		mStickyMsgTv = (TextView) mStickyV.findViewById(R.id.sticky_msg_tv);
		//Add header.
		mHeaderV = getLayoutInflater().inflate(LAYOUT_HEADER, mLv, false);
		mHeaderV.getLayoutParams().height = getActionBarHeight();
		//Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.prg_0, R.color.prg_1, R.color.prg_2, R.color.prg_3);
		mRefreshLayout.setOnRefreshListener(this);
		//Long press to the ActionMode.
		mLv.setOnItemLongClickListener(this);
		//Move the progress-indicator firstly under the ActionBar.
		ViewCompat.setY(mRefreshLayout, getActionBarHeight());
	}

	@Override
	public void onResume() {
		super.onResume();
		loadHistory();

		Prefs prefs = Prefs.getInstance(getApplication());
		if (!prefs.isTipLongPressRmvLogHistoryShown() &&
				mAdapter != null &&
				mAdapter.getGroupCount() > 0) {
			//For first log-items, we show a message on sticky.
			showStickyMsg(getString(R.string.msg_long_press_rmv_log_history), getResources().getColor(
					R.color.warning_green_1));
			prefs.setTipLongPressRmvLogHistoryShown(true);
		}
	}

	/**
	 * Load all log-history from DB.
	 */
	private void loadHistory() {
		new ParallelTask<Void, List<HistoryItem>, List<HistoryItem>>(true) {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mLoading = true;
			}

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
				mLoading = false;
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
		if (mAdapter != null) {
			mAdapter.actionModeBegin();

			for(int i = 0, count = mAdapter.getGroupCount(); i < count; i++) {
				if(mLv.isGroupExpanded(i)) {
					mLv.collapseGroup(i);
				}
			}
		}

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
	public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_delete: {
			new ParallelTask<Void, Void, LongSparseArray<HistoryItem>>(true) {
				@Override
				protected LongSparseArray<HistoryItem> doInBackground(Void... params) {
					DB db = DB.getInstance(getApplication());
					long key;
					HistoryItem item;
					LongSparseArray<HistoryItem> removedItems = mAdapter.removeItems();
					for (int i = 0; removedItems != null && i < removedItems.size(); i++) {
						key = removedItems.keyAt(i);
						item = removedItems.get(key);
						db.removeHistory(item);
					}
					return removedItems;
				}

				@Override
				protected void onPostExecute(LongSparseArray<HistoryItem> result) {
					super.onPostExecute(result);
					if (result == null) {
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_success),
								getResources().getColor(R.color.warning_green_1)));
					} else {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_fail),
								getResources().getColor(R.color.warning_red_1)));
					}
					mActionMode.finish();
					mActionMode = null;
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
		mActionMode = null;
		if (mAdapter != null) {
			mAdapter.actionModeEnd();
		}
		mLv.setOnItemLongClickListener(this);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (view.getId() == mLv.getId()) {
			final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRefreshLayout.getLayoutParams();
			final int currentFirstVisibleItem = view.getFirstVisiblePosition();
			if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				if (getSupportActionBar().isShowing() && !mLoading) {
					getSupportActionBar().hide();
					ViewCompat.setY(mRefreshLayout, 0);
					mLv.addHeaderView(mHeaderV, null, false);
				}
			} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
					ViewCompat.setY(mRefreshLayout, getActionBarHeight());
					mLv.removeHeaderView(mHeaderV);
				}
			}
			mLastFirstVisibleItem = currentFirstVisibleItem;
		}
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	/**
	 * Show message on sticky box.
	 *
	 * @param msg
	 * 		Message.
	 * @param colorRes
	 * 		{@link android.support.annotation.ColorRes} for sticky background.
	 */
	private void showStickyMsg(String msg, int colorRes) {
		if (getSupportActionBar().isShowing()) {
			getSupportActionBar().hide();
		}
		mStickyMsgTv.setText(msg);
		mStickyV.setVisibility(View.VISIBLE);
		mStickyV.setBackgroundColor(colorRes);
		AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.slide_in_and_out);
		animSet.setAnimationListener(this);
		mStickyV.startAnimation(animSet);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mStickyV.setVisibility(View.GONE);
		getSupportActionBar().show();
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onRefresh() {
		loadHistory();
	}

}

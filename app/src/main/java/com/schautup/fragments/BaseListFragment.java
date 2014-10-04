package com.schautup.fragments;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;

import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.adapters.BaseScheduleListAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.AskDeleteScheduleItemsEvent;
import com.schautup.bus.DeletedConfirmEvent;
import com.schautup.bus.FilterEvent;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.HideActionModeEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.Filter;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;

import de.greenrobot.event.EventBus;

/**
 * Abstract impl for {@link com.schautup.fragments.BaseFragment}s that hold {@link android.widget.ListView}, {@link
 * android.widget.GridView}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseListFragment extends BaseFragment implements AbsListView.OnScrollListener,
		AdapterView.OnItemLongClickListener {
	/**
	 * {@link android.widget.AbsListView} for all schedules.
	 * <p/>
	 * It must be {@link android.widget.ListView}, {@link android.widget.GridView}.
	 */
	private AbsListView mLv;
	/**
	 * {@link com.schautup.adapters.BaseScheduleListAdapter} for {@link #mLv}.
	 */
	private BaseScheduleListAdapter mAdp;
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mLv}.
	 */
	private int mLastFirstVisibleItem;

	/**
	 * {@link android.view.View} for "add".
	 */
	private AnimImageButton mAddNewVG;
	/**
	 * A button to load data when there's no data.
	 */
	private AnimImageButton mNoDataBtn;
	/**
	 * There is different between android pre 3.0 and 3.x, 4.x on this wording.
	 */
	private static final String ALPHA =
			(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) ? "alpha" : "Alpha";

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.AllScheduleLoadedEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.AllScheduleLoadedEvent}.
	 */
	public void onEvent(AllScheduleLoadedEvent e) {
		List<ScheduleItem> data = e.getScheduleItemList();
		showAllSelectedData(data);

	}


	/**
	 * Handler for {@link com.schautup.bus.UpdatedItemEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.UpdatedItemEvent}.
	 */
	public void onEvent(UpdatedItemEvent e) {
		if (mNoDataBtn.getVisibility() == View.VISIBLE) {
			mNoDataBtn.setVisibility(View.GONE);
		}
		ScheduleItem item = e.getItem();
		ScheduleItem itemFound = mAdp.findItem(item);
		if (itemFound == null) {
			mAdp.addItem(item);
		} else {
			mAdp.editItem(itemFound, item);
		}
		refreshUI(item, getResources().getDrawable(R.drawable.anim_list_warning_green));
	}


	/**
	 * Handler for {@link com.schautup.bus.HideActionModeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.HideActionModeEvent}.
	 */
	public void onEvent(HideActionModeEvent e) {
		if (mAdp != null) {
			mAdp.actionModeEnd();
		}
		mLv.setOnItemLongClickListener(this);
		//The ActionMode is ending,  add-button should work again.
		mLv.setOnScrollListener(this);
		mAddNewVG.setVisibility(View.VISIBLE);
	}

	/**
	 * Handler for {@link com.schautup.bus.AskDeleteScheduleItemsEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.AskDeleteScheduleItemsEvent}.
	 */
	public void onEvent(AskDeleteScheduleItemsEvent e) {
		if (mAdp != null) {
			EventBus.getDefault().post(new GivenRemovedScheduleItemsEvent(mAdp.removeItems()));
			if (mAdp != null) {
				mAdp.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.DeletedConfirmEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.DeletedConfirmEvent}.
	 */
	public void onEvent(DeletedConfirmEvent e) {
		if (mAdp == null || mAdp.getItemList() == null || mAdp.getItemList().size() == 0) {
			mNoDataBtn.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.FilterEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.FilterEvent}.
	 */
	public void onEvent(FilterEvent e) {
		new ParallelTask<Filter, List<ScheduleItem>, List<ScheduleItem>>(true) {
			@Override
			protected List<ScheduleItem> doInBackground(Filter... params) {
				Filter filter = params[0];
				int hour = filter.getHour();
				int minute = filter.getMinute();
				EventRecurrence er = filter.getEventRecurrence();
				List<ScheduleItem> items =  DB.getInstance(getActivity().getApplication()).getSchedules(hour, minute, filter.getSelectedTypes(), er);
				return items;
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> result) {
				super.onPostExecute(result);
				showAllSelectedData(result);
			}
		}.executeParallel(e.getFilter());
	}

	//------------------------------------------------

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLv.setOnScrollListener(this);

		// Add new.
		mAddNewVG = (AnimImageButton) view.findViewById(R.id.add_btn);
		mAddNewVG.setOnClickListener(new AnimImageButton.OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new AddNewScheduleItemEvent());
			}
		});

		// No data.
		mNoDataBtn = (AnimImageButton) view.findViewById(R.id.no_data_btn);
		mNoDataBtn.setOnClickListener(new AnimImageButton.OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new AddNewScheduleItemEvent());
			}
		});
		((AdapterView) mLv).setAdapter(mAdp);
		mLv.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		mLv.setOnItemLongClickListener(null);
		if (mAdp != null) {
			mAdp.actionModeBegin();
		}
		//The ActionMode is starting, add-button should not work.
		mLv.setOnScrollListener(null);
		mAddNewVG.setVisibility(View.GONE);
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();

		new ParallelTask<Void, Void, List<ScheduleItem>>(true) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				return Utils.getAllSchedules(getActivity().getApplication());
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> result) {
				super.onPostExecute(result);
				EventBus.getDefault().postSticky(new AllScheduleLoadedEvent(result));
			}
		}.executeParallel();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAdp != null) {
			mAdp.actionModeEnd();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		float translationY = ViewHelper.getTranslationY(mAddNewVG);
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			//ListView is idle, user can add item with a button.
			if (translationY != 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(0).setDuration(500);
			}
		} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			//ListView moving, add button can dismiss.
			if (translationY == 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(getActionBarHeight() * 4).setDuration(500);
			}
		}
		if (view.getId() == mLv.getId()) {
			final int currentFirstVisibleItem = view.getFirstVisiblePosition();
			if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				if (getSupportActionBar().isShowing()) {
					EventBus.getDefault().post(new ShowActionBarEvent(false));
				}
			} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				if (!getSupportActionBar().isShowing()) {
					EventBus.getDefault().post(new ShowActionBarEvent(true));
				}
			}
			mLastFirstVisibleItem = currentFirstVisibleItem;
		}
		//So long the state of the ListView is changed, removed the warning highlighting.
		mAdp.clearWarning();
	}

	/**
	 * Refresh UI after some Ops on DB. It could be fired after user has updated(added, edited) an item, or user tried
	 * to add a duplicated data.
	 *
	 * @param item
	 * 		The item that user wanna do.
	 * @param warningDrawable
	 * 		The {@link android.support.annotation.DrawableRes} for waning animation-list.
	 */
	private void refreshUI(ScheduleItem item, @DrawableRes Drawable warningDrawable) {
		final int location = mAdp.getItemPosition(item);
		//Change the state of layout cased by mLv(AbsListView).
		mAdp.showWarningOnItem(item, warningDrawable);
		//A tricky to jump to changed low.
		//See. https://groups.google.com/forum/#!topic/android-developers/EnyldBQDUwE
		mLv.clearFocus();
		mLv.post(new Runnable() {
			@Override
			public void run() {
				mLv.setSelection(location);
			}
		});
	}


	/**
	 * Create a {@link android.widget.AbsListView} object.
	 *
	 * @return The {@link android.widget.AbsListView} object.
	 */
	protected void setListViewWidget(AbsListView absListView) {
		mLv = absListView;
	}

	/**
	 * Create a {@link com.schautup.adapters.BaseScheduleListAdapter} object.
	 *
	 * @return The {@link com.schautup.adapters.BaseScheduleListAdapter} object.
	 */
	protected void setAdapter(BaseScheduleListAdapter adp) {
		mAdp = adp;
	}

	/**
	 * Get list adapter.
	 *
	 * @return The adapter.
	 */
	protected BaseScheduleListAdapter getAdapter() {
		return mAdp;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}


	/**
	 * Show all selected data or filtered data.
	 * @param data To show.
	 */
	private void showAllSelectedData(List<ScheduleItem> data) {
		// Show all schedules on the ListView or GridView.
		if (data != null && data.size() > 0) {
			//Never see the button "no data" after at least one item was added.
			mNoDataBtn.setVisibility(View.GONE);
			mAdp.setItemList(data);
			//Show data.
			mAdp.notifyDataSetChanged();
		} else {
			mAdp.setItemList(null);
			mAdp.notifyDataSetChanged();
			mNoDataBtn.setVisibility(View.VISIBLE);
		}
	}

}

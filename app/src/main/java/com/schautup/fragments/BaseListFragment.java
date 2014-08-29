package com.schautup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.adapters.BaseScheduleAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.FindDuplicatedItemEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.views.AnimImageButton;

import de.greenrobot.event.EventBus;

/**
 * Abstract impl for {@link com.schautup.fragments.BaseFragment}s that hold {@link android.widget.ListView}, {@link
 * android.widget.GridView}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseListFragment extends BaseFragment implements AbsListView.OnScrollListener {
	/**
	 * {@link android.widget.AbsListView} for all schedules.
	 * <p/>
	 * It must be {@link android.widget.ListView}, {@link android.widget.GridView}.
	 */
	private AbsListView mLv;
	/**
	 * {@link com.schautup.adapters.BaseScheduleAdapter} for {@link #mLv}.
	 */
	private BaseScheduleAdapter mAdp;
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mLv}.
	 */
	private int mLastFirstVisibleItem;

	/**
	 * {@link android.view.ViewGroup} that holds an "add" {@link android.widget.ImageButton}.
	 */
	private ViewGroup mAddNewVG;
	/**
	 * A button to load data when there's no data.
	 */
	private AnimImageButton mNoDataBtn;
	/**
	 * {@code true} if the adapter has pushed data on to list.
	 */
	private boolean mIsShowing = false;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.AllScheduleLoadedEvent}
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.AllScheduleLoadedEvent}.
	 */
	public void onEvent(AllScheduleLoadedEvent e) {
		// Show data.
		if (e.getScheduleItemList() != null && e.getScheduleItemList().size() > 0) {
			mNoDataBtn.setVisibility(View.GONE);
			mAdp.setItemList(e.getScheduleItemList());
			if (mIsShowing) {
				mAdp.notifyDataSetChanged();
			} else {
				((AdapterView) getListViewWidget()).setAdapter(mAdp);
				mIsShowing = true;
			}
		} else {
			mNoDataBtn.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.FindDuplicatedItemEvent}
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.FindDuplicatedItemEvent}.
	 */
	public void onEvent(FindDuplicatedItemEvent e) {
		final ScheduleItem item = e.getDuplicatedItem();
		final int location = mAdp.getItemPosition(item);
		mLv.setSelection(location);

		//This tricky is only for compatiable impl. for the GridView. For ListView,
		//we can call mAdp.showWarningOnItem(item); directly after  mLv.setSelection(location);
		ViewTreeObserver viewTreeObserver = mLv.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mLv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				View v = mLv.getChildAt(location);
				if (v != null) {
					v.setSelected(true);
					mAdp.showWarningOnItem(item);
				}
			}
		});
	}

	//------------------------------------------------

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLv.setOnScrollListener(this);

		// Add new.
		mAddNewVG = (ViewGroup) view.findViewById(R.id.add_fl);
		mAddNewVG.getLayoutParams().height = getActionBarHeight();
		mAddNewVG.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
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
	}

	@Override
	public void onResume() {
		super.onResume();

		//----------------------------------------------------------
		// Description: Test data block.
		//
		// Will be removed late.
		//----------------------------------------------------------
		//				List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		//				for (int i = 0; i < 100; i++) {
		//					items.add(new ScheduleItem(ScheduleType.MUTE, i, i, System.currentTimeMillis()));
		//				}
		//				EventBus.getDefault().post(new AllScheduleLoadedEvent(items));
		//----------------------------------------------------------
		EventBus.getDefault().post(new AllScheduleLoadedEvent(DB.getInstance(getActivity().getApplication())
				.getAllSchedules()));
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		float translationY = ViewHelper.getTranslationY(mAddNewVG);
		if (scrollState == 0) {
			//ListView is idle, user can add item with a button.
			if (translationY != 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(0).setDuration(500);
			}
		} else {
			//ListView moving, add button can dismiss.
			if (translationY == 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(getActionBarHeight()).setDuration(500);
			}
		}
		if (view.getId() == view.getId()) {
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
		mAdp.clearWarning();
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	/**
	 * Access on a widget extends from {@link android.widget.AbsListView}.
	 *
	 * @return An {@link android.widget.AbsListView} object.
	 */
	protected AbsListView getListViewWidget() {
		return mLv;
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
	 * Create a {@link com.schautup.adapters.BaseScheduleAdapter} object.
	 *
	 * @return The {@link com.schautup.adapters.BaseScheduleAdapter} object.
	 */
	protected void setAdapter(BaseScheduleAdapter adp) {
		mAdp = adp;
	}
}

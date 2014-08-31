package com.schautup.fragments;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.adapters.BaseScheduleAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.FindDuplicatedItemEvent;
import com.schautup.bus.RemovedItemEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
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
		// Show all schedules on the ListView or GridView.
		if (e.getScheduleItemList() != null && e.getScheduleItemList().size() > 0) {
			//Never see the button "no data" after at least one item was added.
			mNoDataBtn.setVisibility(View.GONE);
			mAdp.setItemList(e.getScheduleItemList());
			//Show data.
			mAdp.notifyDataSetChanged();
		} else {
			mNoDataBtn.setVisibility(View.VISIBLE);
		}

		//		Utils.showShortToast(getActivity(), "load all.");
	}

	/**
	 * Handler for {@link com.schautup.bus.FindDuplicatedItemEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.FindDuplicatedItemEvent}.
	 */
	public void onEvent(FindDuplicatedItemEvent e) {
		refreshUI(e.getDuplicatedItem(), getResources().getDrawable(R.drawable.anim_list_warning_red));
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
	 * Handler for {@link com.schautup.bus.RemovedItemEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.RemovedItemEvent}.
	 */
	public void onEvent(RemovedItemEvent e) {
		mAdp.removeItem(e.getItem());
		mNoDataBtn.setVisibility(e.getRowsRemain() == 0 ? View.VISIBLE : View.GONE);
		EventBus.getDefault().post(new ShowActionBarEvent(true));
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


		((AdapterView) mLv).setAdapter(mAdp);
		mLv.setOnItemLongClickListener(this);
	}


	@Override
	public void onResume() {
		super.onResume();

		new ParallelTask<Void, Void, List<ScheduleItem>>(true) {
			@Override
			protected List<ScheduleItem> doInBackground(Void... params) {
				return DB.getInstance(getActivity().getApplication()).getAllSchedules();
			}

			@Override
			protected void onPostExecute(List<ScheduleItem> _result) {
				super.onPostExecute(_result);
				EventBus.getDefault().postSticky(new AllScheduleLoadedEvent(_result));
			}
		}.executeParallel();
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
		//Dismiss bottom "add" button.
		float translationY = ViewHelper.getTranslationY(mAddNewVG);
		if (translationY == 0) {
			ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
			animator.translationY(getActionBarHeight()).setDuration(500);
		}
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

	/**
	 * Get list adapter.
	 * @return The adapter.
	 */
	protected BaseScheduleAdapter getAdapter() {
		return mAdp;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}

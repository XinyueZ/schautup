package com.schautup.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.adapters.ScheduleListViewAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;

import de.greenrobot.event.EventBus;

/**
 * A list view for all schedules.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleListFragment extends BaseFragment implements AbsListView.OnScrollListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_schedule_list;
	/**
	 * Header layout for the {@link #mScheduleLv}.
	 */
	private static final int LAYOUT_HEADER = R.layout.inc_lv_header;
	/**
	 * {@link android.widget.ListView} for all schedules.
	 */
	private ListView mScheduleLv;
	/**
	 * {@link com.schautup.adapters.ScheduleListViewAdapter} for {@link #mScheduleLv}.
	 */
	private ScheduleListViewAdapter mAdapter;
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mScheduleLv}.
	 */
	private int mLastFirstVisibleItem;

	/**
	 * {@link android.view.ViewGroup} that holds an "add" {@link android.widget.ImageButton}.
	 */
	private ViewGroup mAddNewVG;


	/**
	 * Initialize a {@link com.schautup.fragments.ScheduleListFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @return An instance of {@link com.schautup.fragments.ScheduleListFragment}.
	 */
	public static Fragment newInstance(Context context) {
		return Fragment.instantiate(context, ScheduleListFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Add new.
		mAddNewVG = (ViewGroup) view.findViewById(R.id.add_fl);
		mAddNewVG.getLayoutParams().height = getActionBarHeight();
		mAddNewVG.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new AddNewScheduleItemEvent());
			}
		});

		// List header.
		mScheduleLv = (ListView) view.findViewById(R.id.schedule_lv);
		mScheduleLv.setOnScrollListener(this);
		View headerV = getActivity().getLayoutInflater().inflate(LAYOUT_HEADER, mScheduleLv, false);
		mScheduleLv.addHeaderView(headerV, null, false);
		headerV.getLayoutParams().height = getActionBarHeight();

		//----------------------------------------------------------
		// Description: Test data block.
		//
		// Will be removed late.
		//----------------------------------------------------------
		List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		for (int i = 0; i < 100; i++) {
			items.add(new ScheduleItem(ScheduleType.MUTE, i, i, System.currentTimeMillis()));
		}
		//----------------------------------------------------------

		// Show data.
		mAdapter = new ScheduleListViewAdapter(items);
		mScheduleLv.setAdapter(mAdapter);
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		final ListView lw = (ListView) view;
		float translationY = ViewHelper.getTranslationY(mAddNewVG);
		if (scrollState == 0) {
			//ListView is idle, user can add item with a button.
			if (translationY != 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(0).setDuration(1000);
			}
		} else {
			//ListView moving, add button can dismiss.
			if (translationY == 0) {
				ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewVG);
				animator.translationY(getActionBarHeight()).setDuration(1000);
			}
		}
		if (view.getId() == lw.getId()) {
			final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
			if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				if (getSupportActionBar().isShowing()) {
					getSupportActionBar().hide();
				}
			} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			}
			mLastFirstVisibleItem = currentFirstVisibleItem;
		}
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}

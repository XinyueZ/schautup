package com.schautup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.schautup.R;
import com.schautup.adapters.ScheduleListViewAdapter;

/**
 * A list view for all schedules.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleListFragment extends BaseListFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_schedule_list;
	/**
	 * Header layout.
	 */
	private static final int LAYOUT_HEADER = R.layout.inc_lv_header;


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
		ListView listView = (ListView) view.findViewById(R.id.schedule_lv);
		setListViewWidget(listView);
		setAdapter(new ScheduleListViewAdapter());
		//Add header.
		View headerV = getActivity().getLayoutInflater().inflate(LAYOUT_HEADER, listView, false);
		listView.addHeaderView(headerV, null, false);
		headerV.getLayoutParams().height = getActionBarHeight();
		super.onViewCreated(view, savedInstanceState);
	}
}

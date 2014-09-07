package com.schautup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.schautup.R;
import com.schautup.adapters.ScheduleGridViewAdapter;
import com.schautup.bus.ShowActionModeEvent;
import com.schautup.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Show all schedules in a grid.
 *
 * @author Xinyue Zhao
 */
public final class ScheduleGridFragment extends BaseListFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_schedule_grid;
	/**
	 * Header layout.
	 */
	private static final int LAYOUT_HEADER = R.layout.inc_lv_header;

	/**
	 * Initialize a {@link com.schautup.fragments.ScheduleListFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @return An instance of {@link com.schautup.fragments.ScheduleGridFragment}.
	 */
	public static Fragment newInstance(Context context) {
		return Fragment.instantiate(context, ScheduleGridFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		GridView gridView = (GridView) view.findViewById(R.id.schedule_gv);
		int screenWidth = Utils.getScreenSize(getActivity(), 0).Width;
		gridView.setColumnWidth(screenWidth / 3);
		setListViewWidget(gridView);
		setAdapter(new ScheduleGridViewAdapter());
		//Add header.
		View headerV = getActivity().getLayoutInflater().inflate(LAYOUT_HEADER, (ViewGroup) view, false);
		((ViewGroup) (view.findViewById(R.id.header_fl))).addView(headerV);
		headerV.getLayoutParams().height = getActionBarHeight();
		super.onViewCreated(view, savedInstanceState);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		EventBus.getDefault().post(new ShowActionModeEvent(
				(com.schautup.data.ScheduleItem) getAdapter().getItem(position)));
		view.setSelected(true);
		return true;
	}
}

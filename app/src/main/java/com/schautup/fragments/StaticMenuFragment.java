package com.schautup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schautup.R;
import com.schautup.activities.HomePageWebViewActivity;
import com.schautup.activities.LogHistoryActivity;
import com.schautup.activities.SettingsActivity;
import com.schautup.bus.CloseDrawerEvent;

import de.greenrobot.event.EventBus;

/**
 * Show only the menu list.
 *
 * @author Xinyue Zhao
 */
public final class StaticMenuFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_static_menu;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		boolean isStage = getResources().getBoolean(R.bool.flag_stage);
		super.onViewCreated(view, savedInstanceState);
		View drawerItemSettings = view.findViewById(R.id.drawer_item_settings_ll);
		drawerItemSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new CloseDrawerEvent());
				SettingsActivity.showInstance(getActivity());
			}
		});

		View drawerItemHomePage = view.findViewById(R.id.drawer_item_home_page_ll);
		View drawerItemLogHistory = view.findViewById(R.id.drawer_item_log_history_ll);
		if (isStage) {
			drawerItemHomePage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new CloseDrawerEvent());
					HomePageWebViewActivity.showInstance(getActivity());
				}
			});

			drawerItemLogHistory.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new CloseDrawerEvent());
					LogHistoryActivity.showInstance(getActivity());
				}
			});
		} else {
			drawerItemHomePage.setVisibility(View.GONE);
			drawerItemLogHistory.setVisibility(View.GONE);
		}
	}
}

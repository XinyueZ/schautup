package com.schautup.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.GsonRequestTask;
import com.chopping.net.TaskHelper;
import com.schautup.R;
import com.schautup.bus.ExternalAppChangedEvent;
import com.schautup.bus.LinkToExternalAppEvent;
import com.schautup.data.AppList;
import com.schautup.data.AppListItem;
import com.schautup.utils.Prefs;

import de.greenrobot.event.EventBus;

/**
 * {@link com.schautup.fragments.AppListFragment} has all external application links.
 *
 * @author Xinyue Zhao
 */
public final class AppListFragment extends  BaseFragment{
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_applist;
	/**
	 * Layout for an external application to download/install/open.
	 */
	private static final int LAYOUT_APP_ITEM = R.layout.inc_app;
	/**
	 * {@link android.view.ViewGroup} for external applications.
	 */
	private ViewGroup mAppListVg;
	/**
	 * True if a loading app-list request is under way.
	 */
	private boolean mReqInProcess = false;
	/**
	 * {@link android.widget.Button}s of all external applications.
	 * </p>
	 * It is a map of the key that item of application against value {@link android.widget.Button} .
	 */
	private ArrayMap<AppListItem, WeakReference<Button>> mAppButtons = new ArrayMap<AppListItem, WeakReference<Button>>();

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------


	/**
	 * Event, show app-list when they have been loaded.
	 *
	 * @param e
	 * 		{@link com.schautup.App}.
	 */
	public void onEvent(AppList e) {
		showAppList(e.getItems());
	}

	/**
	 * Event, open an external app that has been installed.
	 *
	 * @param e
	 * 		{@link com.schautup.bus.LinkToExternalAppEvent}.
	 */
	public void onEvent(LinkToExternalAppEvent e) {
		com.chopping.utils.Utils.linkToExternalApp(getActivity(), e.getAppListItem());
	}


	/**
	 * Event, update list of external apps.
	 *
	 * @param e
	 * 		{@link com.schautup.bus.ExternalAppChangedEvent}.
	 */
	public void onEvent(ExternalAppChangedEvent e) {
		updateForAppChanged(e);
	}



	//------------------------------------------------

	/**
	 * New an instance of {@link AppListFragment}.
	 *
	 * @param context {@link android.content.Context}.
	 * @return
	 */
	public static Fragment newInstance(Context context ) {
		return  AppListFragment.instantiate(context, AppListFragment.class.getName()  );
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mAppListVg = (ViewGroup) view.findViewById(R.id.app_list_ll);
		loadAppList();
	}


	/**
	 * Update the status of buttons that can open store linking to the external _app or directly on the _app.
	 *
	 * @param appOpen
	 * 		The button for the app, open, install, or buy.
	 * @param app
	 * 		The data-set represent an external app.
	 */
	private void refreshExternalAppButtonStatus(final Button appOpen, final AppListItem app) {
		Activity activity = getActivity();
		if (activity != null) {
			Resources res = getResources();
			if (com.chopping.utils.Utils.isAppInstalled(app.getPackageName(), activity.getPackageManager())) {
				appOpen.setText(R.string.extapp_open);
				appOpen.setTextColor(res.getColor(R.color.installed_text));
				appOpen.setBackgroundResource(R.drawable.selector_intstalled_app_item_btn_color);
			} else {
				appOpen.setText(app.getFree() ? R.string.extapp_download : R.string.extapp_buy);
				appOpen.setTextColor(res.getColor(R.color.not_installed_text));
				appOpen.setBackgroundResource(R.drawable.selector_not_intstalled_app_item_btn_color);
			}
		}
	}

	/**
	 * Show list of all external applications.
	 *
	 * @param apps
	 * 		The array of all external applications.
	 */
	private void showAppList(AppListItem[] apps) {
		Activity activity = getActivity();
		if (activity != null) {
			/* It should filter itself. */
			String packageName = getActivity().getPackageName();
			List<AppListItem> appsFiltered = new ArrayList<AppListItem>();
			for (AppListItem app : apps) {
				if (TextUtils.equals(packageName, app.getPackageName())) {
					continue;
				}
				appsFiltered.add(app);
			}
			View itemV;
			NetworkImageView logoIv;
			TextView appNameTv;
			Button appBtn;
			mAppListVg.removeAllViews();
			for (final AppListItem item : appsFiltered) {
				itemV =activity.getLayoutInflater().inflate(LAYOUT_APP_ITEM, mAppListVg, false);
				logoIv = (NetworkImageView) itemV.findViewById(R.id.app_logo_iv);
				appNameTv = (TextView) itemV.findViewById(R.id.app_name_tv);
				appBtn = (Button) itemV.findViewById(R.id.start_app_btn);
				logoIv.setDefaultImageResId(R.drawable.ic_action_logo);
				logoIv.setImageUrl(item.getLogoUrl(), TaskHelper.getImageLoader());
				appNameTv.setText(item.getName());
				refreshExternalAppButtonStatus(appBtn, item);
				appBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						EventBus.getDefault().post(new LinkToExternalAppEvent(item));
					}
				});
				mAppButtons.put(item , new WeakReference<Button>(appBtn));
				mAppListVg.addView(itemV);
			}
		}
		mReqInProcess = false;
	}


	/**
	 * Load list of apps.
	 */
	private void loadAppList() {
		Activity activity = getActivity();
		if (activity != null) {
			Application app = activity.getApplication();
			final String urlAppList = Prefs.getInstance(app).getApiAppList();
			if (!TextUtils.isEmpty(urlAppList) && !mReqInProcess) {
				new GsonRequestTask<AppList>(app, Request.Method.GET, urlAppList,
						AppList.class).execute();
				mReqInProcess = true;
			}
		}
	}


	/**
	 * The application status has been changed and handling now.
	 * @param e {@link com.schautup.bus.ExternalAppChangedEvent}
	 */
	private void updateForAppChanged(ExternalAppChangedEvent e) {
		Set<AppListItem> keys = mAppButtons.keySet();
		for(AppListItem key : keys) {
			if (TextUtils.equals(e.getPackageName(), key.getPackageName())) {
				WeakReference<Button> appBtnRef = mAppButtons.get(key);
				if(appBtnRef != null && appBtnRef.get() != null) {
					Button appBtn = appBtnRef.get();
					refreshExternalAppButtonStatus(appBtn, key);
				}
				break;
			}
		}
	}
}

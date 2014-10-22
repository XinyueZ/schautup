package com.schautup.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.schautup.R;
import com.schautup.adapters.InstalledApplicationsListAdapter;
import com.schautup.adapters.InstalledApplicationsListAdapter.ViewHolder;
import com.schautup.bus.SelectedInstalledApplicationEvent;

import de.greenrobot.event.EventBus;

/**
 * Show all installed applications.
 *
 * @author Xinyue Zhao
 */
public final class InstalledApplicationsListDialogFragment extends DialogFragment implements OnItemClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_installed_applications_list_dialog;
	/**
	 * When there's a pre-selected application. When no pre-selection, it is {@code null}.
	 */
	private static final String EXTRAS_PRE_SELECTED_APP = "extras.pre-selected.app";
	/**
	 * All installed application loaded in this {@link android.widget.ListView}.
	 */
	private ListView mLv;

	/**
	 * Initialize an {@link com.schautup.fragments.InstalledApplicationsListDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @param preSelectedApp
	 * 		When there's a pre-selected application. When no pre-selection, it is {@code null}.
	 *
	 * @return An instance of {@link com.schautup.fragments.InstalledApplicationsListDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context, ResolveInfo preSelectedApp) {
		Bundle args = new Bundle();
		args.putParcelable(EXTRAS_PRE_SELECTED_APP, preSelectedApp);
		return (DialogFragment) Fragment.instantiate(context, InstalledApplicationsListDialogFragment.class.getName(),
				args);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLv = (ListView) view.findViewById(R.id.installed_applications_lv);
		mLv.setOnItemClickListener(this);
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> pkgAppsList = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
		ResolveInfo app = getExtrasPreSelectedApp();
		if(app != null) {
			ArrayList<ResolveInfo> newList = new ArrayList<ResolveInfo>();
			for(ResolveInfo a : pkgAppsList) {
				if(TextUtils.equals(a.activityInfo.packageName, app.activityInfo.packageName)) {
					newList.add(app);
					break;
				}
			}
			for(ResolveInfo a : pkgAppsList) {
				if(!TextUtils.equals(a.activityInfo.packageName, app.activityInfo.packageName) &&
						!TextUtils.equals(a.activityInfo.packageName, getActivity().getPackageName())) {
					newList.add(a);
				}
			}
			mLv.setAdapter(new InstalledApplicationsListAdapter(newList, app));
		} else {
			mLv.setAdapter(new InstalledApplicationsListAdapter(pkgAppsList, app));
		}
	}

	/**
	 * Helper method and quick access pre-selection.
	 * @return The package-name of pre-selected application, it might be {@code null}.
	 */
	private ResolveInfo getExtrasPreSelectedApp() {
		return getArguments().getParcelable(EXTRAS_PRE_SELECTED_APP);
	}

	/**
	 * Callback method to be invoked when an item in this AdapterView has been clicked.
	 * <p/>
	 * Implementers can call getItemAtPosition(position) if they need to access the data associated with the selected
	 * item.
	 *
	 * @param parent
	 * 		The AdapterView where the click happened.
	 * @param view
	 * 		The view within the AdapterView that was clicked (this will be a view provided by the adapter)
	 * @param position
	 * 		The position of the view in the adapter.
	 * @param id
	 * 		The row id of the item that was clicked.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder vh = (ViewHolder) view.getTag();
		EventBus.getDefault().post(new SelectedInstalledApplicationEvent(vh.mResolveInfo));
		dismiss();
	}
}

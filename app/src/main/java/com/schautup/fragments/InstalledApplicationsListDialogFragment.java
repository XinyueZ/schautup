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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.schautup.R;
import com.schautup.adapters.InstalledApplicationsListAdapter;
import com.schautup.adapters.InstalledApplicationsListAdapter.ViewHolder;
import com.schautup.bus.SelectedInstalledApplicationEvent;

import de.greenrobot.event.EventBus;

/**
 * Show all installed applications and user can select one.
 *
 * @author Xinyue Zhao
 */
public final class InstalledApplicationsListDialogFragment extends DialogFragment implements OnItemSelectedListener,
		OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_installed_applications_list_dialog;
	/**
	 * When there's a pre-selected application. When no pre-selection, it is {@code null}.
	 */
	private static final String EXTRAS_PRE_SELECTED_APP = "extras.pre-selected.app";
	/**
	 * All installed application loaded in this {@link android.widget.Spinner}.
	 */
	private Spinner mSp;
	/**
	 * The selected application.
	 */
	private ResolveInfo mSelectedInfo;

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
		setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mSp = (Spinner) view.findViewById(R.id.installed_application_sp);
		mSp.setOnItemSelectedListener(this);
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> pkgAppsList = getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
		ResolveInfo app = getExtrasPreSelectedApp();
		ArrayList<ResolveInfo> newList = new ArrayList<ResolveInfo>();
		//Remove SchautUp and remember the one that might have been selected before.
		int posRemember = -1;
		int i = 0;
		for(ResolveInfo a : pkgAppsList) {
			if(!TextUtils.equals(a.activityInfo.packageName, getActivity().getPackageName())) {
				newList.add(a);
				if(app != null && TextUtils.equals(app.activityInfo.packageName, a.activityInfo.packageName)) {
					posRemember = i;
				}
				i++;
			}
		}
		mSp.setAdapter(new InstalledApplicationsListAdapter(newList));
		if(posRemember >= 0) {
			mSp.setSelection(posRemember);
		}
		view.findViewById(R.id.close_cancel_btn).setOnClickListener(this);
		view.findViewById(R.id.close_confirm_btn).setOnClickListener(this);
	}

	/**
	 * Helper method and quick access pre-selection.
	 * @return The package-name of pre-selected application, it might be {@code null}.
	 */
	private ResolveInfo getExtrasPreSelectedApp() {
		return getArguments().getParcelable(EXTRAS_PRE_SELECTED_APP);
	}

	/**
	 * <p>Callback method to be invoked when an item in this view has been selected. This callback is invoked only when
	 * the newly selected position is different from the previously selected position or if there was no selected
	 * item.</p>
	 * <p/>
	 * Impelmenters can call getItemAtPosition(position) if they need to access the data associated with the selected
	 * item.
	 *
	 * @param parent
	 * 		The AdapterView where the selection happened
	 * @param view
	 * 		The view within the AdapterView that was clicked
	 * @param position
	 * 		The position of the view in the adapter
	 * @param id
	 * 		The row id of the item that is selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder vh = (ViewHolder) view.getTag();
		mSelectedInfo = vh.mResolveInfo;
	}

	/**
	 * Callback method to be invoked when the selection disappears from this view. The selection can disappear for
	 * instance when touch is activated or when the adapter becomes empty.
	 *
	 * @param parent
	 * 		The AdapterView that now contains no selected item.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v
	 * 		The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_confirm_btn:
			EventBus.getDefault().post(new SelectedInstalledApplicationEvent(mSelectedInfo));
			dismiss();
			break;
		case R.id.close_cancel_btn:
			EventBus.getDefault().post(new SelectedInstalledApplicationEvent(null));
			dismiss();
			break;
		}
	}
}

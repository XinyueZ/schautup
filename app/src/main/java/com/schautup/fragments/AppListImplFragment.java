package com.schautup.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.AppListFragment;
import com.schautup.utils.Prefs;

/**
 * Impl. The {@link com.chopping.fragments.AppListFragment}.
 *
 * @author Xinyue Zhao
 */
public final class AppListImplFragment extends AppListFragment{
	/**
	 * Initialize an {@link com.schautup.fragments.AppListImplFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link com.schautup.fragments.AppListImplFragment}.
	 */
	public static Fragment newInstance(Context context) {
		return   Fragment.instantiate(context, AppListImplFragment.class.getName());
	}

	/**
	 * App that use this Chopping should know the preference-storage.
	 *
	 * @return An instance of {@link com.chopping.application.BasicPrefs}.
	 */
	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getActivity().getApplication());
	}
}

package com.schautup.fragments;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.schautup.BaseActivity;
import com.schautup.R;

import de.greenrobot.event.EventBus;

/**
 * {@link com.schautup.fragments.BaseFragment} for all fragments except dialogs.
 *
 * @author Xinyue Zhao
 */
public abstract  class BaseFragment extends Fragment {
	/**
	 * Height of {@link android.support.v7.app.ActionBar}.
	 */
	private int mActionBarHeight;
	/**
	 * App {@link android.support.v7.app.ActionBar}.
	 */
	private ActionBar mActionBar;

	/**
	 * Handler for {@link }
	 *
	 * @param e
	 * 		Event {@link  }.
	 */
	public void onEvent(Object e) {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = activity.obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);

		mActionBar = ((BaseActivity) activity).getSupportActionBar();
	}

	@Override
	public void onResume() {
		EventBus.getDefault().register(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	/**
	 * Get height of {@link android.support.v7.app.ActionBar}.
	 */
	protected int getActionBarHeight() {
		return mActionBarHeight;
	}

	/**
	 * Get app {@link android.support.v7.app.ActionBar}.
	 */
	public ActionBar getSupportActionBar() {
		return mActionBar;
	}
}

package com.schautup.activities;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import com.chopping.application.BasicPrefs;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.schautup.R;
import com.schautup.fragments.AboutDialogFragment;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;
import com.schautup.utils.uihelper.SystemUiHelper;

/**
 * System basic {@link android.app.Activity}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseActivity extends com.chopping.activities.BaseActivity {
	/**
	 * Height of {@link android.support.v7.app.ActionBar}.
	 */
	private int mActionBarHeight;

	/**
	 * The uiHelper classes from <a href="https://gist.github.com/chrisbanes/73de18faffca571f7292">Chris Banes</a>
	 */
	private SystemUiHelper mSystemUiHelper;
	/**
	 * Handler for {@link }
	 *
	 * @param e
	 * 		Event {@link  }.
	 */
	public void onEvent(Object e) {

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mSystemUiHelper.hide();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setErrorHandlerAvailable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionBarHeight = Utils.getActionBarHeight(this);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception _e) {
			_e.printStackTrace();
		}

		mSystemUiHelper = new SystemUiHelper(this, SystemUiHelper.LEVEL_IMMERSIVE, 0);
		mSystemUiHelper.hide();
	}


	@Override
	public void onResume() {
		super.onResume();
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isFound == ConnectionResult.SUCCESS ||
				isFound == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if (!Prefs.getInstance(getApplication()).isEULAOnceConfirmed()) {
				showDialogFragment(AboutDialogFragment.EulaConfirmationDialog.newInstance(this), null);
			}
		}else {
			new AlertDialog.Builder(this).setTitle(R.string.application_name).setMessage(R.string.lbl_play_service)
					.setCancelable(false).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(getString(R.string.play_service_url)));
					startActivity(intent);
					finish();
				}
			}).create().show();
		}
	}



	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link
	 * 		android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment(DialogFragment dlgFrg, String tagName) {
		try {
			if (dlgFrg != null) {
				DialogFragment dialogFragment = dlgFrg;
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg");
				if (prev != null) {
					ft.remove(prev);
				}
				try {
					if (TextUtils.isEmpty(tagName)) {
						dialogFragment.show(ft, "dlg");
					} else {
						dialogFragment.show(ft, tagName);
					}
				} catch (Exception _e) {
				}
			}
		} catch (Exception _e) {
		}
	}

	/**
	 * Get height of {@link android.support.v7.app.ActionBar}.
	 */
	protected int getActionBarHeight() {
		return mActionBarHeight;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance(getApplication());
	}


	/**
	 * The uiHelper classes from <a href="https://gist.github.com/chrisbanes/73de18faffca571f7292">Chris Banes</a>
	 */
	protected SystemUiHelper getSystemUiHelper() {
		return mSystemUiHelper;
	}
}

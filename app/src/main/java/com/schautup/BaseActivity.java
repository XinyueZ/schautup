package com.schautup;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

import de.greenrobot.event.EventBus;

/**
 * System basic {@link android.app.Activity}.
 *
 * @author Xinyue Zhao
 */
public abstract  class BaseActivity extends ActionBarActivity {
	/**
	 * Handler for {@link }
	 *
	 * @param e
	 * 		Event {@link  }.
	 */
	public void onEvent(Object e) {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
}

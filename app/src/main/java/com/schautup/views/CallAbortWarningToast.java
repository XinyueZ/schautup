package com.schautup.views;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.schautup.R;
import com.schautup.activities.MainActivity;

/**
 * A customized {@link android.widget.Toast} for warning that all incoming calls are abort.
 *
 * @author Xinyue Zhao
 */
public final class CallAbortWarningToast extends Toast {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT =  R.layout.inc_turn_off_reject_incoming;

	/**
	 * Constructor of {@link com.schautup.views.CallAbortWarningToast}.
	 * @param cxt {@link android.content.Context}.
	 */
	private CallAbortWarningToast(Context cxt) {
		super(cxt);
		setGravity(Gravity.BOTTOM, 0, 0);
		setDuration(Toast.LENGTH_LONG);
		View inflatedV = View.inflate(cxt, LAYOUT, null);
		inflatedV.findViewById(R.id.status_off_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MainActivity.class);
				intent.putExtra(MainActivity.EXTRAS_STOPPED_CALL_ABORT, true);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
		});
		inflatedV.setBackgroundResource(R.color.c16);
		setView(inflatedV);
	}


	/**
	 * Show an instance of {@link com.schautup.views.CallAbortWarningToast}.
	 * @param cxt {@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		new CallAbortWarningToast(cxt).show();
	}
}

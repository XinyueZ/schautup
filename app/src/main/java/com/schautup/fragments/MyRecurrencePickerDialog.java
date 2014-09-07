package com.schautup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.schautup.R;

/**
 * Temp solution to dismiss some unnecessary UI elements on{@link com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog}.
 *
 * @author Xinyue Zhao
 */
public final class MyRecurrencePickerDialog extends RecurrencePickerDialog {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup vp = (ViewGroup) v.findViewById(R.id.repeat_switch).getParent();
		v.findViewById(R.id.repeat_switch).setVisibility(View.GONE);
		v.findViewById(R.id.freqSpinner).setVisibility(View.GONE);
		TextView titleTv = (TextView) inflater.inflate(R.layout.inc_dialog_title, container, false);
		titleTv.setText(R.string.option_recurrence_dlg_title);
		titleTv.setTextColor(getResources().getColor(R.color.text_common_black));
		vp.addView(titleTv);
		v.findViewById(R.id.intervalGroup).setVisibility(View.GONE);
		v.findViewById(R.id.endGroup).setVisibility(View.GONE);
		Button doneBtn = (Button) v.findViewById(R.id.done);
		doneBtn.setText(R.string.btn_ok);
		doneBtn.setEnabled(true);

		((ViewGroup) v).getChildAt(0).getLayoutParams().height = getResources().getDimensionPixelSize(
				R.dimen.recurrence_picker_height);
		return v;
	}

	@Override
	public void updateDialog() {
		super.updateDialog();
		View view = getView();
		if (view != null) {
			Button doneBtn = (Button) view.findViewById(R.id.done);
			doneBtn.setEnabled(true);
		}
	}
}

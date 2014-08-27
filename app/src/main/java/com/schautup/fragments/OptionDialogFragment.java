package com.schautup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.schautup.R;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageTextView;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

/**
 * {@link com.schautup.fragments.OptionDialogFragment} when user clicks "option" {@link android.widget.ImageButton} on
 * list item or user click item direct on {@link com.schautup.fragments.ScheduleGridFragment}.
 *
 * @author Xinyue Zhao
 */
public final class OptionDialogFragment extends DialogFragment implements View.OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_dialog_option;
	/**
	 * {@link android.view.View} represent selection on mute.
	 */
	private View mSelMuteV;
	/**
	 * {@link android.view.View} represent selection on vibration.
	 */
	private View mSelVibrateV;
	/**
	 * {@link android.view.View} represent selection on sound.
	 */
	private View mSelSoundV;
	/**
	 * {@link android.widget.TextView} for selected hour.
	 */
	private TextView mHourTv;
	/**
	 * {@link android.widget.TextView} for selected minute.
	 */
	private TextView mMinuteTv;
	/**
	 * Pointer to selected {@link com.schautup.data.ScheduleType}.
	 */
	private ScheduleType mSelectedType;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.SetTimeEvent}
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.SetTimeEvent}.
	 */
	public void onEvent(SetTimeEvent e) {
		mHourTv.setText(e.getHour());
		mMinuteTv.setText(e.getMinute());
	}
	//------------------------------------------------

	/**
	 * Initialize a {@link com.schautup.fragments.OptionDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @return An instance of {@link com.schautup.fragments.OptionDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, OptionDialogFragment.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Base_AppCompat_Dialog_FixedSize);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		EventBus.getDefault().register(this);
		super.onViewCreated(view, savedInstanceState);
		DateTime now = DateTime.now();
		mHourTv = (TextView) view.findViewById(R.id.sel_hour_tv);
		mHourTv.setOnClickListener(new AnimImageTextView.OnAnimTextViewClickedListener() {
			@Override
			public void onClick() {
				NumberPickerBuilder npb = new NumberPickerBuilder().setPlusMinusVisibility(View.INVISIBLE)
						.setDecimalVisibility(View.INVISIBLE).setFragmentManager(getFragmentManager()).setMinNumber(0)
						.setMaxNumber(23).setStyleResId(R.style.BetterPickersDialogFragment_Light)
						.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
							@Override
							public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
									double fullNumber) {
								mHourTv.setText(Utils.convertValue(number));
							}
						});
				npb.show();
			}
		});
		mHourTv.setText(Utils.convertValue(now.getHourOfDay()));
		mMinuteTv = (TextView) view.findViewById(R.id.sel_minute_tv);
		mMinuteTv.setOnClickListener(new AnimImageTextView.OnAnimTextViewClickedListener() {
			@Override
			public void onClick() {
				NumberPickerBuilder npb = new NumberPickerBuilder().setPlusMinusVisibility(View.INVISIBLE)
						.setDecimalVisibility(View.INVISIBLE).setFragmentManager(getFragmentManager()).setMinNumber(0)
						.setMaxNumber(60).setStyleResId(R.style.BetterPickersDialogFragment_Light)
						.addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
							@Override
							public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative,
									double fullNumber) {
								mMinuteTv.setText(Utils.convertValue(number));
							}
						});
				npb.show();
			}
		});
		mMinuteTv.setText(Utils.convertValue(now.getMinuteOfHour()));
		mSelMuteV = view.findViewById(R.id.set_mute_ll);
		mSelMuteV.setOnClickListener(this);
		mSelVibrateV = view.findViewById(R.id.set_vibrate_ll);
		mSelVibrateV.setOnClickListener(this);
		mSelSoundV = view.findViewById(R.id.set_sound_ll);
		mSelSoundV.setOnClickListener(this);
		view.findViewById(R.id.close_confirm_btn).setOnClickListener(this);
		view.findViewById(R.id.close_cancel_btn).setOnClickListener(this);
		view.findViewById(R.id.open_timepicker_btn).setOnClickListener(
				new AnimImageButton.OnAnimImageButtonClickedListener() {
					@Override
					public void onClick() {
						EventBus.getDefault().post(new OpenTimePickerEvent());
					}
				});
		getDialog().setTitle(R.string.lbl_option_title);
	}

	@Override
	public void onDestroyView() {
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_mute_ll:
			mSelMuteV.setSelected(true);
			mSelVibrateV.setSelected(false);
			mSelSoundV.setSelected(false);
			mSelectedType = ScheduleType.MUTE;
			break;
		case R.id.set_vibrate_ll:
			mSelMuteV.setSelected(false);
			mSelVibrateV.setSelected(true);
			mSelSoundV.setSelected(false);
			mSelectedType = ScheduleType.VIBRATE;
			break;
		case R.id.set_sound_ll:
			mSelMuteV.setSelected(false);
			mSelVibrateV.setSelected(false);
			mSelSoundV.setSelected(true);
			mSelectedType = ScheduleType.SOUND;
			break;
		case R.id.close_confirm_btn:
			if (mSelectedType == null) {
				Utils.showLongToast(getActivity(), R.string.lbl_tip_selection);
				((View) mSelMuteV.getParent()).setSelected(true);
				mSelMuteV.setSelected(false);
				mSelVibrateV.setSelected(false);
				mSelSoundV.setSelected(false);
			} else {
				EventBus.getDefault().post(new UpdateDBEvent(new ScheduleItem(mSelectedType, Integer.valueOf(
						mHourTv.getText().toString()), Integer.valueOf(mMinuteTv.getText().toString()))));
				dismiss();
			}
			break;
		case R.id.close_cancel_btn:
			dismiss();
			break;
		}
	}
}

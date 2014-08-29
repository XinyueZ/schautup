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
import com.schautup.bus.ShowSetOptionEvent;
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
	/**
	 * Selected hour.
	 */
	private int mHour;
	/**
	 * Selected minute.
	 */
	private int mMinute;
	/**
	 * {@code true} if the shown data should only be updated.
	 */
	private boolean mEditMode = false;

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
		mHour = e.getHour();
		mMinute = e.getMinute();
		mHourTv.setText(Utils.convertValue(mHour));
		mMinuteTv.setText(Utils.convertValue(mMinute));
	}

	/**
	 * Handler for {@link com.schautup.bus.ShowSetOptionEvent}
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ShowSetOptionEvent}.
	 */
	public void onEvent(ShowSetOptionEvent e) {
		mEditMode = true;
		mHour = e.getScheduleItem().getHour();
		mMinute =  e.getScheduleItem().getMinute();
		mHourTv.setText(Utils.convertValue(mHour));
		mMinuteTv.setText(Utils.convertValue(mMinute));

		switch (e.getScheduleItem().getType()) {
		case MUTE:
			mSelMuteV.setSelected(true);
			break;
		case VIBRATE:
			mSelVibrateV.setSelected(true);
			break;
		case SOUND:
			mSelSoundV.setSelected(true);
			break;
		}

		EventBus.getDefault().removeStickyEvent(ShowSetOptionEvent.class);
	}

	//------------------------------------------------

	/**
	 * Initialize an {@link com.schautup.fragments.OptionDialogFragment}.
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
		super.onViewCreated(view, savedInstanceState);
		DateTime now = DateTime.now();
		mHour = now.getHourOfDay();
		mMinute = now.getMinuteOfHour();
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
								mHour = number;
							}
						});
				npb.show();
			}
		});
		mHourTv.setText(Utils.convertValue(mHour));
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
								mMinute = number;
							}
						});
				npb.show();
			}
		});
		mMinuteTv.setText(Utils.convertValue(mMinute));
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
						EventBus.getDefault().post(new OpenTimePickerEvent(mHour, mMinute));
					}
				});
		getDialog().setTitle(R.string.lbl_option_title);
	}

	@Override
	public void onResume() {
		EventBus.getDefault().registerSticky(this);
		super.onResume();
	}

	@Override
	public void onPause() {
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
				EventBus.getDefault().post(new UpdateDBEvent(new ScheduleItem(mSelectedType, mHour,
						mMinute), mEditMode));
				dismiss();
			}
			break;
		case R.id.close_cancel_btn:
			dismiss();
			break;
		}
	}
}

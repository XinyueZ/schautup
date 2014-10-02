package com.schautup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.R;
import com.schautup.bus.UpdateFilterEvent;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowSetFilterEvent;
import com.schautup.data.Filter;
import com.schautup.data.ScheduleType;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;
import com.schautup.views.AnimImageTextView;
import com.schautup.views.BadgeView;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

/**
 * By this {@link android.support.v4.app.Fragment} we can define different filters.
 *
 * @author Xinyue Zhao
 */
public final class FiltersDefineDialogFragment extends DialogFragment implements OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_filters_define;
	/**
	 * The id of item if the item was inserted into DB before.
	 */
	private long mId;
	/**
	 * Name of filter.
	 */
	private String mName;
	/**
	 * Selected hour.
	 */
	private int mHour;
	/**
	 * Selected minute.
	 */
	private int mMinute;
	/**
	 * {@link android.widget.TextView} for selected hour.
	 */
	private TextView mHourTv;
	/**
	 * {@link android.widget.TextView} for selected minute.
	 */
	private TextView mMinuteTv;
	/**
	 * {@link android.widget.EditText} for filter's name.
	 */
	private EditText mNameEt;
	/**
	 * Select mute in filter.
	 */
	private View mSetMuteV;
	/**
	 * Select vibrate in filter.
	 */
	private View mSetVibrateV;
	/**
	 * Select sound in filter.
	 */
	private View mSetSoundV;
	/**
	 * Select wifi in filter.
	 */
	private View mSetWifiV;
	/**
	 * Select mobile data in filter.
	 */
	private View mSetMobileDataV;
	/**
	 * Select bluetooth in filter.
	 */
	private View mSetBluetoothV;
	/**
	 * Select call abort(reject incoming) in filter.
	 */
	private View mSetCallAbortV;
	/**
	 * Select start app in filter.
	 */
	private View mSetStartAppV;
	/**
	 * Select brightness in filter.
	 */
	private View mSetBrightnessV;
	/**
	 * The recurrence settings.
	 */
	private EventRecurrence mEventRecurrence;
	/**
	 * Open setting dialog for recurrence.
	 */
	private View mRecurrenceV;
	/**
	 * Information about selected recurrence.
	 */
	private BadgeView mRecurrenceBgv;
	/**
	 * Filter to create.
	 */
	private Filter mFilter = new Filter();
	/**
	 * {@code true} for edit, {@code false} for add new.
	 */
	private boolean mIsEdit;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.SetTimeEvent}.
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
	 * Handler for {@link com.schautup.bus.SetRecurrenceEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.SetRecurrenceEvent}.
	 */
	public void onEvent(SetRecurrenceEvent e) {
		mEventRecurrence = e.getEventRecurrence();
		mRecurrenceV.setSelected(false);

		mEventRecurrence = Utils.showRecurrenceBadge(getActivity(), mEventRecurrence, mRecurrenceBgv);
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowSetFilterEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ShowSetFilterEvent}.
	 */
	public void onEvent(ShowSetFilterEvent e) {
		mIsEdit = true;
		Filter item = e.getFilter();
		mId = item.getId();
		mName = item.getName();
		mHour = item.getHour();
		mMinute = item.getMinute();
		mNameEt.setText(mName);
		mHourTv.setText(Utils.convertValue(mHour));
		mMinuteTv.setText(Utils.convertValue(mMinute));
		SparseArrayCompat<ScheduleType> types= item.getSelectedTypes();
		int key;
		ScheduleType type;
		for(int i = 0; i < types.size(); i++) {
			key = types.keyAt(i);
			type = types.get(key);
			switch (type) {
			case MUTE:
				mSetMuteV.performClick();
				break;
			case VIBRATE:
				mSetVibrateV.performClick();
				break;
			case SOUND:
				mSetSoundV.performClick();
				break;
			case WIFI:
				mSetWifiV.performClick();
				break;
			case MOBILE:
				mSetMobileDataV.performClick();
				break;
			case BRIGHTNESS:
				mSetBrightnessV.performClick();
				break;
			case BLUETOOTH:
				mSetBluetoothV.performClick();
				break;
			case STARTAPP:
				mSetStartAppV.performClick();
				break;
			case CALLABORT:
				mSetCallAbortV.performClick();
				break;
			}
		}
		mEventRecurrence = item.getEventRecurrence();
		mEventRecurrence = Utils.showRecurrenceBadge(getActivity(), mEventRecurrence, mRecurrenceBgv);
		EventBus.getDefault().removeStickyEvent(ShowSetFilterEvent.class);
	}
	//------------------------------------------------

	/**
	 * Initialize an {@link com.schautup.fragments.FiltersDefineDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link com.schautup.fragments.FiltersDefineDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, FiltersDefineDialogFragment.class.getName());
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
		mRecurrenceBgv = (BadgeView) view.findViewById(R.id.recurrence_bgv);
		mEventRecurrence = Utils.showRecurrenceBadge(getActivity(), mEventRecurrence, mRecurrenceBgv);
		mNameEt = (EditText) view.findViewById(R.id.filter_name_et);
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

		mSetMuteV = view.findViewById(R.id.set_mute_ll);
		mSetMuteV.setOnClickListener(this);
		mSetMuteV.setTag(ScheduleType.MUTE);
		mSetVibrateV= view.findViewById(R.id.set_vibrate_ll);
		mSetVibrateV.setOnClickListener(this);
		mSetVibrateV.setTag(ScheduleType.VIBRATE);
		mSetSoundV= view.findViewById(R.id.set_sound_ll);
		mSetSoundV.setOnClickListener(this);
		mSetSoundV.setTag(ScheduleType.SOUND);

		mSetWifiV= view.findViewById(R.id.set_wifi_ll);
		mSetWifiV.setOnClickListener(this);
		mSetWifiV.setTag(ScheduleType.WIFI);
		mSetMobileDataV= view.findViewById(R.id.set_mobile_data_ll);
		mSetMobileDataV.setOnClickListener(this);
		mSetMobileDataV.setTag(ScheduleType.MOBILE);
		mSetBluetoothV= view.findViewById(R.id.set_bluetooth_ll);
		mSetBluetoothV.setOnClickListener(this);
		mSetBluetoothV.setTag(ScheduleType.BLUETOOTH);

		mSetCallAbortV= view.findViewById(R.id.set_call_abort_ll);
		mSetCallAbortV.setOnClickListener(this);
		mSetCallAbortV.setTag(ScheduleType.CALLABORT);
		mSetStartAppV= view.findViewById(R.id.set_start_app_ll);
		mSetStartAppV.setOnClickListener(this);
		mSetStartAppV.setTag(ScheduleType.STARTAPP);
		mSetBrightnessV= view.findViewById(R.id.set_brightness_ll);
		mSetBrightnessV.setOnClickListener(this);
		mSetBrightnessV.setTag(ScheduleType.BRIGHTNESS);

		view.findViewById(R.id.close_cancel_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		view.findViewById(R.id.close_confirm_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mName = mNameEt.getText().toString();
				if(TextUtils.isEmpty(mName)) {
					com.chopping.utils.Utils.showLongToast(getActivity(), R.string.msg_filter_name_must_given);
				} else {
					mFilter.setId(mId);
					mFilter.setName(mName);
					mFilter.setHour(mHour);
					mFilter.setMinute(mMinute);
					mFilter.setEventRecurrence(mEventRecurrence);
					EventBus.getDefault().post(new UpdateFilterEvent(mFilter, mIsEdit));
					dismiss();
				}
			}
		});

		view.findViewById(R.id.open_timepicker_btn).setOnClickListener(
				new AnimImageButton.OnAnimImageButtonClickedListener() {
					@Override
					public void onClick() {
						EventBus.getDefault().post(new OpenTimePickerEvent(mHour, mMinute));
					}
				});
		mRecurrenceV = view.findViewById(R.id.open_recurrence_btn);
		mRecurrenceV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new OpenRecurrencePickerEvent(
						mEventRecurrence == null ? null : mEventRecurrence.toString()));
			}
		});

	}

	@Override
	public void onResume() {
		EventBus.getDefault().registerSticky(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		ViewGroup vp = (ViewGroup) v;
		CheckBox cb = (CheckBox) vp.getChildAt(2);
		cb.setChecked(!cb.isChecked());
		ScheduleType type = (ScheduleType) v.getTag();
		if(!cb.isChecked()) {
			mFilter.getSelectedTypes().delete(type.getCode());
		} else {
			mFilter.getSelectedTypes().put(type.getCode(), type);
		}
	}
}

package com.schautup.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.R;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.data.Level;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;
import com.schautup.db.DB;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;
import com.schautup.views.AnimImageTextView;
import com.schautup.views.BadgeView;

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
	 * {@link android.view.View} represent selection on wifi.
	 */
	private View mSelWifiV;
	/**
	 * {@link android.view.View} represent selection on bluetooth.
	 */
	private View mSelBluetoothV;
	/**
	 * {@link android.view.View} represent selection on mobile network.
	 */
	private View mSelMobileV;
	/**
	 * {@link android.view.View} represent setting on brightness.
	 */
	private View mSelBrightnessV;
	/**
	 * {@link android.widget.TextView} for selected hour.
	 */
	private TextView mHourTv;
	/**
	 * {@link android.widget.TextView} for selected minute.
	 */
	private TextView mMinuteTv;
	/**
	 * The id of item if the item was inserted into DB before.
	 */
	private long mId;
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
	/**
	 * A previous cache of item before user edits.
	 */
	private ScheduleItem mPreScheduleItem;
	/**
	 * The recurrence settings.
	 */
	private EventRecurrence mEventRecurrence;
	/**
	 * Open setting dialog for recurrence.
	 */
	private View mRecurrenceV;
	/**
	 * Parent view for all setting types.
	 */
	private View mSettingTypesLl;
	/**
	 * Information about wifi setting ON/OFF.
	 */
	private BadgeView mWifiInfoBgb;
	/**
	 * Information about bluetooth setting ON/OFF.
	 */
	private BadgeView mBluetoothInfoBgb;
	/**
	 * Information about mobile setting ON/OFF.
	 */
	private BadgeView mMobileInfoBgb;
	/**
	 * Information about setting brightness.
	 */
	private BadgeView mBrightnessInfoBgb;
	/**
	 * Information about selected recurrence.
	 */
	private BadgeView mRecurrenceBgv;

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

		showRecurrenceBadge();
	}

	/**
	 * Handler for {@link com.schautup.bus.ShowSetOptionEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ShowSetOptionEvent}.
	 */
	public void onEvent(ShowSetOptionEvent e) {
		mEditMode = true;
		ScheduleItem item = e.getScheduleItem();
		mPreScheduleItem = item;
		mId = item.getId();
		mHour = item.getHour();
		mMinute = item.getMinute();
		mHourTv.setText(Utils.convertValue(mHour));
		mMinuteTv.setText(Utils.convertValue(mMinute));
		mSelectedType = item.getType();
		mEventRecurrence = item.getEventRecurrence();

		showRecurrenceBadge();

		//Init all values that have been set before.
		switch (item.getType()) {
		case MUTE:
			mSelMuteV.setSelected(true);
			break;
		case VIBRATE:
			mSelVibrateV.setSelected(true);
			break;
		case SOUND:
			mSelSoundV.setSelected(true);
			break;
		case WIFI:
			mSelWifiV.setSelected(true);
			mSelWifiV.setTag(item.getReserveLeft());
			Utils.showBadgeView(getActivity(), mWifiInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
					Boolean.parseBoolean(item.getReserveLeft())));
			break;
		case MOBILE:
			mSelMobileV.setSelected(true);
			mSelMobileV.setTag(item.getReserveLeft());
			Utils.showBadgeView(getActivity(), mMobileInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
					Boolean.parseBoolean(item.getReserveLeft())));
			break;
		case BRIGHTNESS:
			Level l = Level.fromInt(Integer.valueOf(item.getReserveLeft()));
			mSelBrightnessV.setSelected(true);
			mSelBrightnessV.setTag(l);
			Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(l.getLevelShortResId()));
			break;
		case BLUETOOTH:
			mSelBluetoothV.setSelected(true);
			mSelBluetoothV.setTag(item.getReserveLeft());
			Utils.showBadgeView(getActivity(), mBluetoothInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
					Boolean.parseBoolean(item.getReserveLeft())));
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
	 *
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
		mRecurrenceBgv = (BadgeView) view.findViewById(R.id.recurrence_bgv);
		showRecurrenceBadge();

		mSettingTypesLl = view.findViewById(R.id.setting_types_ll);
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
		mSelWifiV = view.findViewById(R.id.set_wifi_ll);
		mSelWifiV.setOnClickListener(this);
		mSelBluetoothV = view.findViewById(R.id.set_bluetooth_ll);
		mSelBluetoothV.setOnClickListener(this);
		mSelMobileV = view.findViewById(R.id.set_mobile_data_ll);
		mSelMobileV.setOnClickListener(this);
		mSelBrightnessV = view.findViewById(R.id.set_brightness_ll);
		mSelBrightnessV.setOnClickListener(this);
		mWifiInfoBgb = (BadgeView) view.findViewById(R.id.info_wifi_bgv);
		mWifiInfoBgb.setVisibility(View.GONE);
		mMobileInfoBgb = (BadgeView) view.findViewById(R.id.info_mobile_data_bgv);
		mMobileInfoBgb.setVisibility(View.GONE);
		mBrightnessInfoBgb = (BadgeView) view.findViewById(R.id.info_brightness_bgv);
		mBrightnessInfoBgb.setVisibility(View.GONE);
		view.findViewById(R.id.close_confirm_btn).setOnClickListener(this);
		view.findViewById(R.id.close_cancel_btn).setOnClickListener(this);
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
		getDialog().setTitle(R.string.option_dlg_title);
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
	public void onDestroyView() {
		super.onDestroyView();
		mSelectedType = null;
		mPreScheduleItem = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_mute_ll:
			mSelMuteV.setSelected(true);
			mSelVibrateV.setSelected(false);
			mSelSoundV.setSelected(false);
			mSelWifiV.setSelected(false);
			mWifiInfoBgb.setVisibility(View.GONE);
			mSelBluetoothV.setSelected(false);
			mBluetoothInfoBgb.setVisibility(View.GONE);
			mSelMobileV.setSelected(false);
			mMobileInfoBgb.setVisibility(View.GONE);
			mSelBrightnessV.setSelected(false);
			mBrightnessInfoBgb.setVisibility(View.GONE);

			mSelectedType = ScheduleType.MUTE;
			break;
		case R.id.set_vibrate_ll:
			mSelVibrateV.setSelected(true);
			mSelMuteV.setSelected(false);
			mSelSoundV.setSelected(false);
			mSelWifiV.setSelected(false);
			mWifiInfoBgb.setVisibility(View.GONE);
			mSelBluetoothV.setSelected(false);
			mBluetoothInfoBgb.setVisibility(View.GONE);
			mSelMobileV.setSelected(false);
			mMobileInfoBgb.setVisibility(View.GONE);
			mSelBrightnessV.setSelected(false);
			mBrightnessInfoBgb.setVisibility(View.GONE);

			mSelectedType = ScheduleType.VIBRATE;
			break;
		case R.id.set_sound_ll:
			mSelSoundV.setSelected(true);
			mSelMuteV.setSelected(false);
			mSelVibrateV.setSelected(false);
			mSelWifiV.setSelected(false);
			mWifiInfoBgb.setVisibility(View.GONE);
			mSelBluetoothV.setSelected(false);
			mBluetoothInfoBgb.setVisibility(View.GONE);
			mSelMobileV.setSelected(false);
			mMobileInfoBgb.setVisibility(View.GONE);
			mSelBrightnessV.setSelected(false);
			mBrightnessInfoBgb.setVisibility(View.GONE);

			mSelectedType = ScheduleType.SOUND;
			break;
		case R.id.set_wifi_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_wifi).setMessage(R.string.msg_wifi_on_off)
					.setCancelable(false).setPositiveButton(R.string.lbl_on, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.WIFI;

					mSelWifiV.setTag(true);
					Utils.showBadgeView(getActivity(), mWifiInfoBgb, Utils.convertBooleanToOnOff(getActivity(), true));
					mSelWifiV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNegativeButton(R.string.lbl_off, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.WIFI;

					mSelWifiV.setTag(false);
					Utils.showBadgeView(getActivity(), mWifiInfoBgb, Utils.convertBooleanToOnOff(getActivity(), false));
					mSelWifiV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNeutralButton(R.string.btn_cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = null;

					mSelWifiV.setTag(null);
					mSelWifiV.setSelected(false);
				}
			}).create().show();
			break;
		case R.id.set_bluetooth_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_bluetooth).setMessage(R.string.msg_bluetooth_on_off)
					.setCancelable(false).setPositiveButton(R.string.lbl_on, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.BLUETOOTH;

					mSelBluetoothV.setTag(true);
					Utils.showBadgeView(getActivity(), mBluetoothInfoBgb, Utils.convertBooleanToOnOff(getActivity(), true));
					mSelBluetoothV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNegativeButton(R.string.lbl_off, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.BLUETOOTH;

					mSelBluetoothV.setTag(false);
					Utils.showBadgeView(getActivity(), mBluetoothInfoBgb, Utils.convertBooleanToOnOff(getActivity(), false));
					mSelBluetoothV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNeutralButton(R.string.btn_cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = null;

					mSelBluetoothV.setTag(null);
					mSelBluetoothV.setSelected(false);
				}
			}).create().show();
			break;
		case R.id.set_mobile_data_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_wifi).setMessage(R.string.msg_wifi_on_off)
					.setCancelable(false).setPositiveButton(R.string.lbl_on, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.MOBILE;

					mSelMobileV.setTag(true);
					Utils.showBadgeView(getActivity(), mMobileInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
							true));
					mSelMobileV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNegativeButton(R.string.lbl_off, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.MOBILE;

					mSelMobileV.setTag(false);
					Utils.showBadgeView(getActivity(), mMobileInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
							false));
					mSelMobileV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelBrightnessV.setSelected(false);
					mBrightnessInfoBgb.setVisibility(View.GONE);
				}
			}).setNeutralButton(R.string.btn_cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = null;

					mSelMobileV.setTag(null);
					mSelMobileV.setSelected(false);
				}
			}).create().show();
			break;
		case R.id.set_brightness_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_brightness).setMessage(
					R.string.msg_brightness).setCancelable(true).setPositiveButton(Level.MAX.getLevelResId(),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mSelectedType = ScheduleType.BRIGHTNESS;

							mSelBrightnessV.setTag(Level.MAX);
							Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MAX.getLevelShortResId()));
							mSelBrightnessV.setSelected(true);

							mSelVibrateV.setSelected(false);
							mSelMuteV.setSelected(false);
							mSelSoundV.setSelected(false);
							mSelWifiV.setSelected(false);
							mWifiInfoBgb.setVisibility(View.GONE);
							mSelBluetoothV.setSelected(false);
							mBluetoothInfoBgb.setVisibility(View.GONE);
							mSelMobileV.setSelected(false);
							mMobileInfoBgb.setVisibility(View.GONE);
						}
					}).setNegativeButton(Level.MIN.getLevelResId(), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.BRIGHTNESS;

					mSelBrightnessV.setTag(Level.MIN);
					Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MIN.getLevelShortResId()));
					mSelBrightnessV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
				}
			}).setNeutralButton(Level.MEDIUM.getLevelResId(), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mSelectedType = ScheduleType.BRIGHTNESS;

					mSelBrightnessV.setTag(Level.MEDIUM);
					Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MEDIUM.getLevelShortResId()));
					mSelBrightnessV.setSelected(true);

					mSelVibrateV.setSelected(false);
					mSelMuteV.setSelected(false);
					mSelSoundV.setSelected(false);
					mSelWifiV.setSelected(false);
					mWifiInfoBgb.setVisibility(View.GONE);
					mSelBluetoothV.setSelected(false);
					mBluetoothInfoBgb.setVisibility(View.GONE);
					mSelMobileV.setSelected(false);
					mMobileInfoBgb.setVisibility(View.GONE);
				}
			}).create().show();
			break;
		case R.id.close_confirm_btn:
			if (mSelectedType == null) {
				//Warning, if has not selected a type to schedule.
				EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_tip_selection),
						getResources().getColor(R.color.warning_green_1)));
				mSettingTypesLl.setSelected(true);
				mSelMuteV.setSelected(false);
				mSelVibrateV.setSelected(false);
				mSelSoundV.setSelected(false);
				mSelWifiV.setSelected(false);
				mSelBluetoothV.setSelected(false);
				mSelMobileV.setSelected(false);
				mSelBrightnessV.setSelected(false);
			} else if (mEventRecurrence == null ||
					((mEventRecurrence.byday == null || mEventRecurrence.byday.length == 0))) {
				//Warning, we must select "repeat".
				EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_tip_recurrence),
						getResources().getColor(R.color.warning_green_1)));
				mRecurrenceV.setSelected(true);
			} else {
				if (mEditMode && mPreScheduleItem != null) {
					//When no change on the item to edit, we don't send event to edit.
					//Close dialog directly.
					if (mPreScheduleItem.getType() == mSelectedType &&
							mPreScheduleItem.getHour() == mHour &&
							mPreScheduleItem.getMinute() == mMinute &&
							mPreScheduleItem.getEventRecurrence().equals(mEventRecurrence)) {
						switch (mSelectedType) {
						case WIFI:
							if (TextUtils.equals(mPreScheduleItem.getReserveLeft(), Utils.toString(
									mSelWifiV.getTag()))) {
								dismiss();
							}
							break;
						case BLUETOOTH:
							if (TextUtils.equals(mPreScheduleItem.getReserveLeft(), Utils.toString(
									mSelBluetoothV.getTag()))) {
								dismiss();
							}
							break;
						case MOBILE:
							if (TextUtils.equals(mPreScheduleItem.getReserveLeft(), Utils.toString(
									mSelMobileV.getTag()))) {
								dismiss();
							}
							break;
						case BRIGHTNESS:
							Level l = Level.fromInt(Integer.valueOf(mPreScheduleItem.getReserveLeft()));
							if (l == mSelBrightnessV.getTag()) {
								dismiss();
							}
							break;
						default:
							dismiss();
							break;
						}
					}
				}

				if (!mEditMode) {
					//Add mode, we should check whether the new item has been in DB or not.
					if (DB.getInstance(getActivity().getApplication()).findDuplicatedItem(mSelectedType, mHour,
							mMinute)) {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_tip_duplicated, getString(
								mSelectedType.getNameResId()), Utils.convertValue(mHour), Utils.convertValue(mMinute)),
								getResources().getColor(R.color.warning_red_1)));
						break;
					}
				} else {
					//Edit mode, we should check whether the "updated" item(different hour,
					//minute or type) has been in DB or not.
					if (mPreScheduleItem.getType() != mSelectedType ||
							mPreScheduleItem.getHour() != mHour ||
							mPreScheduleItem.getMinute() != mMinute) {
						//Any different value that has been set cases to check once on DB.
						if (DB.getInstance(getActivity().getApplication()).findDuplicatedItem(mSelectedType, mHour,
								mMinute)) {
							EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_tip_duplicated,
									getString(mSelectedType.getNameResId()), Utils.convertValue(mHour),
									Utils.convertValue(mMinute)), getResources().getColor(R.color.warning_red_1)));
							break;
						}
					}
				}

				ScheduleItem scheduleItem = new ScheduleItem(mId, mSelectedType, mHour, mMinute);
				scheduleItem.setEventRecurrence(mEventRecurrence);
				//Set some additional information for the selected schedule-type.
				switch (mSelectedType) {
				case WIFI:
					scheduleItem.setReserveLeft(Utils.toString(mSelWifiV.getTag()));
					scheduleItem.setReserveRight("boolean");
					break;
				case BLUETOOTH:
					scheduleItem.setReserveLeft(Utils.toString(mSelBluetoothV.getTag()));
					scheduleItem.setReserveRight("boolean");
					break;
				case MOBILE:
					scheduleItem.setReserveLeft(Utils.toString(mSelMobileV.getTag()));
					scheduleItem.setReserveRight("boolean");
					break;
				case BRIGHTNESS:
					Level level = (Level) mSelBrightnessV.getTag();
					scheduleItem.setReserveLeft(level.toCode() + "");
					scheduleItem.setReserveRight("int");
					break;
				}
				EventBus.getDefault().post(new UpdateDBEvent(scheduleItem, mEditMode));
				dismiss();
			}
			break;
		case R.id.close_cancel_btn:
			dismiss();
			break;
		}
	}

	/**
	 * Show information about pre-selected recurrence.
	 */
	private void showRecurrenceBadge() {
		if (mEventRecurrence != null && mEventRecurrence.byday != null) {
			if (mEventRecurrence.bydayCount > 1) {
				Utils.showBadgeView(getActivity(), mRecurrenceBgv, "..");
			} else {
				int sel = mEventRecurrence.byday[0];
				Utils.showBadgeView(getActivity(), mRecurrenceBgv, Utils.recurrenceDay2String(getActivity(), sel));
			}
		} else {
			String todayInWeek = Utils.dateTimeDay2String(DateTime.now().getDayOfWeek());
			mEventRecurrence = new EventRecurrence();
			mEventRecurrence.parse("FREQ=WEEKLY;WKST=SU;BYDAY=" + todayInWeek);
			int sel = mEventRecurrence.byday[0];
			Utils.showBadgeView(getActivity(), mRecurrenceBgv, Utils.recurrenceDay2String(getActivity(), sel));
		}
	}
}

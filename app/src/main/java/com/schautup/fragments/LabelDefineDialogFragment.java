package com.schautup.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.schautup.R;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.SelectedInstalledApplicationEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowInstalledApplicationsListEvent;
import com.schautup.bus.ShowSetLabelEvent;
import com.schautup.bus.UpdateLabelEvent;
import com.schautup.data.Filter;
import com.schautup.data.Label;
import com.schautup.data.Level;
import com.schautup.data.ScheduleType;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;
import com.schautup.views.AnimImageTextView;
import com.schautup.views.BadgeView;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;

/**
 * By this {@link android.support.v4.app.Fragment} we can define different labels which are filters but before they are
 * fired, they are labels.
 *
 * @author Xinyue Zhao
 */
public final class LabelDefineDialogFragment extends DialogFragment implements OnClickListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_label_define_dialog;
	/**
	 * The id of item if the item was inserted into DB before.
	 */
	private long mId;
	/**
	 * Name of label.
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
	 * {@link android.widget.EditText} for label's name.
	 */
	private EditText mNameEt;
	/**
	 * Select mute in label.
	 */
	private ViewGroup mSetMuteV;
	/**
	 * Select vibrate in label.
	 */
	private ViewGroup mSetVibrateV;
	/**
	 * Select sound in label.
	 */
	private ViewGroup mSetSoundV;
	/**
	 * Select wifi in label.
	 */
	private ViewGroup mSetWifiV;
	/**
	 * Select mobile data in label.
	 */
	private ViewGroup mSetMobileDataV;
	/**
	 * Select bluetooth in label.
	 */
	private ViewGroup mSetBluetoothV;
	/**
	 * Select call abort(reject incoming) in label.
	 */
	private ViewGroup mSetCallAbortV;
	/**
	 * Select start app in label.
	 */
	private ViewGroup mSetStartAppV;
	/**
	 * Select brightness in label.
	 */
	private ViewGroup mSetBrightnessV;
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
	private Filter mLabel = new Filter();
	/**
	 * {@code true} for edit, {@code false} for add new.
	 */
	private boolean mIsEdit;
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
	 * An icon of an application that has been selected.
	 */
	private ImageView mSelectedAppIv;

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
	 * Handler for {@link com.schautup.bus.ShowSetLabelEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ShowSetLabelEvent}.
	 */
	public void onEvent(ShowSetLabelEvent e) {
		mIsEdit = true;
		Filter item = e.getLabel();
		mId = item.getId();
		mName = item.getName();
		mHour = item.getHour();
		mMinute = item.getMinute();
		mNameEt.setText(mName);
		mHourTv.setText(Utils.convertValue(mHour));
		mMinuteTv.setText(Utils.convertValue(mMinute));

		mEventRecurrence = item.getEventRecurrence();
		mEventRecurrence = Utils.showRecurrenceBadge(getActivity(), mEventRecurrence, mRecurrenceBgv);
		EventBus.getDefault().removeStickyEvent(ShowSetLabelEvent.class);

		new ParallelTask<Filter, Filter, Void>(false) {
			@Override
			protected Void doInBackground(Filter... params) {
				Activity activity = getActivity();
				if (activity != null) {
					mLabels = DB.getInstance(activity.getApplication()).getAllLabels(params[0]);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void item) {
				super.onPostExecute(item);

				CheckBox cb;
				for (Label label : mLabels) {
					switch (label.getType()) {
					case MUTE:
						cb = (CheckBox) mSetMuteV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case VIBRATE:
						cb = (CheckBox) mSetVibrateV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case SOUND:
						cb = (CheckBox) mSetSoundV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case WIFI:
						cb = (CheckBox) mSetWifiV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case MOBILE:
						cb = (CheckBox) mSetMobileDataV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case BRIGHTNESS:
						cb = (CheckBox) mSetBrightnessV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case BLUETOOTH:
						cb = (CheckBox) mSetBluetoothV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					case STARTAPP:
						cb = (CheckBox) mSetStartAppV.getChildAt(2);
						cb.setChecked(!cb.isChecked());

						if (cb.isChecked()) {
							final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
							mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							final List<ResolveInfo> pkgAppsList =
									getActivity().getPackageManager().queryIntentActivities(mainIntent, 0);
							PackageManager pm = getActivity().getPackageManager();
							for (ResolveInfo app : pkgAppsList) {
								if (TextUtils.equals(app.activityInfo.packageName, label.getReserveLeft())) {
									cb.setTag(app);

									try {
										String startPackageName = label.getReserveLeft();
										PackageInfo info = pm.getPackageInfo(startPackageName,
												PackageManager.GET_ACTIVITIES);
										Drawable logo = info.applicationInfo.loadIcon(pm);
										if (logo != null) {
											mSelectedAppIv.setImageDrawable(logo);
										}
									} catch (PackageManager.NameNotFoundException ex) {
									}
									break;
								}
							}
						}
						break;
					case CALLABORT:
						cb = (CheckBox) mSetCallAbortV.getChildAt(2);
						cb.setChecked(!cb.isChecked());
						break;
					}
				}
			}
		}.executeParallel(item);
	}

	/**
	 * Handler for {@link com.schautup.bus.SelectedInstalledApplicationEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.SelectedInstalledApplicationEvent}.
	 */
	public void onEvent(SelectedInstalledApplicationEvent e) {
		ResolveInfo info = e.getResolveInfo();
		mSetStartAppV.setTag(info);
		PackageManager pm = getActivity().getPackageManager();
		Drawable logo = info.loadIcon(pm);
		if (logo != null) {
			mSelectedAppIv.setImageDrawable(logo);
		}

		CheckBox cb = (CheckBox) mSetStartAppV.getChildAt(2);
		if (cb.isChecked()) {
			mLabel.getSelectedTypes().put(ScheduleType.STARTAPP.getCode(), ScheduleType.STARTAPP);
			mLabels.add(new Label(ScheduleType.STARTAPP, mHour, mMinute, mEventRecurrence,
					info.activityInfo.packageName, "pkg"));
		}
	}

	//------------------------------------------------

	/**
	 * Initialize an {@link com.schautup.fragments.LabelDefineDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 *
	 * @return An instance of {@link com.schautup.fragments.LabelDefineDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, LabelDefineDialogFragment.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
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
		mNameEt = (EditText) view.findViewById(R.id.label_name_et);
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

		mSetMuteV = (ViewGroup) view.findViewById(R.id.set_mute_ll);
		mSetMuteV.setOnClickListener(this);
		mSetVibrateV = (ViewGroup) view.findViewById(R.id.set_vibrate_ll);
		mSetVibrateV.setOnClickListener(this);
		mSetSoundV = (ViewGroup) view.findViewById(R.id.set_sound_ll);
		mSetSoundV.setOnClickListener(this);

		mSetWifiV = (ViewGroup) view.findViewById(R.id.set_wifi_ll);
		mSetWifiV.setOnClickListener(this);
		mSetMobileDataV = (ViewGroup) view.findViewById(R.id.set_mobile_data_ll);
		mSetMobileDataV.setOnClickListener(this);
		mSetBluetoothV = (ViewGroup) view.findViewById(R.id.set_bluetooth_ll);
		mSetBluetoothV.setOnClickListener(this);

		mSetCallAbortV = (ViewGroup) view.findViewById(R.id.set_call_abort_ll);
		mSetCallAbortV.setOnClickListener(this);
		mSetStartAppV = (ViewGroup) view.findViewById(R.id.set_start_app_ll);
		mSetStartAppV.setOnClickListener(this);
		mSetBrightnessV = (ViewGroup) view.findViewById(R.id.set_brightness_ll);
		mSetBrightnessV.setOnClickListener(this);

		mWifiInfoBgb = (BadgeView) view.findViewById(R.id.info_wifi_bgv);
		mWifiInfoBgb.setVisibility(View.GONE);
		mBluetoothInfoBgb = (BadgeView) view.findViewById(R.id.info_bluetooth_bgv);
		mBluetoothInfoBgb.setVisibility(View.GONE);
		mMobileInfoBgb = (BadgeView) view.findViewById(R.id.info_mobile_data_bgv);
		mMobileInfoBgb.setVisibility(View.GONE);
		mBrightnessInfoBgb = (BadgeView) view.findViewById(R.id.info_brightness_bgv);
		mBrightnessInfoBgb.setVisibility(View.GONE);
		mSelectedAppIv = (ImageView) view.findViewById(R.id.info_start_app_iv);

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
				if (TextUtils.isEmpty(mName)) {
					com.chopping.utils.Utils.showLongToast(getActivity(), R.string.msg_label_name_must_given);
				} else {
					mLabel.setId(mId);
					mLabel.setName(mName);
					mLabel.setHour(mHour);
					mLabel.setMinute(mMinute);
					mLabel.setEventRecurrence(mEventRecurrence);
					mLabel.setLabel(true);
					mLabel.getSelectedTypes().clear();
					for(Label label : mLabels) {
						mLabel.getSelectedTypes().put(label.getType().getCode(), label.getType());
					}
					EventBus.getDefault().post(new UpdateLabelEvent(mLabel, mIsEdit, mLabels));
					dismiss();
				}
			}
		});

		view.findViewById(R.id.open_timepicker_btn).setOnClickListener(new OnAnimImageButtonClickedListener() {
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

		switch (v.getId()) {
		case R.id.set_mute_ll:
			discardAgainst(mSetSoundV, ScheduleType.SOUND);
			discardAgainst(mSetVibrateV,ScheduleType.VIBRATE);
			selectSMVA((ViewGroup) v, ScheduleType.MUTE);
			break;
		case R.id.set_vibrate_ll:
			discardAgainst(mSetSoundV, ScheduleType.SOUND);
			discardAgainst(mSetMuteV, ScheduleType.MUTE);
			selectSMVA((ViewGroup) v, ScheduleType.VIBRATE);
			break;
		case R.id.set_sound_ll:
			discardAgainst(mSetVibrateV, ScheduleType.VIBRATE);
			discardAgainst(mSetMuteV, ScheduleType.MUTE);
			selectSMVA((ViewGroup) v, ScheduleType.SOUND);
			break;
		case R.id.set_call_abort_ll:
			selectSMVA((ViewGroup) v, ScheduleType.CALLABORT);
			break;
		case R.id.set_start_app_ll:
			cb = removeHistory((ViewGroup) v,  ScheduleType.STARTAPP);
			if(cb.isChecked()) {
				ResolveInfo app = (ResolveInfo) cb.getTag();
				EventBus.getDefault().post(new ShowInstalledApplicationsListEvent(app));
			} else {
				mSelectedAppIv.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.set_wifi_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_wifi).setMessage(R.string.msg_wifi_on_off)
					.setCancelable(false).setPositiveButton(R.string.lbl_on, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mWifiInfoBgb, Utils.convertBooleanToOnOff(getActivity(), true));

				}
			}).setNegativeButton(R.string.lbl_off, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mWifiInfoBgb, Utils.convertBooleanToOnOff(getActivity(), false));

				}
			}).setNeutralButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create().show();
			break;
		case R.id.set_bluetooth_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_bluetooth).setMessage(
					R.string.msg_bluetooth_on_off).setCancelable(false).setPositiveButton(R.string.lbl_on,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							//					Utils.showBadgeView(getActivity(), mBluetoothInfoBgb, Utils.convertBooleanToOnOff(getActivity(), true));

						}
					}).setNegativeButton(R.string.lbl_off, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mBluetoothInfoBgb, Utils.convertBooleanToOnOff(getActivity(), false));

				}
			}).setNeutralButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create().show();
			break;
		case R.id.set_mobile_data_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_wifi).setMessage(R.string.msg_wifi_on_off)
					.setCancelable(false).setPositiveButton(R.string.lbl_on, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mMobileInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
					//							true));

				}
			}).setNegativeButton(R.string.lbl_off, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mMobileInfoBgb, Utils.convertBooleanToOnOff(getActivity(),
					//							false));

				}
			}).setNeutralButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).create().show();
			break;
		case R.id.set_brightness_ll:
			new AlertDialog.Builder(getActivity()).setTitle(R.string.option_brightness).setMessage(
					R.string.msg_brightness).setCancelable(true).setPositiveButton(Level.MAX.getLevelResId(),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							//							Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MAX.getLevelShortResId()));

						}
					}).setNegativeButton(Level.MIN.getLevelResId(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MIN.getLevelShortResId()));

				}
			}).setNeutralButton(Level.MEDIUM.getLevelResId(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					//					Utils.showBadgeView(getActivity(), mBrightnessInfoBgb, getString(Level.MEDIUM.getLevelShortResId()));

				}
			}).create().show();
			break;
		}
	}

	/**
	 * The set {@link com.schautup.data.Label}s.
	 */
	private List<Label> mLabels = new ArrayList<Label>();


	/**
	 * Deselect the {@code againstV} when SMV(one of sound, mute, vibrate) is selected.
	 *
	 * @param againstV
	 * 		Other {@link android.view.View} that is not selected.
	 * 	@param type  The type to remove.
	 */
	private void discardAgainst(ViewGroup againstV,  ScheduleType type) {
		CheckBox cb = (CheckBox) againstV.getChildAt(2);
		cb.setChecked(false);
		removeHistory(againstV, type);
	}

	/**
	 * Select a SMV(one of sound, mute, vibrate, abort call).
	 *
	 * @param v
	 * 		{@link android.view.ViewGroup} for SMVA.
	 * 	@param type  The type that user selected.
	 */
	private void selectSMVA(ViewGroup v, ScheduleType type) {
		CheckBox checkBox = removeHistory(v, type);
		//Then re-add.
		if (checkBox.isChecked()) {
			mLabels.add(new Label(type, mHour, mMinute, mEventRecurrence, "", ""));
		}
	}

	/**
	 * Remove the old mark history of an item on {@link android.widget.CheckBox}.
	 * <p/>
	 * Read the {@link android.widget.CheckBox#getTag()} to determine the type that it represents.
	 *
	 * @param v
	 * 		The host of {@link android.widget.CheckBox}.
	 * 	@param type  The type to remove.
	 *
	 * @return {@link android.widget.CheckBox}, represents the item.
	 */
	private CheckBox removeHistory(ViewGroup v, ScheduleType type) {
		CheckBox cb = (CheckBox) v.getChildAt(2);
		//Remove in labels collections.
		for (Label label : mLabels) {
			if (label.getType() == type) {
				mLabels.remove(label);
				break;
			}
		}
		return cb;
	}
}

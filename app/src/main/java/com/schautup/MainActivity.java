package com.schautup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.FindDuplicatedItemEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.RemovedItemEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.ShowActionModeEvent;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.exceptions.AddSameDataException;
import com.schautup.fragments.AboutDialogFragment;
import com.schautup.fragments.OptionDialogFragment;
import com.schautup.fragments.ScheduleGridFragment;
import com.schautup.fragments.ScheduleListFragment;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;

import de.greenrobot.event.EventBus;


/**
 * Main {@link android.support.v7.app.ActionBarActivity} that holds a {@link android.widget.ListView} showing all
 * schedules that control system vibration, sound, mute statuses.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity implements RadialTimePickerDialog.OnTimeSetListener,
		Animation.AnimationListener, android.support.v7.view.ActionMode.Callback {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Main menu.
	 */
	private static final int MENU = R.menu.main;
	/**
	 * Menu for the Action-Mode.
	 */
	private static final int ACTION_MODE_MENU = R.menu.action_mode;
	/**
	 * {@code true} when current view is a list, otherwise is a grid.
	 */
	private boolean mListViewCurrent = true;
	/**
	 * Progress indicator.
	 */
	private SwipeRefreshLayout mRefreshLayout;
	/**
	 * Message sticky.
	 */
	private View mStickyV;
	/**
	 * {@link android.widget.TextView} where message to be shown.
	 */
	private TextView mStickyMsgTv;
	/**
	 * The item that has been selected by the Action Mode.
	 */
	private ScheduleItem mItemSelected;
	/**
	 * {@code true} if current is at the Action Mode
	 */
	private boolean mActionModeOn;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.ShowStickyEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ShowStickyEvent}.
	 */
	public void onEvent(ShowStickyEvent e) {
		if (getSupportActionBar().isShowing()) {
			getSupportActionBar().hide();
		}
		mStickyMsgTv.setText(e.getMessage());
		mStickyV.setVisibility(View.VISIBLE);
		mStickyV.setBackgroundColor(e.getColor());
		AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.slide_in_and_out);
		animSet.setAnimationListener(this);
		mStickyV.startAnimation(animSet);
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowActionBarEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowActionBarEvent }.
	 */
	public void onEvent(ShowActionBarEvent e) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRefreshLayout.getLayoutParams();
		if (e.isShow()) {
			getSupportActionBar().show();
			ViewCompat.setY(mRefreshLayout, getActionBarHeight());
		} else {
			getSupportActionBar().hide();
			ViewCompat.setY(mRefreshLayout, 0);
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.ProgressbarEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.ProgressbarEvent}.
	 */
	public void onEvent(ProgressbarEvent e) {
		mRefreshLayout.setRefreshing(e.isShow());
	}

	/**
	 * Handler for {@link com.schautup.bus.ShowSetOptionEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowSetOptionEvent}.
	 */
	public void onEvent(ShowSetOptionEvent e) {
		if (!mActionModeOn) {
			showDialogFragment(OptionDialogFragment.newInstance(this), null);
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.AddNewScheduleItemEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.AddNewScheduleItemEvent}.
	 */
	public void onEvent(AddNewScheduleItemEvent e) {
		if (!mActionModeOn) {
			showDialogFragment(OptionDialogFragment.newInstance(this), null);
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.OpenTimePickerEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.OpenTimePickerEvent}.
	 */
	public void onEvent(OpenTimePickerEvent e) {
		RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, e.getHour(), e.getMinute(),
				DateFormat.is24HourFormat(this));
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	/**
	 * Handler for {@link com.schautup.bus.UpdateDBEvent}.
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.UpdateDBEvent}.
	 */
	public void onEvent(final UpdateDBEvent e) {
		// Add new item into DB.
		new ParallelTask<Void, Void, Object>(true) {
			private boolean mEditMode = false;
			private ScheduleItem mItem;

			@Override
			protected Object doInBackground(Void... params) {
				mEditMode = e.isEditMode();
				mItem = e.getItem();
				DB db = DB.getInstance(getApplication());
				try {
					if (mEditMode) {
						db.updateSchedule(mItem);
					} else {
						db.addSchedule(mItem);
					}
				} catch (AddSameDataException e1) {
					return e1;
				}
				return db.getAllSchedules();
			}

			@Override
			protected void onPostExecute(Object obj) {
				super.onPostExecute(obj);
				if (obj instanceof AddSameDataException) {
					//Show warning sticky for duplicated updating.
					EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_try_to_add_schedule_fail),
							getResources().getColor(R.color.warning_red_1)));
					//Highlight item that might be duplicated by the updating.
					AddSameDataException exp = (AddSameDataException) obj;
					EventBus.getDefault().post(new FindDuplicatedItemEvent(exp.getDuplicatedItem()));
				} else {
					//Refresh ListView or GridView.
					if (!mEditMode) {
						//Show a tip: long press to remove for first insert.
						Prefs prefs = Prefs.getInstance(getApplication());
						if (!prefs.isTipLongPressRmvShown()) {
							onEvent(new ShowStickyEvent(getString(R.string.msg_long_press_rmv), getResources().getColor(
									R.color.warning_green_1)));
							prefs.setTipLongPressRmvShown(true);
						}
					}
					//It lets UI show warning(green) on the item that has been added or edited.
					EventBus.getDefault().post(new UpdatedItemEvent(mItem));
				}
			}
		}.executeParallel();
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowActionModeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowActionModeEvent}.
	 */
	public void onEvent(ShowActionModeEvent e) {
		mItemSelected = e.getSelectedItem();
		if (!getSupportActionBar().isShowing()) {
			getSupportActionBar().show();
		}
		startSupportActionMode(this);
	}

	//------------------------------------------------

	/**
	 * Show single instance of {@link MainActivity}.
	 *
	 * @param cxt {@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mStickyV = findViewById(R.id.sticky_fl);
		mStickyMsgTv = (TextView) mStickyV.findViewById(R.id.sticky_msg_tv);
		// Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.prg_0, R.color.prg_1, R.color.prg_2, R.color.prg_3);

		// Fragments will load data from DB, here we show the indicator directly.
		mRefreshLayout.setRefreshing(true);

		// Show all saved schedules.
		if (Prefs.getInstance(getApplication()).isLastAListView()) {
			showListView();
		} else {
			showGridView();
		}

		// Move the progress-indicator firstly under the ActionBar.
		ViewCompat.setY(mRefreshLayout, getActionBarHeight());
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().removeStickyEvent(AllScheduleLoadedEvent.class);
		Prefs.getInstance(getApplication()).setLastAListView(mListViewCurrent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		MenuItem menuShare = menu.findItem(R.id.action_share_app);
		//Getting the actionprovider associated with the menu item whose id is share.
		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuShare);
		//Setting a share intent.
		String subject = getString(R.string.lbl_share_app_title);
		String text = getString(R.string.lbl_share_app_content);
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			EventBus.getDefault().post(new AddNewScheduleItemEvent());
			break;
		case R.id.action_view:
			if (!mListViewCurrent) {
				//Current is grid, then switch to list.
				showListView();
			} else {
				showGridView();
			}
			break;
		case R.id.action_settings:
			SettingsActivity.showInstance(this);
			break;
		case R.id.action_sort_by_schedule:
			break;
		case R.id.action_sort_by_creation_time:
			break;
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!mListViewCurrent) {
			//Current is grid, then switch to list late.
			menu.findItem(R.id.action_view).setIcon(R.drawable.ic_action_listview);
		} else {
			menu.findItem(R.id.action_view).setIcon(R.drawable.ic_action_gridview);
		}
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public void onTimeSet(RadialPickerLayout dialog, int hourOfDay, int minute) {
		EventBus.getDefault().post(new SetTimeEvent(hourOfDay, minute));
	}

	/**
	 * Show list view of all schedules.
	 */
	private void showListView() {
		getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_fl,
				ScheduleListFragment.newInstance(this), ScheduleListFragment.class.getName()).commit();
		mListViewCurrent = true;

		ActivityCompat.invalidateOptionsMenu(this);
	}

	/**
	 * Show grid view of all schedules.
	 */
	private void showGridView() {
		getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_fl,
				ScheduleGridFragment.newInstance(this), ScheduleGridFragment.class.getName()).commit();
		mListViewCurrent = false;

		ActivityCompat.invalidateOptionsMenu(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mStickyV.setVisibility(View.GONE);
		getSupportActionBar().show();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(ACTION_MODE_MENU, menu);
		mActionModeOn = true;
		return true;
	}

	@Override
	public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
		if (mItemSelected != null) {
			switch (menuItem.getItemId()) {
			case R.id.action_delete: {
				new ParallelTask<Void, Void, Void>(true) {
					private int mRowsRemain;

					@Override
					protected Void doInBackground(Void... params) {
						mRowsRemain = DB.getInstance(getApplication()).removeSchedule(mItemSelected);
						return null;
					}

					@Override
					protected void onPostExecute(Void _result) {
						super.onPostExecute(_result);
						if (mRowsRemain >= 0) {
							EventBus.getDefault().post(new RemovedItemEvent(mItemSelected, mRowsRemain));
							EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_success),
									getResources().getColor(R.color.warning_green_1)));
						} else {
							EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_fail),
									getResources().getColor(R.color.warning_red_1)));
						}
						actionMode.finish();
					}
				}.executeParallel();
				break;
			}
			default:
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
		actionMode = null;
		mActionModeOn = false;
	}


}

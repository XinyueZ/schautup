package com.schautup.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog.OnRecurrenceSetListener;
import com.schautup.R;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.AskDeleteScheduleItemsEvent;
import com.schautup.bus.DeletedConfirmEvent;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.HideActionModeEvent;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.ShowActionModeEvent;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.fragments.AboutDialogFragment;
import com.schautup.fragments.MyRecurrencePickerDialog;
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
public final class MainActivity extends BaseActivity implements OnTimeSetListener, AnimationListener, Callback,
		OnRecurrenceSetListener {
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
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * {@link android.support.v7.view.ActionMode} on the list-view then it is not null.
	 */
	private ActionMode mActionMode;

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
		if (mActionMode == null) {
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
		if (mActionMode == null) {
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
	 * Handler for {@link com.schautup.bus.OpenRecurrencePickerEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.OpenRecurrencePickerEvent}.
	 */
	public void onEvent(OpenRecurrencePickerEvent e) {
		Bundle b = new Bundle();
		Time t = new Time();
		t.setToNow();
		b.putLong(RecurrencePickerDialog.BUNDLE_START_TIME_MILLIS, t.toMillis(false));
		b.putString(RecurrencePickerDialog.BUNDLE_TIME_ZONE, t.timezone);
		b.putString(RecurrencePickerDialog.BUNDLE_RRULE, e.getRule());
		RecurrencePickerDialog rpd = new MyRecurrencePickerDialog();
		rpd.setArguments(b);
		rpd.setOnRecurrenceSetListener(this);
		rpd.show(getSupportFragmentManager(), null);
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

				boolean success;
				if (mEditMode) {
					success = db.updateSchedule(mItem);
				} else {
					success = db.addSchedule(mItem);
				}
				if (!success) {
					return null;
				} else {
					return db.getAllSchedules();
				}
			}

			@Override
			protected void onPostExecute(Object obj) {
				super.onPostExecute(obj);
				if (obj == null) {
					onEvent(new ShowStickyEvent(getString(R.string.msg_err), getResources().getColor(
							R.color.warning_red_1)));
				} else {
					//Refresh ListView or GridView.
					if (!mEditMode) {
						//Show a tip: long press to remove for first insert.
						Prefs prefs = Prefs.getInstance(getApplication());
						if (!prefs.isTipLongPressRmvScheduleShown()) {
							onEvent(new ShowStickyEvent(getString(R.string.msg_long_press_rmv_schedule),
									getResources().getColor(R.color.warning_green_1)));
							//							Utils.showLongToast(MainActivity.this, R.string.msg_long_press_rmv_schedule);
							prefs.setTipLongPressRmvScheduleShown(true);
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
		if (!getSupportActionBar().isShowing()) {
			getSupportActionBar().show();
		}
		startSupportActionMode(this);
	}

	/**
	 * Handler for {@link com.schautup.bus.GivenRemovedScheduleItemsEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.GivenRemovedScheduleItemsEvent}.
	 */
	public void onEvent(GivenRemovedScheduleItemsEvent e) {
		LongSparseArray<ScheduleItem> items = e.getItems();
		if (mActionMode != null && items != null) {
			new ParallelTask<LongSparseArray<ScheduleItem>, Void, LongSparseArray<ScheduleItem>>(true) {
				@Override
				protected LongSparseArray<ScheduleItem> doInBackground(LongSparseArray<ScheduleItem>... params) {
					if (params.length < 0) {
						return null;
					}
					DB db = DB.getInstance(getApplication());
					long key;
					ScheduleItem item;
					LongSparseArray<ScheduleItem> removedItems = params[0];
					for (int i = 0; removedItems != null && i < removedItems.size(); i++) {
						key = removedItems.keyAt(i);
						item = removedItems.get(key);
						db.removeSchedule(item);
					}
					return removedItems;
				}

				@Override
				protected void onPostExecute(LongSparseArray<ScheduleItem> result) {
					super.onPostExecute(result);
					if (result != null) {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_success),
								getResources().getColor(R.color.warning_green_1)));
						EventBus.getDefault().post(new DeletedConfirmEvent());
					} else {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_fail),
								getResources().getColor(R.color.warning_red_1)));
					}
					mActionMode.finish();
					mActionMode = null;
				}
			}.executeParallel(items);
		}
	}

	//------------------------------------------------

	/**
	 * Show single instance of {@link MainActivity}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
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
		initDrawer();
		//Sticky message box.
		mStickyV = findViewById(R.id.sticky_fl);
		mStickyMsgTv = (TextView) mStickyV.findViewById(R.id.sticky_msg_tv);
		//Progress-indicator.
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_srl);
		mRefreshLayout.setColorSchemeResources(R.color.prg_0, R.color.prg_1, R.color.prg_2, R.color.prg_3);
		//Fragments will load data from DB, here we show the indicator directly.
		mRefreshLayout.setRefreshing(true);
		//Show all saved schedules.
		if (Prefs.getInstance(getApplication()).isLastAListView()) {
			showListView();
		} else {
			showGridView();
		}
		// Move the progress-indicator firstly under the ActionBar.
		ViewCompat.setY(mRefreshLayout, getActionBarHeight());
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mActionMode != null) {
			mActionMode.finish();
		}
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
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
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
		case R.id.action_sort_by_schedule:
		case R.id.action_sort_by_edit_time:
			new ParallelTask<Void, Void, AllScheduleLoadedEvent>(true) {
				@Override
				protected AllScheduleLoadedEvent doInBackground(Void[] params) {
					DB db = DB.getInstance(getApplication());
					List<ScheduleItem> list =
							item.getItemId() == R.id.action_sort_by_schedule ? db.getAllSchedulesOrderByScheduleTime() :
									db.getAllSchedulesOrderByEditTime();
					if (list.size() > 0) {
						return new AllScheduleLoadedEvent(list);
					} else {
						return null;
					}
				}

				@Override
				protected void onPostExecute(AllScheduleLoadedEvent e) {
					super.onPostExecute(e);
					if (e != null) {
						EventBus.getDefault().post(e);
					}
				}
			}.executeParallel();
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

	@Override
	public void onRecurrenceSet(String rrule) {
		EventRecurrence e = new EventRecurrence();
		e.parse(rrule);
		//		Utils.showLongToast(this, e.toString());
		EventBus.getDefault().post(new SetRecurrenceEvent(e));
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
		mActionMode = actionMode;
		return true;
	}

	@Override
	public boolean onPrepareActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(final android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_delete: {
			EventBus.getDefault().post(new AskDeleteScheduleItemsEvent());
			break;
		}
		default:
			return false;
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode) {
		mActionMode = null;
		EventBus.getDefault().post(new HideActionModeEvent());
	}

	/**
	 * Initialize the navigation drawer.
	 */
	private void initDrawer() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.app_name,
					R.string.app_name) {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					if (!getSupportActionBar().isShowing()) {
						getSupportActionBar().show();
					}
				}
			};
			drawerLayout.setDrawerListener(mDrawerToggle);

			findViewById(R.id.drawer_header_v).getLayoutParams().height = getActionBarHeight();

			View drawerItemSettings = findViewById(R.id.drawer_item_settings_ll);
			drawerItemSettings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawers();
					SettingsActivity.showInstance(MainActivity.this);
				}
			});

			View drawerItemHomePage = findViewById(R.id.drawer_item_home_page_ll);
			drawerItemHomePage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawers();
					HomePageWebViewActivity.showInstance(MainActivity.this);
				}
			});

			View drawerItemLogHistory = findViewById(R.id.drawer_item_log_history_ll);
			drawerItemLogHistory.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					drawerLayout.closeDrawers();
					LogHistoryActivity.showInstance(MainActivity.this);
				}
			});
		}
	}
}

package com.schautup.activities;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.chopping.bus.CloseDrawerEvent;
import com.crashlytics.android.Crashlytics;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog.OnRecurrenceSetListener;
import com.github.mrengineer13.snackbar.SnackBar;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.adapters.FiltersAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.AskDeleteScheduleItemsEvent;
import com.schautup.bus.FilterEvent;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.HideActionModeEvent;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.ShowActionModeEvent;
import com.schautup.bus.ShowFilterDefineDialogEvent;
import com.schautup.bus.ShowInstalledApplicationsListEvent;
import com.schautup.bus.ShowLabelDefineDialogEvent;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.bus.UpdateActionBarEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.Filter;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.db.DatabaseHelper;
import com.schautup.fragments.AboutDialogFragment;
import com.schautup.fragments.AppListImplFragment;
import com.schautup.fragments.FilterDefineDialogFragment;
import com.schautup.fragments.InstalledApplicationsListDialogFragment;
import com.schautup.fragments.LabelDefineDialogFragment;
import com.schautup.fragments.MyRecurrencePickerDialog;
import com.schautup.fragments.OptionDialogFragment;
import com.schautup.fragments.ScheduleGridFragment;
import com.schautup.fragments.ScheduleListFragment;
import com.schautup.scheduler.ScheduleManager;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;

import de.greenrobot.event.EventBus;


/**
 * Main {@link android.support.v7.app.ActionBarActivity} that holds a {@link android.widget.ListView} showing all
 * schedules that control system vibration, sound, mute statuses.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity implements OnTimeSetListener, AnimationListener, Callback,
		OnRecurrenceSetListener, OnItemSelectedListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Main menu.
	 */
	private static final int MENU = R.menu.main;

	/**
	 * Extras and flag to indicate that the
	 */
	public static final String EXTRAS_STOPPED_CALL_ABORT = "com.schautup.scheduler.Stop.CallAbort";

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
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;
	/**
	 * {@link android.support.v7.view.ActionMode} on the list-view then it is not null.
	 */
	private ActionMode mActionMode;

	/**
	 * All defined {@link com.schautup.data.Filter}s to select.
	 */
	private Spinner mFilterSpinner;
	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * {@link android.view.MenuItem} to handle list or grid view.
	 */
	private MenuItem mViewMenuItem;
	/**
	 * {@link android.view.View} for "add".
	 */
	private AnimImageButton mAddNewV;

	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;

	/**
	 * Height of statusbar.
	 */
	private int mStatusBarHeight;
	/**
	 * Flag that is {@code true} if the statusbar will show first time.
	 */
	private boolean mFistTimeHide = true;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link CloseDrawerEvent}.
	 *
	 * @param e
	 * 		Event {@link CloseDrawerEvent}.
	 */
	public void onEvent(CloseDrawerEvent e) {
		mDrawerLayout.closeDrawers();
	}

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
		SnackBar sb = new SnackBar(this);
		sb.show(e.getMessage());
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowActionBarEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowActionBarEvent }.
	 */
	public void onEvent(ShowActionBarEvent e) {
		if (e.isShow()) {
			animShowMainUI();
			ViewCompat.setY(mRefreshLayout, getActionBarHeight());
		} else {
			animHideMainUI();
			ViewCompat.setY(mRefreshLayout, 0);
		}
		getSystemUiHelper().hide();
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
			showDialogFragment(OptionDialogFragment.newInstance(getApplication()), null);
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
			showDialogFragment(OptionDialogFragment.newInstance(getApplication()), null);
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
					return Utils.getAllSchedules(getApplication());
				}
			}

			@Override
			protected void onPostExecute(Object obj) {
				super.onPostExecute(obj);
				if (obj == null) {
					onEvent(new ShowStickyEvent(getString(R.string.msg_err) ));
				} else {
					//Refresh ListView or GridView.
					if (!mEditMode) {
						//Show a tip: long press to remove for first insert.
						Prefs prefs = Prefs.getInstance(getApplication());
						if (!prefs.isTipLongPressRmvScheduleShown()) {
							onEvent(new ShowStickyEvent(getString(R.string.msg_long_press_rmv_schedule) ));
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
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_success) ));
					} else {
						EventBus.getDefault().post(new ShowStickyEvent(getString(R.string.msg_rmv_fail) ));
					}
					mActionMode.finish();
					mActionMode = null;
				}
			}.executeParallel(items);
		}
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowFilterDefineDialogEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowFilterDefineDialogEvent}.
	 */
	public void onEvent(ShowFilterDefineDialogEvent e) {
		showDialogFragment(FilterDefineDialogFragment.newInstance(getApplication()), null);
	}


	/**
	 * Handler for {@link com.schautup.bus.ShowLabelDefineDialogEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.ShowLabelDefineDialogEvent}.
	 */
	public void onEvent(ShowLabelDefineDialogEvent e) {
		showDialogFragment(LabelDefineDialogFragment.newInstance(getApplication()), null);
	}

	/**
	 * Handler for {@link com.schautup.bus.FilterEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.FilterEvent}.
	 */
	public void onEvent(FilterEvent e) {
		if (mFiltersAdapter != null && e.isFromDrawer()) {
			for (int i = 0, sz = mFiltersAdapter.getCount(); i < sz; i++) {
				Object spinnterItem = mFiltersAdapter.getItem(i);
				if (spinnterItem instanceof Filter) {
					Filter filterInSpinner = (Filter) spinnterItem;
					if (filterInSpinner.getId() == e.getFilter().getId()) {
						mFromDrawer = true;
						mFilterSpinner.setSelection(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * Handler for {@link com.schautup.bus.UpdateActionBarEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.UpdateActionBarEvent}.
	 */
	public void onEvent(UpdateActionBarEvent e) {
		if (!mListViewCurrent) {
			//Current is grid, then switch to list late.
			mViewMenuItem.setIcon(R.drawable.ic_action_listview);
		} else {
			mViewMenuItem.setIcon(R.drawable.ic_action_gridview);
		}
		makeFilterSpinner();
	}

	/**
	 * Handler for {@link ShowInstalledApplicationsListEvent}.
	 *
	 * @param e
	 * 		Event {@link ShowInstalledApplicationsListEvent}.
	 */
	public void onEvent(ShowInstalledApplicationsListEvent e) {
		ResolveInfo app  = e.getResolveInfo();
		InstalledApplicationsListDialogFragment.newInstance(this, app).show(getSupportFragmentManager(), null);
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
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	/**
	 * To get height of statusbar.
	 */
	private void calcStatusBarHeight() {
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			mStatusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(LAYOUT);
		calcStatusBarHeight();

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setTitle(null);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT  < VERSION_CODES.LOLLIPOP) {
			View decorView = getWindow().getDecorView();
			decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
				@Override
				public void onSystemUiVisibilityChange(int visibility) {
					// Note that system bars will only be "visible" if none of the
					// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
					if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
						// The system bars are visible.
						animToolActionBar(mStatusBarHeight);
					} else {
						if (mFistTimeHide) {
							mFistTimeHide = false;
							return;
						}
						// The system bars are NOT visible.
						animToolActionBar(0);
					}
				}
			});
		}
		initDrawer();
		// Add new.
		mAddNewV = (AnimImageButton) findViewById(R.id.add_btn);
		mAddNewV.setOnClickListener(new AnimImageButton.OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new AddNewScheduleItemEvent());
			}
		});

		// No data.
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

		handleStopAbortIncomings(getIntent());
	}

	private void handleStopAbortIncomings(Intent intent) {
		Application application = getApplication();
		if (intent.getBooleanExtra(EXTRAS_STOPPED_CALL_ABORT, false)) {
			Prefs.getInstance(application).setRejectIncomingCall(false);
			stopService(new Intent(application, ScheduleManager.class));
			startService(new Intent(application, ScheduleManager.class));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleStopAbortIncomings(intent);
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
		String subject = getString(R.string.lbl_share_app_title, getString(R.string.application_name));
		String text = getString(R.string.lbl_share_app_content);
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));

		MenuItem menuFilter = menu.findItem(R.id.action_filter);
		mFilterSpinner = (Spinner) MenuItemCompat.getActionView(menuFilter);
		mNewSpinner = true;
		makeFilterSpinner();

		mViewMenuItem = menu.findItem(R.id.action_view);

		boolean isStage = getResources().getBoolean(R.bool.flag_stage);
		menu.findItem(R.id.action_dump_sqlite).setVisible(isStage);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		//		case R.id.action_add:
		//			EventBus.getDefault().post(new AddNewScheduleItemEvent());
		//			break;
		case R.id.action_view:
			if (!mListViewCurrent) {
				//Current is grid, then switch to list.
				showListView();
			} else {
				showGridView();
			}
			EventBus.getDefault().post(new UpdateActionBarEvent());
			break;
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		case R.id.action_settings:
			SettingsActivity.showInstance(MainActivity.this);
			break;
		case R.id.action_dump_sqlite:
			com.chopping.utils.Utils.dumpSqlite(getApplication(), DatabaseHelper.DATABASE_NAME);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * @param dialog
	 * @param hourOfDay
	 * 		The hour that was set.
	 * @param minute
	 * 		The minute that was set.
	 */
	@Override
	public void onTimeSet(RadialTimePickerDialog dialog, int hourOfDay, int minute) {
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
	}

	/**
	 * Show grid view of all schedules.
	 */
	private void showGridView() {
		getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_fl,
				ScheduleGridFragment.newInstance(this), ScheduleGridFragment.class.getName()).commit();
		mListViewCurrent = false;
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		getSupportActionBar().show();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
		actionMode.getMenuInflater().inflate(ACTION_MODE_MENU, menu);
		mActionMode = actionMode;
		mAddNewV.setVisibility(View.GONE);
		mToolbar.setVisibility(View.GONE);
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
		mAddNewV.setVisibility(View.VISIBLE);
		mToolbar.setVisibility(View.VISIBLE);
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
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.application_name,
					R.string.app_name) {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					animShowMainUI();
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}
	}


	/**
	 * Update {@link android.widget.Spinner} on {@link android.support.v7.app.ActionBar} for {@link
	 * com.schautup.data.Filter}s.
	 */
	private void makeFilterSpinner() {
		new ParallelTask<Void, List<Filter>, List<Filter>>(false) {
			@Override
			protected List<Filter> doInBackground(Void... params) {
				return DB.getInstance(getApplication()).getAllFilters();
			}

			@Override
			protected void onPostExecute(List<Filter> result) {
				super.onPostExecute(result);
				String first = getString(R.string.lbl_filter_selection);
				if (mFiltersAdapter != null && !mNewSpinner) {
					mFiltersAdapter.clear();
					mFiltersAdapter.add(first);
					for (Object o : result) {
						mFiltersAdapter.add(o);
					}
				} else {
					mFiltersAdapter = new FiltersAdapter(getApplicationContext(), R.layout.spinner_filter,
							android.R.id.text1);
					mFiltersAdapter.add(first);
					for (Object o : result) {
						mFiltersAdapter.add(o);
					}
					mFilterSpinner.setAdapter(mFiltersAdapter);
					mNewSpinner = false;
				}
				mFiltersAdapter.setDropDownViewResource(R.layout.spinner_filter_dropdown);
				mFilterSpinner.setOnItemSelectedListener(MainActivity.this);
			}
		}.executeParallel();
	}

	/**
	 * To avoid onItemSelected when {@link android.widget.Spinner} initialized.
	 */
	private boolean mInitSpinner;
	/**
	 * When drawer(filter list) fired a filter, the {@link android.widget.Spinner} must select the correct position to
	 * indicate what was selected from drawer.
	 */
	private boolean mFromDrawer;
	/**
	 * {@link android.widget.Adapter} for {@link com.schautup.data.Filter}'s {@link android.widget.Spinner}.
	 */
	private FiltersAdapter mFiltersAdapter;
	/**
	 * We use this flag to distinguish,unnecessary creation of {@link android.widget.ArrayAdapter} for {@link
	 * android.widget.Spinner} of {@link com.schautup.data.Filter}s.
	 */
	private boolean mNewSpinner;


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, final int location, long arg3) {
		if (mInitSpinner) {
			if (!mFromDrawer) {
				mDrawerLayout.closeDrawers();
				if (location > 0) {
					Filter filter = (Filter) mFilterSpinner.getAdapter().getItem(location);
					EventBus.getDefault().postSticky(new FilterEvent(filter, false));
				} else {
					new ParallelTask<Void, Void, List<ScheduleItem>>(true) {
						@Override
						protected List<ScheduleItem> doInBackground(Void... params) {
							return Utils.getAllSchedules(getApplication());
						}

						@Override
						protected void onPostExecute(List<ScheduleItem> result) {
							super.onPostExecute(result);
							EventBus.getDefault().postSticky(new AllScheduleLoadedEvent(result));
						}
					}.executeParallel();
				}
			} else {
				mFromDrawer = false;
			}
		} else {
			mInitSpinner = true;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		showAppList();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		showAppList();
	}

	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListImplFragment.newInstance(this))
				.commit();
	}

	/**
	 * Dismiss actionbar, and add-new-btn.
	 */
	private void animHideMainUI() {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewV);
		animator.translationY(getActionBarHeight() * 4).setDuration(400);

		animToolActionBar(-getActionBarHeight() * 4);
	}

	/**
	 * Show actionbar, and add-new-btn.
	 */
	private void animShowMainUI() {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mAddNewV);
		animator.translationY(0).setDuration(400);

		animToolActionBar(0);
	}

	/**
	 * Animation and moving actionbar(toolbar).
	 *
	 * @param value
	 * 		The property value of animation.
	 */
	private void animToolActionBar(float value) {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mToolbar);
		animator.translationY(value).setDuration(400);
	}
}

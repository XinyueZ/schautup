package com.schautup.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;
import com.chopping.net.GsonRequestTask;
import com.chopping.net.TaskHelper;
import com.crashlytics.android.Crashlytics;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog.OnTimeSetListener;
import com.doomonafireball.betterpickers.recurrencepicker.EventRecurrence;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog;
import com.doomonafireball.betterpickers.recurrencepicker.RecurrencePickerDialog.OnRecurrenceSetListener;
import com.schautup.R;
import com.schautup.adapters.FiltersAdapter;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.AllScheduleLoadedEvent;
import com.schautup.bus.AskDeleteScheduleItemsEvent;
import com.schautup.bus.DeletedConfirmEvent;
import com.schautup.bus.ExternalAppChangedEvent;
import com.schautup.bus.FilterEvent;
import com.schautup.bus.GivenRemovedScheduleItemsEvent;
import com.schautup.bus.HideActionModeEvent;
import com.schautup.bus.LinkToExternalAppEvent;
import com.schautup.bus.OpenRecurrencePickerEvent;
import com.schautup.bus.OpenTimePickerEvent;
import com.schautup.bus.ProgressbarEvent;
import com.schautup.bus.SetRecurrenceEvent;
import com.schautup.bus.SetTimeEvent;
import com.schautup.bus.ShowActionBarEvent;
import com.schautup.bus.ShowActionModeEvent;
import com.schautup.bus.ShowSetFilterEvent;
import com.schautup.bus.ShowSetOptionEvent;
import com.schautup.bus.ShowStickyEvent;
import com.schautup.bus.UpdateDBEvent;
import com.schautup.bus.UpdateFilterEvent;
import com.schautup.bus.UpdatedItemEvent;
import com.schautup.data.AppList;
import com.schautup.data.AppListItem;
import com.schautup.data.Filter;
import com.schautup.data.ScheduleItem;
import com.schautup.db.DB;
import com.schautup.fragments.AboutDialogFragment;
import com.schautup.fragments.FiltersDefineDialogFragment;
import com.schautup.fragments.MyRecurrencePickerDialog;
import com.schautup.fragments.OptionDialogFragment;
import com.schautup.fragments.ScheduleGridFragment;
import com.schautup.fragments.ScheduleListFragment;
import com.schautup.utils.ParallelTask;
import com.schautup.utils.Prefs;
import com.schautup.utils.Utils;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;
import com.schautup.views.AnimImageTextView;
import com.schautup.views.AnimImageTextView.OnAnimTextViewClickedListener;

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
	 * Layout for an external application to download/install/open.
	 */
	private static final int LAYOUT_APP_ITEM = R.layout.inc_app;
	/**
	 * Layout for a label item.
	 */
	public static final int LAYOUT_LABEL = R.layout.inc_label;
	/**
	 * Layout for a filter item.
	 */
	public static final int LAYOUT_FILTER = R.layout.inc_filter;
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

	/**
	 * List of all stored labels.
	 */
	private ViewGroup mLabelsVg;
	/**
	 * List of all stored filters.
	 */
	private ViewGroup mFiltersVg;
	/**
	 * All labels, for add schedules by a group.
	 */
	private LongSparseArray<ViewGroup> mLabelsList;
	/**
	 * All filters, for user easy to do filtering.
	 */
	private LongSparseArray<Filter> mFiltersList = new LongSparseArray<Filter>();

	/**
	 * All defined {@link com.schautup.data.Filter}s to select.
	 */
	private Spinner mFilterSpinner;
	/**
	 * Navigation drawer.
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * {@link android.view.ViewGroup} for external applications.
	 */
	private ViewGroup mAppListVg;
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
					return Utils.getAllSchedules(getApplication());
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

	/**
	 * Handler for {@link com.schautup.bus.UpdateFilterEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.UpdateFilterEvent}.
	 */
	public void onEvent(UpdateFilterEvent e) {
		  Filter newFilter  = e.getFilter();
		if(!e.isEdit()) {
			new ParallelTask<Filter, Filter, Filter>(false) {
				@Override
				protected Filter doInBackground(Filter... params) {
					DB.getInstance(getApplication()).addFilter(params[0]);
					return params[0];
				}
				@Override
				protected void onPostExecute(Filter filter) {
					super.onPostExecute(filter);
					addedNewFilter(filter);
					makeFilterSpinner();
				}
			}.executeParallel(newFilter);
		} else {
			new ParallelTask<Filter, Filter, Filter>(false) {
				@Override
				protected Filter doInBackground(Filter... params) {
					DB.getInstance(getApplication()).updateFilter(params[0]);
					return params[0];
				}
				@Override
				protected void onPostExecute(Filter filter) {
					super.onPostExecute(filter);
					mFiltersList.get(filter.getId()).clone(filter);
					View hostOfFilterV =   mFiltersVg.findViewById((int) filter.getId());
					if(hostOfFilterV != null) {
						TextView nameTv = (TextView) hostOfFilterV.findViewById(R.id.filter_name_tv);
						nameTv.setText(filter.getName());
					}
					makeFilterSpinner();
				}
			}.executeParallel(newFilter);
		}
	}


	/**
	 * Event, show app-list when they have been loaded.
	 *
	 * @param e
	 * 		{@link com.schautup.App}.
	 */
	public void onEvent(AppList e) {
		showAppList(e.getItems());
	}

	/**
	 * Event, open an external app that has been installed.
	 *
	 * @param e
	 * 		{@link com.schautup.bus.LinkToExternalAppEvent}.
	 */
	public void onEvent(LinkToExternalAppEvent e) {
		com.chopping.utils.Utils.linkToExternalApp(this, e.getAppListItem());
		mDrawerLayout.closeDrawers();
	}


	/**
	 * Event, update list of external apps.
	 *
	 * @param e
	 * 		{@link com.schautup.bus.ExternalAppChangedEvent}.
	 */
	public void onEvent(ExternalAppChangedEvent e) {
		updateForAppChanged(e);
	}

	/**
	 * The application status has been changed and handling now.
	 * @param e {@link ExternalAppChangedEvent}
	 */
	private void updateForAppChanged(ExternalAppChangedEvent e) {
		Set<AppListItem> keys = mAppButtons.keySet();
		for(AppListItem key : keys) {
			if (TextUtils.equals(e.getPackageName(), key.getPackageName())) {
				WeakReference<Button> appBtnRef = mAppButtons.get(e.getPackageName());
				if(appBtnRef != null && appBtnRef.get() != null) {
					Button appBtn = appBtnRef.get();
					refreshExternalAppButtonStatus(appBtn, key);
				}
				break;
			}
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
		Crashlytics.start(this);
		setContentView(LAYOUT);
		mAppListVg = (ViewGroup) findViewById(R.id.drawer_app_list_ll);
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
		String subject = getString(R.string.lbl_share_app_title, getString(R.string.app_name));
		String text = getString(R.string.lbl_share_app_content);
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));

		MenuItem menuFilter  = menu.findItem( R.id.action_filter);
		mFilterSpinner = (Spinner)MenuItemCompat.getActionView(menuFilter);
		mNewSpinner = true;
		makeFilterSpinner();
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
			break;
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
			break;
		case R.id.action_settings:
			SettingsActivity.showInstance(MainActivity.this);
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
		supportInvalidateOptionsMenu();
	}

	/**
	 * Show grid view of all schedules.
	 */
	private void showGridView() {
		getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.content_fl,
				ScheduleGridFragment.newInstance(this), ScheduleGridFragment.class.getName()).commit();
		mListViewCurrent = false;
		supportInvalidateOptionsMenu();
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
		boolean isStage = getResources().getBoolean(R.bool.flag_stage);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
					R.string.app_name) {
				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					super.onDrawerSlide(drawerView, slideOffset);
					if (!getSupportActionBar().isShowing()) {
						getSupportActionBar().show();
					}
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);

			findViewById(R.id.drawer_header_v).getLayoutParams().height = getActionBarHeight();

			View drawerItemSettings = findViewById(R.id.drawer_item_settings_ll);
			drawerItemSettings.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDrawerLayout.closeDrawers();
					SettingsActivity.showInstance(MainActivity.this);
				}
			});

			View drawerItemHomePage = findViewById(R.id.drawer_item_home_page_ll);
			View drawerItemLogHistory = findViewById(R.id.drawer_item_log_history_ll);
			if(isStage) {
				drawerItemHomePage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mDrawerLayout.closeDrawers();
						HomePageWebViewActivity.showInstance(MainActivity.this);
					}
				});

				drawerItemLogHistory.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mDrawerLayout.closeDrawers();
						LogHistoryActivity.showInstance(MainActivity.this);
					}
				});
			} else {
				drawerItemHomePage.setVisibility(View.GONE);
				drawerItemLogHistory.setVisibility(View.GONE);
			}
			mLabelsVg = (ViewGroup) findViewById(R.id.labels_list_ll);
			View addNewLabel =  findViewById(R.id.drawer_item_add_label);
			addNewLabel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(  View v) {

					View newLabelV = getLayoutInflater().inflate(LAYOUT_LABEL, mLabelsVg, false);
					mLabelsVg.addView(newLabelV);
					final AnimImageButton rmvV = (AnimImageButton)newLabelV.findViewById(R.id.label_remove_ibtn);
					rmvV.setOnClickListener(new OnAnimImageButtonClickedListener() {
						@Override
						public void onClick() {
							ViewGroup hostV  = (ViewGroup) rmvV.getParent();
							mLabelsVg.removeView(hostV);
						}
					});
				}
			});

			// Init the list of all filters.
			mFiltersVg  = (ViewGroup) findViewById(R.id.filters_list_ll);
			final AnimImageTextView addNewFilter = (AnimImageTextView)findViewById(R.id.drawer_item_add_filter);
			addNewFilter.setOnClickListener(new OnAnimTextViewClickedListener() {
				@Override
				public void onClick() {
					showDialogFragment(FiltersDefineDialogFragment.newInstance(MainActivity.this), null);
				}
			});
			new ParallelTask<Void, List<Filter>, List<Filter>>(false) {
				@Override
				protected List<Filter> doInBackground(Void... params) {
					return DB.getInstance(getApplication()).getAllFilters();
				}

				@Override
				protected void onPostExecute(List<Filter> result) {
					super.onPostExecute(result);
					if(result != null && result.size() > 0) {
						for(Filter filter : result) {
							addedNewFilter(filter);
						}
					}
				}
			}.executeParallel();
		}
	}

	/**
	 * Helper method to add a new {@link com.schautup.data.Filter}.
	 * @param filter A {@link com.schautup.data.Filter} to show.
	 */
	private void addedNewFilter(final Filter filter) {
		final ViewGroup filterV = (ViewGroup) getLayoutInflater().inflate(LAYOUT_FILTER, mFiltersVg, false);
		filterV.setId((int) filter.getId());
		TextView nameTv = (TextView) filterV.findViewById(R.id.filter_name_tv);
		nameTv.setText(filter.getName());
		final AnimImageButton rmvV = (AnimImageButton) filterV.findViewById(R.id.filter_remove_ibtn);
		rmvV.setTag(filter);
		rmvV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				removeFilter(rmvV);
			}
		});
		AnimImageButton editV = (AnimImageButton) filterV.findViewById(R.id.filter_edit_ibtn);
		editV.setTag(filter);
		editV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().postSticky(new ShowSetFilterEvent(filter));
				showDialogFragment(FiltersDefineDialogFragment.newInstance(MainActivity.this), null);
			}
		});
		AnimImageButton doFilterV = (AnimImageButton) filterV.findViewById(R.id.filter_do_ibtn);
		doFilterV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				mDrawerLayout.closeDrawers();
				mFromDrawer = true;
				EventBus.getDefault().post(new FilterEvent(filter));
				if(mFiltersAdapter != null) {
					for (int i = 0, sz = mFiltersAdapter.getCount(); i < sz; i++) {
						Object spinnterItem = mFiltersAdapter.getItem(i);
						if (spinnterItem instanceof Filter) {
							Filter filterInSpinner = (Filter) spinnterItem;
							if(filterInSpinner.getId() == filter.getId()){
								mFilterSpinner.setSelection(i);
								break;
							}
						}
					}
				}
			}
		});
		mFiltersList.put(filter.getId(), filter);
		mFiltersVg.addView(filterV);
	}


	/**
	 * Helper method to remove a {@link com.schautup.data.Filter}.
	 * @param v The remove button view.
	 */
	private void removeFilter(View v) {
		final Filter oldFilter = (Filter) v.getTag();
		ViewGroup hostV = (ViewGroup) v.getParent();
		mFiltersVg.removeView(hostV);
		mFiltersList.remove(oldFilter.getId());
		new ParallelTask<Void, Void, Void>(false) {
			@Override
			protected Void doInBackground(Void... params) {
				DB.getInstance(getApplication()).removeFilter(oldFilter);
				return null;
			}

			@Override
			protected void onPostExecute(Void _result) {
				super.onPostExecute(_result);
				makeFilterSpinner();
			}
		}.executeParallel();
	}



	/**
	 * Update {@link android.widget.Spinner} on {@link android.support.v7.app.ActionBar} for {@link com.schautup.data.Filter}s.
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
				if(mFiltersAdapter != null && !mNewSpinner ) {
					mFiltersAdapter.clear();
					mFiltersAdapter.add(first);
					for(Object o : result) {
						mFiltersAdapter.add(o);
					}
				} else {
					mFiltersAdapter = new FiltersAdapter(getApplicationContext(), R.layout.spinner_filter, android.R.id.text1 );
					mFiltersAdapter.add(first);
					for(Object o : result) {
						mFiltersAdapter.add(o);
					}
					mFilterSpinner.setAdapter( mFiltersAdapter );
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
	 * When drawer(filter list) fired a filter, the {@link android.widget.Spinner} must select the correct position to indicate what was selected from drawer.
	 */
	private boolean mFromDrawer;
	/**
	 * {@link android.widget.Adapter} for {@link com.schautup.data.Filter}'s {@link android.widget.Spinner}.
	 */
	private FiltersAdapter mFiltersAdapter;
	/**
	 * We use this flag to distinguish,unnecessary creation of {@link android.widget.ArrayAdapter} for {@link android.widget.Spinner} of {@link com.schautup.data.Filter}s.
	 */
	private boolean mNewSpinner;


	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, final int location, long arg3) {
		if(mInitSpinner) {
			if(!mFromDrawer) {
				if (location > 0) {
					Filter filter = (Filter) mFilterSpinner.getAdapter().getItem(location);
					EventBus.getDefault().postSticky(new FilterEvent(filter));
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


	/**
	 * True if a loading app-list request is under way.
	 */
	private boolean mReqInProcess = false;
	/**
	 * {@link android.widget.Button}s of all external applications.
	 * </p>
	 * It is a map of the key that item of application against value {@link android.widget.Button} .
	 */
	private ArrayMap<AppListItem, WeakReference<Button>> mAppButtons = new ArrayMap<AppListItem, WeakReference<Button>>();

	/**
	 * Load list of apps.
	 */
	private void loadAppList() {
		final String urlAppList = Prefs.getInstance(getApplication()).getApiAppList();
		if (!TextUtils.isEmpty(urlAppList) && !mReqInProcess) {
			new GsonRequestTask<AppList>(getApplicationContext(), Request.Method.GET, urlAppList,
					AppList.class).execute();
			mReqInProcess = true;
		}
	}

	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		loadAppList();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		loadAppList();
	}

	/**
	 * Update the status of buttons that can open store linking to the external _app or directly on the _app.
	 *
	 * @param appOpen
	 * 		The button for the app, open, install, or buy.
	 * @param app
	 * 		The data-set represent an external app.
	 */
	private void refreshExternalAppButtonStatus(final Button appOpen, final AppListItem app) {
		Resources res = getResources();
		if (com.chopping.utils.Utils.isAppInstalled(app.getPackageName(), getPackageManager())) {
			appOpen.setText(R.string.extapp_open);
			appOpen.setTextColor(res.getColor(R.color.installed_text));
			appOpen.setBackgroundResource(R.drawable.selector_intstalled_app_item_btn_color);
		} else {
			appOpen.setText(app.getFree() ? R.string.extapp_download : R.string.extapp_buy);
			appOpen.setTextColor(res.getColor(R.color.not_installed_text));
			appOpen.setBackgroundResource(R.drawable.selector_not_intstalled_app_item_btn_color);
		}
	}

	/**
	 * Show list of all external applications.
	 *
	 * @param apps
	 * 		The array of all external applications.
	 */
	private void showAppList(AppListItem[] apps) {
		/* It should filter itself. */
		String packageName = getPackageName();
		List<AppListItem> appsFiltered = new ArrayList<AppListItem>();
		for (AppListItem app : apps) {
			if (TextUtils.equals(packageName, app.getPackageName())) {
				continue;
			}
			appsFiltered.add(app);
		}
		View itemV;
		NetworkImageView logoIv;
		TextView appNameTv;
		Button appBtn;
		mAppListVg.removeAllViews();
		for (final AppListItem item : appsFiltered) {
			itemV = getLayoutInflater().inflate(LAYOUT_APP_ITEM, mAppListVg, false);
			logoIv = (NetworkImageView) itemV.findViewById(R.id.app_logo_iv);
			appNameTv = (TextView) itemV.findViewById(R.id.app_name_tv);
			appBtn = (Button) itemV.findViewById(R.id.start_app_btn);
			logoIv.setDefaultImageResId(R.drawable.ic_action_logo);
			logoIv.setImageUrl(item.getLogoUrl(), TaskHelper.getImageLoader());
			appNameTv.setText(item.getName());
			refreshExternalAppButtonStatus(appBtn, item);
			appBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					EventBus.getDefault().post(new LinkToExternalAppEvent(item));
				}
			});
			mAppButtons.put(item , new WeakReference<Button>(appBtn));
			mAppListVg.addView(itemV);
		}
		mReqInProcess = false;
	}
}

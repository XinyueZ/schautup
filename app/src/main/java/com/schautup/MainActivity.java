package com.schautup;

import java.util.ArrayList;
import java.util.List;

import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.schautup.adapters.ScheduleListViewAdapter;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;

import org.joda.time.DateTime;


/**
 * Main {@link android.support.v7.app.ActionBarActivity} that holds a {@link android.widget.ListView} showing all
 * schedules that control system vibration, sound, mute statuses.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends ActionBarActivity implements AbsListView.OnScrollListener,
		ScheduleListViewAdapter.OnOptionClickedListener, RadialTimePickerDialog.OnTimeSetListener {
	private static final int LAYOUT = R.layout.activity_main;
	private static final int LAYOUT_HEADER = R.layout.inc_lv_header;
	private static final int MENU = R.menu.main;
	/**
	 * Height of {@link android.support.v7.app.ActionBar}.
	 */
	private int mActionBarHeight;
	/**
	 * {@link android.widget.ListView} for all schedules.
	 */
	private ListView mScheduleLv;
	/**
	 * {@link com.schautup.adapters.ScheduleListViewAdapter} for {@link #mScheduleLv}.
	 */
	private ScheduleListViewAdapter mAdapter;
	/**
	 * Helper value to detect scroll direction of {@link android.widget.ListView} {@link #mScheduleLv}.
	 */
	private int mLastFirstVisibleItem;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);

		//Actionbar height.
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);

		// List header.
		mScheduleLv = (ListView) findViewById(R.id.schedule_lv);
		mScheduleLv.setOnScrollListener(this);
		View headerV = getLayoutInflater().inflate(LAYOUT_HEADER, mScheduleLv, false);
		mScheduleLv.addHeaderView(headerV, null, false);
		headerV.getLayoutParams().height = mActionBarHeight;

		//----------------------------------------------------------
		// Description: Test data block.
		//
		// Will be removed late.
		//----------------------------------------------------------
		List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		for (int i = 0; i < 100; i++) {
			items.add(new ScheduleItem(ScheduleType.MUTE, i, i, System.currentTimeMillis()));
		}
		//----------------------------------------------------------

		// Show data.
		mAdapter = new ScheduleListViewAdapter(items);
		mScheduleLv.setAdapter(mAdapter);
		mAdapter.setListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sort_by_schedule:
			break;
		case R.id.action_sort_by_creation_time:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		final ListView lw = (ListView) view;
		if (view.getId() == lw.getId()) {
			final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
			if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				if (getSupportActionBar().isShowing()) {
					getSupportActionBar().hide();
				}
			} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				if (!getSupportActionBar().isShowing()) {
					getSupportActionBar().show();
				}
			}
			mLastFirstVisibleItem = currentFirstVisibleItem;
		}
	}


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onOptionClicked(ScheduleItem item) {
		DateTime now = DateTime.now();
		RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog
				.newInstance(this, now.getHourOfDay(), now.getMinuteOfHour(),
						DateFormat.is24HourFormat(this));
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	@Override
	public void onTimeSet(RadialPickerLayout dialog, int hourOfDay, int minute) {

	}
}

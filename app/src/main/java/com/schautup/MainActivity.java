package com.schautup;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.schautup.adapters.ScheduleListViewAdapter;
import com.schautup.data.ScheduleItem;
import com.schautup.data.ScheduleType;


/**
 * Main {@link android.support.v7.app.ActionBarActivity} that holds a {@link android.widget.ListView} showing all
 * schedules that control system vibration, sound, mute statuses.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends ActionBarActivity {
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * {@link android.widget.ListView} for all schedules.
	 */
	private ListView mScheduleLv;
	/**
	 * {@link com.schautup.adapters.ScheduleListViewAdapter} for {@link #mScheduleLv}.
	 */
	private ScheduleListViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mScheduleLv = (ListView) findViewById(R.id.schedule_lv);
		ScheduleItem item0 = new ScheduleItem(ScheduleType.MUTE, 2, 4, System.currentTimeMillis());
		ScheduleItem item1 = new ScheduleItem(ScheduleType.MUTE, 23, 34, System.currentTimeMillis());
		ScheduleItem item2 = new ScheduleItem(ScheduleType.MUTE, 21, 44, System.currentTimeMillis());
		List<ScheduleItem> items = new ArrayList<ScheduleItem>();
		items.add(item0);
		items.add(item1);
		items.add(item2);
		mAdapter = new ScheduleListViewAdapter(items);
		mScheduleLv.setAdapter(mAdapter);
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

	public static final int MENU = R.menu.main;
}

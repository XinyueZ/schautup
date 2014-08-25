package com.schautup;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.schautup.bus.AddNewScheduleItemEvent;
import com.schautup.bus.SetOptionEvent;
import com.schautup.data.ScheduleItem;
import com.schautup.fragments.ScheduleListFragment;

import org.joda.time.DateTime;


/**
 * Main {@link android.support.v7.app.ActionBarActivity} that holds a {@link android.widget.ListView} showing all
 * schedules that control system vibration, sound, mute statuses.
 *
 * @author Xinyue Zhao
 */
public final class MainActivity extends BaseActivity implements RadialTimePickerDialog.OnTimeSetListener {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Main menu.
	 */
	private static final int MENU = R.menu.main;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.SetOptionEvent}
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.SetOptionEvent}.
	 */
	public void onEvent(SetOptionEvent e) {
		openOptionDialog(e.getScheduleItem());
	}

	/**
	 * Handler for {@link com.schautup.bus.AddNewScheduleItemEvent}
	 *
	 * @param e
	 * 		Event {@link  com.schautup.bus.AddNewScheduleItemEvent}.
	 */
	public void onEvent(AddNewScheduleItemEvent e) {
		Utils.showShortToast(this, "AddNewScheduleItemEvent");
	}
	//------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);

		getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.content_fl,
				ScheduleListFragment.newInstance(this)).commit();
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

	/**
	 * Open a dialog set properties on a {@link com.schautup.data.ScheduleItem}.
	 *
	 * @param item
	 * 		{@link com.schautup.data.ScheduleItem} to be set.
	 */
	public void openOptionDialog(ScheduleItem item) {
		DateTime now = DateTime.now();
		RadialTimePickerDialog timePickerDialog = RadialTimePickerDialog.newInstance(this, now.getHourOfDay(),
				now.getMinuteOfHour(), DateFormat.is24HourFormat(this));
		timePickerDialog.show(getSupportFragmentManager(), null);
	}

	@Override
	public void onTimeSet(RadialPickerLayout dialog, int hourOfDay, int minute) {

	}
}

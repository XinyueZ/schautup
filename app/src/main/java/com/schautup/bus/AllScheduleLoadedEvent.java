package com.schautup.bus;

import java.util.List;

import com.schautup.data.ScheduleItem;

/**
 * Event when all schedules have been loaded.
 *
 * @author Xinyue Zhao
 */
public final class AllScheduleLoadedEvent {
	/**
	 * All loaded {@link com.schautup.data.ScheduleItem}.
	 */
	private List<ScheduleItem> mScheduleItemList;

	/**
	 * Constructor of {@link com.schautup.bus.AllScheduleLoadedEvent}.
	 *
	 * @param scheduleItemList
	 * 		All loaded {@link com.schautup.data.ScheduleItem}.
	 */
	public AllScheduleLoadedEvent(List<ScheduleItem> scheduleItemList) {
		mScheduleItemList = scheduleItemList;
	}

	/**
	 * Get all loaded {@link com.schautup.data.ScheduleItem}.
	 */
	public List<ScheduleItem> getScheduleItemList() {
		return mScheduleItemList;
	}
}

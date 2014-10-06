package com.schautup.data;

import com.google.gson.annotations.SerializedName;

public final class AppList {
	@SerializedName("apps")
	private AppListItem[] mItems;

	public AppList(AppListItem[] _items) {
		mItems = _items;
	}

	public AppListItem[] getItems() {
		return mItems;
	}
}

package com.schautup.data;

import com.chopping.application.IApp;
import com.google.gson.annotations.SerializedName;

public final class AppListItem implements IApp{
	@SerializedName("name")
	private String mName;
	@SerializedName("free")
	private boolean mFree;
	@SerializedName("packageName")
	private String mPackageName;
	@SerializedName("logo_url")
	private String mLogoUrl;
	@SerializedName("playstore_url")
	private String mPlaystoreUrl;

	public AppListItem(String _name, boolean _free, String _packageName, String _logoUrl, String _playstoreUrl) {
		mName = _name;
		mFree = _free;
		mPackageName = _packageName;
		mLogoUrl = _logoUrl;
		mPlaystoreUrl = _playstoreUrl;
	}

	public String getName() {
		return mName;
	}

	public boolean getFree() {
		return mFree;
	}

	public String getPackageName() {
		return mPackageName;
	}

	/**
	 * Location of the application to download.
	 *
	 * @return The url in string for the application.
	 */
	@Override
	public String getStoreUrl() {
		return getPlaystoreUrl();
	}

	public String getLogoUrl() {
		return mLogoUrl;
	}

	public String getPlaystoreUrl() {
		return mPlaystoreUrl;
	}
}

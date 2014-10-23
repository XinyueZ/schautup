package com.schautup.adapters;

import java.util.List;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;

/**
 * {@link android.widget.Adapter} for all installed application list.
 *
 * @author Xinyue Zhao
 */
public final class InstalledApplicationsListAdapter extends BaseAdapter {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_installed_applications_lv;
	/**
	 * {@link java.util.List} of all installed applications.
	 */
	private List<ResolveInfo> mInsApps;

	/**
	 * Constructor of {@link com.schautup.adapters.InstalledApplicationsListAdapter}.
	 *
	 * @param insApps
	 * 		{@link java.util.List} of all installed applications.
	 */
	public InstalledApplicationsListAdapter(List<ResolveInfo> insApps ) {
		mInsApps = insApps;
	}

	/**
	 * How many items are in the data set represented by this Adapter.
	 *
	 * @return Count of items.
	 */
	@Override
	public int getCount() {
		return mInsApps == null ? 0 : mInsApps.size();
	}

	/**
	 * Get the data item associated with the specified position in the data set.
	 *
	 * @param position
	 * 		Position of the item whose data we want within the adapter's data set.
	 *
	 * @return The data at the specified position.
	 */
	@Override
	public Object getItem(int position) {
		return mInsApps.get(position);
	}

	/**
	 * Get the row id associated with the specified position in the list.
	 *
	 * @param position
	 * 		The position of the item within the adapter's data set whose row id we want.
	 *
	 * @return The id of the item at the specified position.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data set. You can either create a View
	 * manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView,
	 * ListView...) will apply default layout parameters unless you use {@link android.view.LayoutInflater#inflate(int,
	 * android.view.ViewGroup, boolean)} to specify a root view and to prevent attachment to the root.
	 *
	 * @param position
	 * 		The position of the item within the adapter's data set of the item whose view we want.
	 * @param convertView
	 * 		The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate
	 * 		type before using. If it is not possible to convert this view to display the correct data, this method can
	 * 		create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of
	 * 		the right type (see {@link #getViewTypeCount()} and {@link #getItemViewType(int)}).
	 * @param parent
	 * 		The parent that this view will eventually be attached to
	 *
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResolveInfo a = mInsApps.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(ITEM_LAYOUT, parent, false);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		PackageManager pmg = parent.getContext().getPackageManager();
		viewHolder.mIconIv.setImageDrawable(a.loadIcon(pmg));
		viewHolder.mNameTv.setText(a.loadLabel(pmg));
		viewHolder.mResolveInfo = a;

		return convertView;
	}

	/**
	 * The view-holder pattern.
	 *
	 * @author Xinyue Zhao
	 */
	public static class ViewHolder {
		/**
		 * App icon.
		 */
		ImageView mIconIv;
		/**
		 * App name.
		 */
		TextView mNameTv;

		public ResolveInfo mResolveInfo;
		/**
		 * Constructor of {@link com.schautup.adapters.InstalledApplicationsListAdapter.ViewHolder}.
		 *
		 * @param convertView
		 * 		The parent {@link View}.
		 */
		ViewHolder(View convertView ) {
			mIconIv = (ImageView) convertView.findViewById(R.id.app_icon_iv);
			mNameTv = (TextView) convertView.findViewById(R.id.app_name_tv);
		}
	}
}

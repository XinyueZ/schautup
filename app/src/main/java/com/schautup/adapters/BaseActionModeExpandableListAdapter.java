package com.schautup.adapters;

import java.util.List;

import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chopping.application.LL;
import com.schautup.R;

/**
 * An {@link android.widget.BaseAdapter} for {@link android.widget.ListView}s that can start {@link
 * android.support.v7.view.ActionMode}.
 *
 * @author Xinyue Zhao
 */
public abstract class BaseActionModeExpandableListAdapter<T> extends BaseExpandableListAdapter implements
		OnClickListener {
	/**
	 * Items that will be removed.
	 */
	private LongSparseArray<T> mToRmvItems;
	/**
	 * {@code true} if the the {@link android.widget.ListView} is under the ActionMode;
	 */
	private boolean mActionMode;


	@Override
	public  View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		ViewHolderActionMode vh = (ViewHolderActionMode) convertView.getTag();
		T item = (T) getGroup(groupPosition);
		if (vh.mDeleteCb != null) {
			vh.mDeleteCb.setVisibility(mActionMode ? View.VISIBLE : View.GONE);
			if (!mActionMode) {
				vh.mDeleteCb.setChecked(false);
			} else {
				vh.mDeleteCb.setChecked(mToRmvItems.indexOfKey(getItemKey(item)) > 0);
			}
			vh.mDeleteCb.setTag(item);
			vh.mDeleteCb.setOnClickListener(this);
		} else {
			LL.e("For ActionMode, a checkbox with id: delete_cb must be provided.");
		}
		return convertView;
	}

	/**
	 * The {@link android.widget.ListView} begins in ActionMode.
	 */
	public void actionModeBegin() {
		mToRmvItems = new LongSparseArray<T>();
		mActionMode = true;
		notifyDataSetChanged();
	}

	/**
	 * The {@link android.widget.ListView} ends from ActionMode.
	 */
	public void actionModeEnd() {
		if (mToRmvItems != null) {
			mToRmvItems.clear();
		}
		mToRmvItems = null;
		mActionMode = false;
		notifyDataSetChanged();
	}


	/**
	 * Removed all selected items from cached {@link java.util.List}.
	 *
	 * @return Data items that have been removed from cache. Return {@code null} when no removal happened.
	 */
	public LongSparseArray<T> removeItems() {
		if (mToRmvItems != null) {
			long key = 0;
			T item;
			List<T> ds = getDataSource();
			for (int i = 0; i < mToRmvItems.size(); i++) {
				key = mToRmvItems.keyAt(i);
				item = mToRmvItems.get(key);
				ds.remove(item);
			}
			return mToRmvItems;
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		CompoundButton buttonView = (CompoundButton) v;
		if (mToRmvItems != null) {
			T item = (T) buttonView.getTag();
			if (buttonView.isChecked()) {
				mToRmvItems.put(getItemKey(item), item);
			} else {
				mToRmvItems.remove(getItemKey(item));
			}
		}
	}


	/**
	 * Data-Source for this {@link com.schautup.adapters.BaseActionModeExpandableListAdapter}.
	 *
	 * @return A {@link java.util.List} of {@code T}.
	 */
	protected abstract List<T> getDataSource();

	/**
	 * Get the key of item which will be moved.
	 *
	 * @param item
	 * 		Item that should gives key for {@link #mToRmvItems}.
	 *
	 * @return The key.
	 */
	protected abstract long getItemKey(T item);

	/**
	 * {@code true} when on the {@link android.support.v7.view.ActionMode}.
	 *
	 * @return {@code true} when on the {@link android.support.v7.view.ActionMode}.
	 */
	protected boolean isActionMode() {
		return mActionMode;
	}

	/**
	 * The abstract ViewHolder pattern for list that uses {@link android.support.v7.view.ActionMode}.
	 *
	 * @author Xinyue Zhao
	 */
	protected static class ViewHolderActionMode {
		/**
		 * Check is shown when wanna delete.
		 */
		private CheckBox mDeleteCb;

		/**
		 * Constructor of {@link com.schautup.adapters.BaseActionModeExpandableListAdapter.ViewHolderActionMode}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		protected ViewHolderActionMode(View convertView) {
			mDeleteCb = (CheckBox) convertView.findViewById(R.id.delete_cb);
		}
	}
}

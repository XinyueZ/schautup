package com.schautup.adapters;

import java.util.List;

import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public abstract class BaseActionModeListAdapter<T> extends BaseAdapter implements
		OnClickListener {
	/**
	 * Items that will be removed.
	 */
	private LongSparseArray<T> mToRmvItems;
	/**
	 * {@code true} if the the {@link android.widget.ListView} is under the ActionMode;
	 */
	private boolean mActionMode;

	/**
	 * Let view show a {@link android.widget.CheckBox} for the {@link android.support.v7.view.ActionMode}.
	 * <p/>
	 * <b>Call this before return if override this method.</b>
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
		ViewHolderActionMode vh = (ViewHolderActionMode) convertView.getTag();
		T item = (T) getItem(position);
		if (vh.mDeleteCb != null) {
			vh.mDeleteCb.setVisibility(mActionMode ? View.VISIBLE : View.GONE);
			if(!mActionMode) {
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
	 * Data-Source for this {@link BaseActionModeListAdapter}.
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
		 * Constructor of {@link BaseActionModeListAdapter.ViewHolderActionMode}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		protected ViewHolderActionMode(View convertView) {
			mDeleteCb = (CheckBox) convertView.findViewById(R.id.delete_cb);
		}
	}
}

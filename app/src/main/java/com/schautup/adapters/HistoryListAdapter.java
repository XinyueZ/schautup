package com.schautup.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.data.HistoryItem;
import com.schautup.data.ScheduleType;
import com.schautup.utils.Utils;

/**
 * {@link android.widget.Adapter} for list of all histories.
 *
 * @author Xinyue Zhao
 */
public class HistoryListAdapter extends BaseActionModeExpandableListAdapter<HistoryItem> {
	/**
	 * Group layout.
	 */
	private static final int ITEM_LAYOUT_GROUP = R.layout.item_log_history_lv_group;
	/**
	 * Child layout.
	 */
	private static final int ITEM_LAYOUT_CHILD = R.layout.item_log_history_lv_child;
	/**
	 * Data-source.
	 */
	private List<HistoryItem> mHistoryItems;


	/**
	 * Constructor of {@link HistoryListAdapter}.
	 *
	 * @param historyItems
	 * 		Data-source.
	 */
	public HistoryListAdapter(List<HistoryItem> historyItems) {
		setData(historyItems);
	}

	public void setData(List<HistoryItem> historyItems) {
		mHistoryItems = historyItems;
	}


	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
		final ViewHolderGroup vh;
		Context cxt = parent.getContext();
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT_GROUP, parent, false);
			vh = new ViewHolderGroup(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolderGroup) convertView.getTag();
		}
		HistoryItem item = mHistoryItems.get(groupPosition);
		ScheduleType type = item.getType();
		vh.mStatusIv.setImageResource(type.getIconDrawResId());
		vh.mStatusTv.setText(cxt.getString(type.getNameResId()));
		vh.mLoggedTimeTv.setText(cxt.getString(R.string.lbl_log_at, Utils.convertTimestamps2DateString(
				parent.getContext(), item.getLogTime())));
		vh.mOpenCommentBtn.setVisibility(!TextUtils.isEmpty(item.getComment()) && !isActionMode() ? View.VISIBLE :
				View.GONE);
		vh.mOpenCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandableListView lv = (ExpandableListView) parent;
				if (!lv.isGroupExpanded(groupPosition)) {
					lv.expandGroup(groupPosition);
					lv.setSelectedGroup(groupPosition);
				} else {
					lv.collapseGroup(groupPosition);
				}
			}
		});
		super.getGroupView(groupPosition, isExpanded, convertView, parent);
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final ViewHolderChild vh;
		Context cxt = parent.getContext();
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT_CHILD, parent, false);
			vh = new ViewHolderChild(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolderChild) convertView.getTag();
		}
		HistoryItem item = mHistoryItems.get(groupPosition);
		if (!TextUtils.isEmpty(item.getComment())) {
			vh.mCommentTv.setText(Html.fromHtml(item.getComment()));
		}
		return convertView;
	}

	@Override
	protected List<HistoryItem> getDataSource() {
		return mHistoryItems;
	}

	@Override
	protected long getItemKey(HistoryItem item) {
		return item.getId();
	}


	@Override
	public int getGroupCount() {
		return mHistoryItems != null ? mHistoryItems.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mHistoryItems.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mHistoryItems.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * ViewHolder pattern for group view of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static final class ViewHolderGroup extends ViewHolderActionMode {
		/**
		 * Show symbol of {@link com.schautup.data.ScheduleType}.
		 */
		private ImageView mStatusIv;
		/**
		 * Show name of {@link com.schautup.data.ScheduleType}.
		 */
		private TextView mStatusTv;
		/**
		 * Show logged time.
		 */
		private TextView mLoggedTimeTv;

		/**
		 * To show comment, then click.
		 */
		private Button mOpenCommentBtn;


		/**
		 * Constructor of {@link com.schautup.adapters.HistoryListAdapter.ViewHolderGroup}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		private ViewHolderGroup(android.view.View convertView) {
			super(convertView);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mLoggedTimeTv = (TextView) convertView.findViewById(R.id.log_at_tv);
			mOpenCommentBtn = (Button) convertView.findViewById(R.id.open_comment_btn);
		}
	}

	/**
	 * ViewHolder pattern for child view of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static final class ViewHolderChild {
		/**
		 * Comment .
		 */
		private TextView mCommentTv;

		/**
		 * Constructor of {@link com.schautup.adapters.HistoryListAdapter.ViewHolderChild}.
		 *
		 * @param convertView
		 */
		private ViewHolderChild(View convertView) {
			mCommentTv = (TextView) convertView.findViewById(R.id.comment_tv);
		}
	}

}

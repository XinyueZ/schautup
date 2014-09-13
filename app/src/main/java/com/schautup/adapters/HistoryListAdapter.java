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
public class HistoryListAdapter extends BaseActionModeAdapter<HistoryItem> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_log_history_lv;
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
	public int getCount() {
		return mHistoryItems != null ? mHistoryItems.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mHistoryItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		Context cxt = parent.getContext();
		if (convertView == null) {
			convertView = LayoutInflater.from(cxt).inflate(ITEM_LAYOUT, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		HistoryItem item = mHistoryItems.get(position);
		ScheduleType type = item.getType();
		vh.mStatusIv.setImageResource(type.getIconDrawResId());
		vh.mStatusTv.setText(cxt.getString(type.getNameResId()));
		vh.mLoggedTimeTv.setText(cxt.getString(R.string.lbl_log_at,
				Utils.convertTimestamps2dateString(parent.getContext(), item.getLogTime())));
		vh.mOpenCommentBtn.setVisibility(!TextUtils.isEmpty(item.getComment()) ? View.VISIBLE : View.GONE);
		vh.mOpenCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				vh.mToggled = !vh.mToggled ;
				vh.mDivCommentV.setVisibility(vh.mToggled ? View.VISIBLE : View.GONE);
				vh.mCommentTv.setVisibility(vh.mToggled ? View.VISIBLE : View.GONE);
			}
		});
		if(!TextUtils.isEmpty(item.getComment())) {
			vh.mCommentTv.setText(
					Html.fromHtml(item.getComment()));
			vh.mDivCommentV.setVisibility(vh.mToggled ? View.VISIBLE : View.GONE);
			vh.mCommentTv.setVisibility(vh.mToggled ? View.VISIBLE : View.GONE);
		} else {
			vh.mDivCommentV.setVisibility(View.GONE);
			vh.mCommentTv.setVisibility( View.GONE);
		}

		super.getView(position, convertView, parent);
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

	/**
	 * ViewHolder pattern for the item of history list.
	 *
	 * @author Xinyue Zhao
	 */
	private static class ViewHolder extends ViewHolderActionMode {
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
		 * Comment of history, not necessary  always be shown.
		 */
		private TextView mCommentTv;
		/**
		 * To show comment, then click.
		 */
		private Button mOpenCommentBtn;
		/**
		 * {@code true} if is opened.
		 */
		private boolean mToggled;
		/**
		 * A divide for comment to headline.
		 */
		private View mDivCommentV;

		/**
		 * Constructor of {@link com.schautup.adapters.HistoryListAdapter.ViewHolder}.
		 *
		 * @param convertView
		 * 		The parent view for all.
		 */
		private ViewHolder(View convertView) {
			super(convertView);
			mStatusIv = (ImageView) convertView.findViewById(R.id.status_iv);
			mStatusTv = (TextView) convertView.findViewById(R.id.status_tv);
			mLoggedTimeTv = (TextView) convertView.findViewById(R.id.log_at_tv);
			mCommentTv = (TextView) convertView.findViewById(R.id.comment_tv);
			mOpenCommentBtn = (Button) convertView.findViewById(R.id.open_comment_btn);
			mDivCommentV = convertView.findViewById(R.id.div_comment_v);
		}
	}

}

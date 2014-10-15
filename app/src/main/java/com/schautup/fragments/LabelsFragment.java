package com.schautup.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chopping.bus.CloseDrawerEvent;
import com.schautup.R;
import com.schautup.bus.ShowLabelDefineDialogEvent;
import com.schautup.bus.ShowSetLabelEvent;
import com.schautup.bus.UpdateLabelEvent;
import com.schautup.data.Filter;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;

import de.greenrobot.event.EventBus;

/**
 * {@link android.support.v4.app.Fragment} that holds all labels.
 *
 * @author Xinyue Zhao
 */
public final class LabelsFragment extends BaseFragment{
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_labels;
	/**
	 * Layout for a label item.
	 */
	private static final int LAYOUT_LABEL = R.layout.inc_label;
	/**
	 * All labels.
	 */
	private LongSparseArray<Filter> mLabelsList = new LongSparseArray<Filter>();
	/**
	 * List of all stored labels.
	 */
	private ViewGroup mLabelsVg;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.UpdateFilterEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.UpdateFilterEvent}.
	 */
	public void onEvent(UpdateLabelEvent e) {
		Filter newFilter = e.getLabel();
		if (!e.isEdit()) {
			new ParallelTask<Filter, Filter, Filter>(false) {
				@Override
				protected Filter doInBackground(Filter... params) {
					Activity activity = getActivity();
					if (activity != null) {
						DB.getInstance(activity.getApplication()).addFilter(params[0]);
						return params[0];
					}
					return null;
				}

				@Override
				protected void onPostExecute(Filter filter) {
					super.onPostExecute(filter);
					if (filter != null) {
						addNewLabel(filter);
					}
				}
			}.executeParallel(newFilter);
		} else {
			new ParallelTask<Filter, Filter, Filter>(false) {
				@Override
				protected Filter doInBackground(Filter... params) {
					Activity activity = getActivity();
					if (activity != null) {
						DB.getInstance(activity.getApplication()).updateFilter(params[0]);
						return params[0];
					}
					return null;
				}

				@Override
				protected void onPostExecute(Filter filter) {
					super.onPostExecute(filter);
					if (filter != null) {
						mLabelsList.get(filter.getId()).clone(filter);
						View hostOfFilterV = mLabelsVg.findViewById((int) filter.getId());
						if (hostOfFilterV != null) {
							TextView nameTv = (TextView) hostOfFilterV.findViewById(R.id.label_name_tv);
							nameTv.setText(filter.getName());
						}
					}
				}
			}.executeParallel(newFilter);
		}
	}

	//------------------------------------------------


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mLabelsVg = (ViewGroup) view.findViewById(R.id.labels_list_ll);
		View addNewLabel =  view.findViewById(R.id.drawer_item_add_label);
		addNewLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(  View v) {
				EventBus.getDefault().post(new ShowLabelDefineDialogEvent());
			}
		});
		new ParallelTask<Void, List<Filter>, List<Filter>>(false) {
			@Override
			protected List<Filter> doInBackground(Void... params) {
				Activity activity = getActivity();
				if (activity != null) {
					return DB.getInstance(activity.getApplication()).getAllLabels();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Filter> result) {
				super.onPostExecute(result);
				if (result != null && result.size() > 0) {
					for (Filter filter : result) {
						addNewLabel(filter);
					}
				}
			}
		}.executeParallel();
	}

	/**
	 * Helper method to add a new {@link com.schautup.data.Filter}.
	 *
	 * @param filter
	 * 		A {@link com.schautup.data.Filter} to show.
	 */
	private void addNewLabel(final Filter filter) {
		final ViewGroup labelV = (ViewGroup) getActivity().getLayoutInflater().inflate(LAYOUT_LABEL, mLabelsVg,
				false);
		labelV.setId((int) filter.getId());
		TextView nameTv = (TextView) labelV.findViewById(R.id.label_name_tv);
		nameTv.setText(filter.getName());
		final AnimImageButton rmvV = (AnimImageButton) labelV.findViewById(R.id.label_remove_ibtn);
		rmvV.setTag(filter);
		rmvV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				removeLabel(rmvV);
			}
		});
		AnimImageButton editV = (AnimImageButton) labelV.findViewById(R.id.label_edit_ibtn);
		editV.setTag(filter);
		editV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().postSticky(new ShowSetLabelEvent(filter));
				EventBus.getDefault().post(new ShowLabelDefineDialogEvent());
			}
		});
		AnimImageButton doLabelV = (AnimImageButton) labelV.findViewById(R.id.label_do_ibtn);
		doLabelV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new CloseDrawerEvent());

			}
		});
		mLabelsList.put(filter.getId(), filter);
		mLabelsVg.addView(labelV);
	}


	/**
	 * Helper method to remove a {@link com.schautup.data.Filter}.
	 *
	 * @param v
	 * 		The remove button view.
	 */
	private void removeLabel(View v) {
		final Filter oldLabel = (Filter) v.getTag();
		ViewGroup hostV = (ViewGroup) v.getParent();
		mLabelsVg.removeView(hostV);
		mLabelsList.remove(oldLabel.getId());
		new ParallelTask<Void, Void, Void>(false) {
			@Override
			protected Void doInBackground(Void... params) {
				Activity activity = getActivity();
				if (activity != null) {
					DB.getInstance(activity.getApplication()).removeFilter(oldLabel);
				}
				return null;
			}
		}.executeParallel();
	}
}

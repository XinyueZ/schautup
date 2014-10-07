package com.schautup.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schautup.R;
import com.schautup.bus.CloseDrawerEvent;
import com.schautup.bus.FilterEvent;
import com.schautup.bus.ShowFiltersDefineDialogEvent;
import com.schautup.bus.ShowSetFilterEvent;
import com.schautup.bus.UpdateActionBarEvent;
import com.schautup.bus.UpdateFilterEvent;
import com.schautup.data.Filter;
import com.schautup.db.DB;
import com.schautup.utils.ParallelTask;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;
import com.schautup.views.AnimImageTextView;
import com.schautup.views.AnimImageTextView.OnAnimTextViewClickedListener;

import de.greenrobot.event.EventBus;

/**
 * {@link android.support.v4.app.Fragment} holds all {@link com.schautup.data.Filter}s.
 *
 * @author Xinyue Zhao
 */
public final class FiltersFragment extends BaseFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_filters;
	/**
	 * Layout for a filter item.
	 */
	public static final int LAYOUT_FILTER = R.layout.inc_filter;

	/**
	 * All filters, for user easy to do filtering.
	 */
	private LongSparseArray<Filter> mFiltersList = new LongSparseArray<Filter>();
	/**
	 * List of all stored filters.
	 */
	private ViewGroup mFiltersVg;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.schautup.bus.UpdateFilterEvent}.
	 *
	 * @param e
	 * 		Event {@link com.schautup.bus.UpdateFilterEvent}.
	 */
	public void onEvent(UpdateFilterEvent e) {
		Filter newFilter = e.getFilter();
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
						addedNewFilter(filter);
						EventBus.getDefault().post(new UpdateActionBarEvent());
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
						mFiltersList.get(filter.getId()).clone(filter);
						View hostOfFilterV = mFiltersVg.findViewById((int) filter.getId());
						if (hostOfFilterV != null) {
							TextView nameTv = (TextView) hostOfFilterV.findViewById(R.id.filter_name_tv);
							nameTv.setText(filter.getName());
						}
						EventBus.getDefault().post(new UpdateActionBarEvent());
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

		mFiltersVg = (ViewGroup) view.findViewById(R.id.filters_list_ll);
		final AnimImageTextView addNewFilter = (AnimImageTextView) view.findViewById(R.id.drawer_item_add_filter);
		addNewFilter.setOnClickListener(new OnAnimTextViewClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new ShowFiltersDefineDialogEvent());
			}
		});
		new ParallelTask<Void, List<Filter>, List<Filter>>(false) {
			@Override
			protected List<Filter> doInBackground(Void... params) {
				Activity activity = getActivity();
				if (activity != null) {
					return DB.getInstance(activity.getApplication()).getAllFilters();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Filter> result) {
				super.onPostExecute(result);
				if (result != null && result.size() > 0) {
					for (Filter filter : result) {
						addedNewFilter(filter);
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
	private void addedNewFilter(final Filter filter) {
		final ViewGroup filterV = (ViewGroup) getActivity().getLayoutInflater().inflate(LAYOUT_FILTER, mFiltersVg,
				false);
		filterV.setId((int) filter.getId());
		TextView nameTv = (TextView) filterV.findViewById(R.id.filter_name_tv);
		nameTv.setText(filter.getName());
		final AnimImageButton rmvV = (AnimImageButton) filterV.findViewById(R.id.filter_remove_ibtn);
		rmvV.setTag(filter);
		rmvV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				removeFilter(rmvV);
			}
		});
		AnimImageButton editV = (AnimImageButton) filterV.findViewById(R.id.filter_edit_ibtn);
		editV.setTag(filter);
		editV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().postSticky(new ShowSetFilterEvent(filter));
				EventBus.getDefault().post(new ShowFiltersDefineDialogEvent());
			}
		});
		AnimImageButton doFilterV = (AnimImageButton) filterV.findViewById(R.id.filter_do_ibtn);
		doFilterV.setOnClickListener(new OnAnimImageButtonClickedListener() {
			@Override
			public void onClick() {
				EventBus.getDefault().post(new CloseDrawerEvent());
				EventBus.getDefault().post(new FilterEvent(filter, true));

			}
		});
		mFiltersList.put(filter.getId(), filter);
		mFiltersVg.addView(filterV);
	}


	/**
	 * Helper method to remove a {@link com.schautup.data.Filter}.
	 *
	 * @param v
	 * 		The remove button view.
	 */
	private void removeFilter(View v) {
		final Filter oldFilter = (Filter) v.getTag();
		ViewGroup hostV = (ViewGroup) v.getParent();
		mFiltersVg.removeView(hostV);
		mFiltersList.remove(oldFilter.getId());
		new ParallelTask<Void, Void, Void>(false) {
			@Override
			protected Void doInBackground(Void... params) {
				Activity activity = getActivity();
				if (activity != null) {
					DB.getInstance(activity.getApplication()).removeFilter(oldFilter);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				EventBus.getDefault().post(new UpdateActionBarEvent());
			}
		}.executeParallel();
	}


}

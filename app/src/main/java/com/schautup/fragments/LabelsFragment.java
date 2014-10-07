package com.schautup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.schautup.R;
import com.schautup.views.AnimImageButton;
import com.schautup.views.AnimImageButton.OnAnimImageButtonClickedListener;

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
	 * Layout for a filter item.
	 */
	private static final int LAYOUT_LABEL = R.layout.inc_label;


	/**
	 * List of all stored labels.
	 */
	private ViewGroup mLabelsVg;

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

				View newLabelV = getActivity().getLayoutInflater().inflate(LAYOUT_LABEL, mLabelsVg, false);
				mLabelsVg.addView(newLabelV);
				final AnimImageButton rmvV = (AnimImageButton)newLabelV.findViewById(R.id.label_remove_ibtn);
				rmvV.setOnClickListener(new OnAnimImageButtonClickedListener() {
					@Override
					public void onClick() {
						ViewGroup hostV  = (ViewGroup) rmvV.getParent();
						mLabelsVg.removeView(hostV);
					}
				});
			}
		});
	}
}

package com.schautup.bus;

import java.util.ArrayList;
import java.util.List;

import com.schautup.data.Filter;
import com.schautup.data.Label;

/**
 * Event when a label which is {@link com.schautup.data.Filter#isLabel()}{@code =true} added / edit on {@link
 * com.schautup.db.DB}.
 *
 * @author Xinyue Zhao
 */
public final class UpdateLabelEvent {
	/**
	 * A   {@link com.schautup.data.Filter} to add or edit.
	 */
	private Filter mFilter;
	/**
	 * {@code true} for edit, {@code false} for add new.
	 */
	private boolean mIsEdit;

	/**
	 * The set {@link com.schautup.data.Label}s.
	 */
	private List<Label> mLabels = new ArrayList<Label>();

	/**
	 * Constructor of {@link com.schautup.bus.UpdateLabelEvent}
	 *
	 * @param filter
	 * 		A   {@link com.schautup.data.Filter} to add or edit.
	 * @param isEdit
	 * 		{@code true} for edit, {@code false} for add new.
	 * @param labels
	 * 		 {@link java.util.Collection} of all {@link com.schautup.data.Label}s to this pre-selected {@link com.schautup.data.Filter}.
	 */
	public UpdateLabelEvent(Filter filter, boolean isEdit, List<Label> labels) {
		mFilter = filter;
		mIsEdit = isEdit;
		mLabels = labels;
	}

	/**
	 * Get {@link com.schautup.data.Filter} what has been updated(add new or edited).
	 *
	 * @return A new {@link com.schautup.data.Filter}.
	 */
	public Filter getLabel() {
		return mFilter;
	}

	/**
	 * Is add new or edit.
	 *
	 * @return {@code true} for edit, {@code false} for add new.
	 */
	public boolean isEdit() {
		return mIsEdit;
	}

	/**
	 * The set {@link com.schautup.data.Label}s.
	 */
	public List<Label> getLabels() {
		return mLabels;
	}
}

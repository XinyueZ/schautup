package com.schautup.adapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * An {@link android.widget.Adapter} for {@link android.widget.Spinner} on {@link android.support.v7.app.ActionBar}.
 *
 * @author Xinyue Zhao
 */
public final class FiltersAdapter  extends ArrayAdapter<Object>{
	/**
	 * Constructor
	 *
	 * @param context
	 * 		The current context.
	 * @param resource
	 * 		The resource ID for a layout file containing a layout to use when instantiating views.
	 * @param textViewResourceId
	 * 		The id of the TextView within the layout resource to be populated
	 * @param objects
	 * 		The objects to represent in the ListView.
	 */
	public FiltersAdapter(Context context, int resource, int textViewResourceId, List<Object> objects) {
		super(context, resource, textViewResourceId, objects);
	}
}

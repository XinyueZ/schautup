package com.schautup.adapters;

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
	 */
	public FiltersAdapter(Context context, int resource, int textViewResourceId ) {
		super(context, resource, textViewResourceId );
	}
}

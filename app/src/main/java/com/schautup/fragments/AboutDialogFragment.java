package com.schautup.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.schautup.R;

/**
 * Dialog popup a "About", include legal text, open source licenses etc.
 * <p/>
 * A lot inspired by App <a href="https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples
 * /apps/iosched/util/HelpUtils.java">Google I/O 2014</a>
 *
 * @author Xinyue Zhao
 */
public final class AboutDialogFragment extends DialogFragment {
	/**
	 * Error-handling.
	 */
	private static final String VERSION_UNAVAILABLE = "N/A";

	/**
	 * Initialize an {@link com.schautup.fragments.AboutDialogFragment}.
	 *
	 * @param context
	 * 		A {@link android.content.Context} object.
	 * @return An instance of {@link com.schautup.fragments.AboutDialogFragment}.
	 */
	public static DialogFragment newInstance(Context context) {
		return (DialogFragment) Fragment.instantiate(context, AboutDialogFragment.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get app version
		PackageManager pm = getActivity().getPackageManager();
		String packageName = getActivity().getPackageName();
		String versionName;
		try {
			PackageInfo info = pm.getPackageInfo(packageName, 0);
			versionName = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			versionName = VERSION_UNAVAILABLE;
		}

		// Build the about body view and append the link to see OSS licenses
		SpannableStringBuilder aboutBody = new SpannableStringBuilder();
		aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));

		SpannableString licensesLink = new SpannableString(getString(R.string.about_licenses));
		licensesLink.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View view) {
				showOpenSourceLicenses(getActivity());
			}
		}, 0, licensesLink.length(), 0);
		aboutBody.append("\n\n");
		aboutBody.append(licensesLink);

		//		SpannableString eulaLink = new SpannableString(getString(R.string.about_eula));
		//		eulaLink.setSpan(new ClickableSpan() {
		//			@Override
		//			public void onClick(View view) {
		//				HelpUtils.showEula(getActivity());
		//			}
		//		}, 0, eulaLink.length(), 0);
		//		aboutBody.append("\n\n");
		//		aboutBody.append(eulaLink);

		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
		aboutBodyView.setText(aboutBody);
		aboutBodyView.setMovementMethod(new LinkMovementMethod());

		return new AlertDialog.Builder(getActivity()).setTitle(R.string.lbl_about).setView(aboutBodyView)
				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).create();
	}

	/**
	 * To open showing open source licenses.
	 *
	 * @param activity
	 * 		Host {@link android.support.v4.app.FragmentActivity}.
	 */
	private static void showOpenSourceLicenses(FragmentActivity activity) {
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("dialog_licenses");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		new OpenSourceLicensesDialog().show(ft, "dialog_licenses");
	}

	/**
	 * Dialog to open showing open source licenses.
	 *
	 * @author Xinyue Zhao
	 */
	public static class OpenSourceLicensesDialog extends DialogFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setCancelable(false);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			WebView webView = new WebView(getActivity());
			webView.loadUrl("file:///android_asset/licenses.html");

			return new AlertDialog.Builder(getActivity()).setTitle(R.string.about_licenses).setView(webView)
					.setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
						}
					}).create();
		}
	}
}



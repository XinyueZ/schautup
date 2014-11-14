package com.schautup.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.schautup.R;
import com.schautup.utils.Prefs;
import com.schautup.views.WebViewEx;
import com.schautup.views.WebViewEx.OnWebViewExScrolledListener;

/**
 * A WebView to the homepage of SchautUp.
 *
 * @author Xinyue Zhao
 */
public final class HomePageWebViewActivity extends BaseActivity implements DownloadListener {

	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_home_page_webview;

	/**
	 * The menu to this view.
	 */
	private static final int MENU = R.menu.webview;

	/**
	 * {@link WebView} shows homepage.
	 */
	private WebViewEx mWebView;
	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;
	/**
	 * Show single instance of {@link com.schautup.activities.HomePageWebViewActivity}.
	 *
	 * @param cxt
	 * 		{@link android.content.Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, HomePageWebViewActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mWebView = (WebViewEx) findViewById(R.id.home_wv);
		mWebView.setOnWebViewExScrolledListener(new OnWebViewExScrolledListener() {
			@Override
			public void onScrollChanged(boolean isUp) {
				if(isUp) {
					animToolActionBar(0);
				} else {
					animToolActionBar(-getActionBarHeight() * 4);
				}
			}

			@Override
			public void onScrolledTop() {
				animToolActionBar(-getActionBarHeight() * 4);
			}
		});
		mWebView.setDownloadListener(this);
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		settings.setDomStorageEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
			}

			@Override
			public void onPageFinished(WebView view, String url) {
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.loadUrl(Prefs.getInstance(getApplication()).getAppWebHome());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_forward:
			if(mWebView.canGoForward()) mWebView.goForward();
			break;
		case R.id.action_backward:
			if(mWebView.canGoBack()) mWebView.goBack();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
			long contentLength) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}


	/**
	 * Animation and moving actionbar(toolbar).
	 *
	 * @param value
	 * 		The property value of animation.
	 */
	private void animToolActionBar(float value) {
		ViewPropertyAnimator animator = ViewPropertyAnimator.animate(mToolbar);
		animator.translationY(value).setDuration(400);
	}
}

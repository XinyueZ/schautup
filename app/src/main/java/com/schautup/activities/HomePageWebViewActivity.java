package com.schautup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.schautup.R;

/**
 * A WebView to the homepage of SchautUp.
 *
 * @author Xinyue Zhao
 */
public final class HomePageWebViewActivity extends BaseActivity {

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
	private WebView mWebView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		getSupportActionBar().setIcon(R.drawable.ic_action_home_page);
		mWebView = (WebView) findViewById(R.id.home_wv);
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
		mWebView.loadUrl("http://wanlingzhao.eu.pn/app/schautup/schautup_index.html");
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
}

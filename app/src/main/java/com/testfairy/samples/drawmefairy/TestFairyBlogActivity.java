package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import draw.me.fairy.R;
import utils.ActivityTime;

/**
 * Created by gilt on 12/17/14.
 */
public class TestFairyBlogActivity extends Activity {

	private final String TAG = getClass().getSimpleName();
	private final String BLOG_URL = "http://blog.testfairy.com";
	private ActivityTime activityTime;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate " + TAG);
		setContentView(R.layout.blog);
		WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl(BLOG_URL);


	}

	@Override
	protected void onStart() {
		activityTime = new ActivityTime("Blog");
		super.onStart();
	}

	@Override
	protected void onPause() {
		activityTime.cancel();
		super.onPause();
	}
}
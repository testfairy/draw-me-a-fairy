package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.testfairy.samples.drawmefairy.R;

public class SecretActivity extends Activity {
	private final String TAG = getClass().getSimpleName();

	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secret_activity);
		Log.d(TAG, "onCreate " + TAG);


	}
}
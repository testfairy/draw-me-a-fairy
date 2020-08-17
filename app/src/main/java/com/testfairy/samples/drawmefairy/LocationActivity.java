package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import utils.LocationHelper;

public class LocationActivity extends Activity {

	private final LocationHelper locationHelper = new LocationHelper();
	private boolean paused = false;
	private boolean destroyed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location);

		final TextView locationText = findViewById(R.id.location_text);
		locationText.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!destroyed) {
					if (!paused) {
						Location location = locationHelper.getLocation();

						if (location != null) {
							locationText.setText(location.toString());
						}
					}

					locationText.postDelayed(this, 1000);
				}
			}
		}, 1000);

		locationHelper.onCreate(this);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onResume() {
		super.onResume();

		paused = false;
	}

	@Override
	protected void onPause() {
		super.onPause();

		paused = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		locationHelper.onDestroy();
		destroyed = true;
	}
}

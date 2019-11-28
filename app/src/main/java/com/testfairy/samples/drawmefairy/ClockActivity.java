package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.testfairy.samples.drawmefairy.R;

import java.util.Date;

public class ClockActivity extends Activity {
	private final String TAG = getClass().getSimpleName();
	private ImageView img;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate " + TAG);
		setContentView(R.layout.clock);

		Runnable runnable = new CountDownRunner();
		Thread myThread = new Thread(runnable);
		myThread.start();

	}

	public void doRotate() {

		runOnUiThread(new Runnable() {
			public void run() {
				try {

					Date dt = new Date();
					int seconds = 60 - dt.getSeconds();
					img = findViewById(R.id.imgsecond);
					RotateAnimation rotateAnimation = new RotateAnimation(
					    (seconds + 1) * 6, seconds * 6,
					    Animation.RELATIVE_TO_SELF, 0.5f,
					    Animation.RELATIVE_TO_SELF, 0.5f);

					rotateAnimation.setInterpolator(new LinearInterpolator());
					rotateAnimation.setDuration(1000);
					rotateAnimation.setFillAfter(true);

					img.startAnimation(rotateAnimation);
				} catch (Exception e) {

					e.printStackTrace();

				}
			}
		});
	}


	class CountDownRunner implements Runnable {
		// @Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {

					doRotate();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					Log.e(TAG, "Error is " + e.toString());
				}
			}
		}
	}
}
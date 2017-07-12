package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import draw.me.fairy.R;
import utils.ActivityTime;

public class AboutActivity extends Activity {

	private final String TAG = getClass().getSimpleName();
	ActivityTime activityTime;
	private String secretEasterEgg;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate " + TAG);
		// hide title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.about);
		ImageView image = (ImageView) findViewById(R.id.about_image);

		RotateAnimation anim = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		image.startAnimation(anim);

		image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
//				crash
				secretEasterEgg.length();
				return false;
			}
		});
	}

	@Override
	protected void onStart() {
		activityTime = new ActivityTime(TAG);
		super.onStart();
	}

	@Override
	protected void onPause() {
		activityTime.cancel();
		super.onPause();
	}
}
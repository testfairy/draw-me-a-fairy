package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import draw.me.fairy.R;

import com.testfairy.TestFairy;

import java.util.Random;

public class SplashScreenActivity extends Activity {

	private final String TAG = getClass().getSimpleName();

	private Animation.AnimationListener splashScreenAnimationListener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			Intent intent = new Intent(SplashScreenActivity.this, MenuActivity.class);
			startActivity(intent);
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	/**
	 * Returns a random animal name, for example "Red Dragon"
	 *
	 * @return string
	 */
	private String randomizeAnimalName() {

		Random random = new Random();
		String[] colors = new String[] {"red", "green", "blue", "black", "white", "pink", "purple", "orange", "yellow"};
		String[] animals = new String[] {"monkey", "dragon", "tiger", "dog", "cat", "mouse", "lion", "parrot", "blowfish"};

		String color = colors[random.nextInt() % colors.length];
		String animal = animals[random.nextInt() % animals.length];
		return color + "." + animal + "@demo.com";
	}

	/**
	 * Returns animal name that is stored on device. Will randomize on the first run
	 *
	 * @return string
	 */
	private String getAnimalName() {

		SharedPreferences preferences = getSharedPreferences("user_details", MODE_PRIVATE);
		String name = preferences.getString("name", null);
		if (name == null) {
			// randomize on first launch
			name = randomizeAnimalName();
			preferences.edit().putString("name", name).commit();
		}

		return name;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		TestFairy.setUserId(getAnimalName());

		TestFairy.begin(this, "e27cf8c46bb25d8986e21915d700e493b268df0b");

		Log.d(TAG, "onCreate " + TAG);
		ImageView image = (ImageView) findViewById(R.id.about_image);

		RotateAnimation anim = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(0);
		anim.setDuration(3000);
		anim.setAnimationListener(splashScreenAnimationListener);
		image.startAnimation(anim);
	}
}

package com.testfairy.samples.drawmefairy;

import android.app.Application;
import android.content.SharedPreferences;

import com.testfairy.TestFairy;

import java.util.Random;
import java.util.UUID;

import audio.TestFairyAudioRecord;

public class MyApplication extends Application {

	private final String TAG = getClass().getSimpleName();

	/// app_token is taken from https://app.testfairy.com/settings/
	/// please remember to change this if you're cloning the repo
	private final String APP_TOKEN = BuildConfig.APP_TOKEN;

	/**
	 * Returns a random animal name, for example "Red Dragon"
	 *
	 * @return string
	 */
	private String randomizeAnimalName() {

		Random random = new Random();
		String[] colors = new String[] {"red", "green", "blue", "black", "white", "pink", "purple", "orange", "yellow"};
		String[] animals = new String[] {"monkey", "dragon", "tiger", "dog", "cat", "mouse", "lion", "parrot", "blowfish"};

		String color = colors[Math.abs(random.nextInt()) % colors.length];
		String animal = animals[Math.abs(random.nextInt()) % animals.length];
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
			preferences.edit().putString("name", name).apply();
		}

		return name;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// uncomment if you're using an on-premise or private-cloud configuration
		// please see https://www.testfairy.com/enterprise for more information
		// TestFairy.setServerEndpoint("https://mycorp.testfairy.com/services/");

		TestFairy.setUserId(getAnimalName());
		TestFairy.begin(this, APP_TOKEN);
		TestFairy.hideView(R.id.hidden);

		// these logs are not visible in 'adb logcat', but are visible in testfairy
		TestFairy.log(TAG, "Remote logging is enabled");
		TestFairy.log(TAG, "Random UUID for this session is " + UUID.randomUUID().toString());
	}
}

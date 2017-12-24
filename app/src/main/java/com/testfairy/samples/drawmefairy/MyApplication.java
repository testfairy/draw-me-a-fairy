package com.testfairy.samples.drawmefairy;

import android.app.Application;
import android.content.SharedPreferences;

import com.testfairy.TestFairy;

import java.util.Random;

public class MyApplication extends Application {

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
	public void onCreate() {
		super.onCreate();

		TestFairy.setUserId(getAnimalName());
		TestFairy.begin(this, "e27cf8c46bb25d8986e21915d700e493b268df0b");
	}
}

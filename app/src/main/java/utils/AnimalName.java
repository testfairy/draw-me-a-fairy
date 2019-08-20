package utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class AnimalName {

	/**
	 * Returns a random animal name, for example "Red Dragon"
	 *
	 * @return string
	 */
	private static String randomizeAnimalName() {

		Random random = new Random();
		String[] colors = new String[]{"red", "green", "blue", "black", "white", "pink", "purple", "orange", "yellow"};
		String[] animals = new String[]{"monkey", "dragon", "tiger", "dog", "cat", "mouse", "lion", "parrot", "blowfish"};

		String color = colors[Math.abs(random.nextInt()) % colors.length];
		String animal = animals[Math.abs(random.nextInt()) % animals.length];
		return color + "." + animal;
	}

	/**
	 * Returns animal name that is stored on device. Will randomize on the first run
	 *
	 * @return string
	 */
	public static String getAnimalName(Context context) {

		SharedPreferences preferences = context.getSharedPreferences("user_details", MODE_PRIVATE);
		String name = preferences.getString("name", null);
		if (name == null) {
			// randomize on first launch
			name = randomizeAnimalName();
			preferences.edit().putString("name", name).apply();
		}

		return name;
	}

	public static String getAnimaEmail(Context context) {

		SharedPreferences preferences = context.getSharedPreferences("user_details", MODE_PRIVATE);
		String name = preferences.getString("name", null);
		if (name == null) {
			// randomize on first launch
			name = randomizeAnimalName();
			preferences.edit().putString("name", name).apply();
		}

		return name + "@demo.com";
	}
}

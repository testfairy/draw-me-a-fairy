package com.testfairy.samples.drawmefairy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.testfairy.TestFairy;

import java.util.UUID;

import utils.AnimalName;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

	private final String TAG = getClass().getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();

		// uncomment if you're using an on-premise or private-cloud configuration
		// please see https://www.testfairy.com/enterprise for more information
		/// app_token is taken from https://app.testfairy.com/settings/
		/// please remember to change this if you're cloning the repo

		TestFairyData testFairyData = new TestFairyDataReader().read(this);

		TestFairy.setServerEndpoint(testFairyData.getServerEndpoint());
		TestFairy.setUserId(AnimalName.getAnimalEmail(this));
		TestFairy.begin(this, testFairyData.getAppToken());
//		TestFairy.hideView(R.id.hidden);

		registerActivityLifecycleCallbacks(this);

		// these logs are not visible in 'adb logcat', but are visible in testfairy
		TestFairy.log(TAG, "Remote logging is enabled");
		TestFairy.log(TAG, "Random UUID for this session is " + UUID.randomUUID().toString());
	}

	private boolean firstLaunch = true;
	private boolean clearTask = false;
	private boolean forceFinish = false;

	@SuppressLint("ApplySharedPref")
	public void markOnPurposeCrash() {
		getSharedPreferences(TAG, MODE_PRIVATE).edit().putBoolean("crashedOnPurpose", true).commit();
	}

	@SuppressLint("ApplySharedPref")
	private void unmarkOnPurposeCrash() {
		getSharedPreferences(TAG, MODE_PRIVATE).edit().putBoolean("crashedOnPurpose", false).commit();
	}

	private boolean isLaunchAfterOnPurposeCrash() {
		boolean result = getSharedPreferences(TAG, MODE_PRIVATE)
				.getBoolean("crashedOnPurpose", false);

		return result;
	}

	@Override
	public synchronized void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		if (firstLaunch) {
			firstLaunch = false;

			boolean crashedOnPurpose = isLaunchAfterOnPurposeCrash();
			unmarkOnPurposeCrash();

			if (crashedOnPurpose) {
				clearTask = true;
			}
		}

		if (forceFinish) {
			forceFinish = false;
			activity.finish();
		}

		if (clearTask) {
			clearTask = false;
			forceFinish = true;

			Intent intent = new Intent(this, MenuActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}
}

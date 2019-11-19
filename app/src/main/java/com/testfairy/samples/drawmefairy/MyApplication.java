package com.testfairy.samples.drawmefairy;

import android.app.Application;

import com.testfairy.TestFairy;
import com.testfairy.samples.drawmefairy.R;

import java.util.UUID;

import utils.AnimalName;

public class MyApplication extends Application {

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
		TestFairy.hideView(R.id.hidden);

		// these logs are not visible in 'adb logcat', but are visible in testfairy
		TestFairy.log(TAG, "Remote logging is enabled");
		TestFairy.log(TAG, "Random UUID for this session is " + UUID.randomUUID().toString());
	}
}

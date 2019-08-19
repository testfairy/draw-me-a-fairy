package com.testfairy.samples.drawmefairy;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class TestFairyData {

	private final String TAG = getClass().getSimpleName();
	private String appToken;
	private String serverEndpoint;

	public String getAppToken() {
		return this.appToken;
	}

	public String getServerEndpoint() {
		return this.serverEndpoint;
	}

	public TestFairyData setData(String str) {

		try {

			JSONObject obj = new JSONObject(str);
			this.appToken = obj.getString("appToken");
			this.serverEndpoint = obj.getString("serverEndpoint");


		} catch (Throwable t) {
			Log.e(TAG, "Could not parse malformed JSON: \"" + str + "\"");
		}

		return this;
	}
}

public class TestFairyDataReader {

	private final String TAG = getClass().getSimpleName();

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public TestFairyData read(Context context) {

		StringBuilder str = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
				new InputStreamReader(context.getAssets().open("user_data.json"), "UTF-8"));

			// do reading, usually loop until end of file reading
			String mLine;
			while ((mLine = reader.readLine()) != null) {
				//process line
				str.append(mLine);
			}
		} catch (IOException e) {
			//log the exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//log the exception
				}
			}
		}

		Log.d(TAG, "read user data " + str);
		return new TestFairyData().setData(str.toString());
	}
}

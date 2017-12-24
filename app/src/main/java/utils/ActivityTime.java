package utils;

import android.os.CountDownTimer;
import com.testfairy.TestFairy;

public class ActivityTime extends CountDownTimer {

	final static int checkTime = 60;
	String activityName = "";

	public ActivityTime(String activityName) {

		super(checkTime * 1000, 1000);
		this.activityName = activityName;
		start();
	}

	@Override
	public void onTick(long millisUntilFinished) {
		int runTime = (int) (checkTime - (millisUntilFinished / 1000));

		if (runTime % 10 == 0) {
			TestFairy.addEvent(activityName + " running for " + runTime + " sec");
		}
	}

	@Override
	public void onFinish() {
	}
};
package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;
import draw.me.fairy.R;

/**
 * Created by gilt on 12/17/14.
 */
public class CrashActivity extends Activity {


	private final long startTime = 5 * 1000;
	private final long interval = 1 * 1000;
	public TextView text;
	private CountDownTimer countDownTimer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash);
		text = (TextView) this.findViewById(R.id.timer);
		countDownTimer = new MyCountDownTimer(startTime, interval);
		text.setText(text.getText() + String.valueOf(startTime / 1000));
		countDownTimer.start();
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this, "You can't go back", Toast.LENGTH_SHORT).show();
//		super.onBackPressed();
	}

	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			text.setText("Time's up!");
			text = null;
			text.toString();

		}

		@Override
		public void onTick(long millisUntilFinished) {
			text.setText("" + millisUntilFinished / 1000);
		}
	}
}
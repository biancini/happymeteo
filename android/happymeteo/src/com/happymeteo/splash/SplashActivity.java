package com.happymeteo.splash;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.happymeteo.IndexActivity;
import com.happymeteo.R;

public class SplashActivity extends Activity {
	private static final long DELAY = 3000;
	private boolean scheduled = false;
	private Timer splashTimer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		splashTimer = new Timer();
		splashTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				SplashActivity.this.finish();
				startActivity(new Intent(SplashActivity.this, IndexActivity.class));
			}
		}, DELAY);
		scheduled = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (scheduled) splashTimer.cancel();
		splashTimer.purge();
	}
}

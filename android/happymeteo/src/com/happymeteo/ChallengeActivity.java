package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happymeteo.utils.Const;

public class ChallengeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);

		Log.i(Const.TAG, "Create ChallengeActivity");

		Button btnChallengeFacebook = (Button) findViewById(R.id.btnChallengeFacebook);
		Button btnChallengeRandom = (Button) findViewById(R.id.btnChallengeRandom);
		Button btnBackChallenge = (Button) findViewById(R.id.btnBackChallenge);

		btnChallengeFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
			}
		});
		
		btnChallengeRandom.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
			}
		});
		
		btnBackChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().terminate(
				getApplicationContext());

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}

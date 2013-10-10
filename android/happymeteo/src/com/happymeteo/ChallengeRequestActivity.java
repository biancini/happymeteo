package com.happymeteo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class ChallengeRequestActivity extends AppyMeteoImpulseActivity
		implements onPostExecuteListener {
	private Activity activity;
	private onPostExecuteListener onPostExecuteListener;

	private final String CHALLENGE_ID = "challenge_id";
	private final String ADVERSARY_FACEBOOK_ID = "adversary_facebook_id";
	private final String ADVERSARY_NAME = "adversary_name";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_request);
		super.onCreate(savedInstanceState);

		this.activity = this;
		this.onPostExecuteListener = this;

		Button btnAcceptChallenge = (Button) findViewById(R.id.btnAcceptChallenge);
		btnAcceptChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.acceptChallenge(onPostExecuteListener,
						activity, intentParameters.get(CHALLENGE_ID), true,
						GCMRegistrar.getRegistrationId(activity),
						SessionCache.getUser_id(activity));
			}
		});

		Button btnRefuseChallenge = (Button) findViewById(R.id.btnRefuseChallenge);
		btnRefuseChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.acceptChallenge(onPostExecuteListener,
						activity, intentParameters.get(CHALLENGE_ID), false,
						GCMRegistrar.getRegistrationId(activity),
						SessionCache.getUser_id(activity));
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception == null) {
			finish();
		}
	}

	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(CHALLENGE_ID);
		keyIntentParameters.add(ADVERSARY_FACEBOOK_ID);
		keyIntentParameters.add(ADVERSARY_NAME);
		return keyIntentParameters;
	}
}

package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.ServerUtilities;

public class ChallengeRequestActivity extends ImpulseActivity implements OnPostExecuteListener {

	private final String CHALLENGE_ID = "challenge_id";
	private final String ADVERSARY_FACEBOOK_ID = "adversary_facebook_id";
	private final String ADVERSARY_NAME = "adversary_name";

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;
		finish();
	}

	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(CHALLENGE_ID);
		keyIntentParameters.add(ADVERSARY_FACEBOOK_ID);
		keyIntentParameters.add(ADVERSARY_NAME);
		return keyIntentParameters;
	}

	@Override
	public void showActivity() {
		ProfilePictureView adversaryPic = (ProfilePictureView) findViewById(R.id.adversaryPic);
		String adversaryFacebookId = intentParameters.get(ADVERSARY_FACEBOOK_ID);
		if (adversaryFacebookId != null) adversaryPic.setProfileId(adversaryFacebookId);

		TextView adversaryPicTextView = (TextView) findViewById(R.id.adversaryName);
		String adversaryName = intentParameters.get(ADVERSARY_NAME);
		if (adversaryName != null) adversaryPicTextView.setText(adversaryName.toUpperCase(Locale.getDefault()));

		Button btnAcceptChallenge = (Button) findViewById(R.id.btnAcceptChallenge);
		btnAcceptChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.acceptChallenge(ChallengeRequestActivity.this, 
						intentParameters.get(CHALLENGE_ID), true,
						GCMRegistrar.getRegistrationId(view.getContext()),
						SessionCache.getUser_id(view.getContext()));
			}
		});

		Button btnRefuseChallenge = (Button) findViewById(R.id.btnRefuseChallenge);
		btnRefuseChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.acceptChallenge(ChallengeRequestActivity.this,
						intentParameters.get(CHALLENGE_ID), false,
						GCMRegistrar.getRegistrationId(view.getContext()),
						SessionCache.getUser_id(view.getContext()));
			}
		});
	}

	@Override
	public int getContentView() {
		return R.layout.activity_challenge_request;
	}

	@Override
	public void onCreation() {
		// TODO Auto-generated method stub
		
	}
}

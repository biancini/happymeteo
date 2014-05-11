package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;

public class ChallengeScoreActivity extends ImpulseActivity {
	
	private final String IO_CHALLENGE = "ioChallenge";
	private final String TU_CHALLENGE = "tuChallenge";
	private final String TU_FACEBOOK_ID = "tuFacebookId";
	private final String TU_NAME = "tuName";
	
	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(IO_CHALLENGE);
		keyIntentParameters.add(TU_CHALLENGE);
		keyIntentParameters.add(TU_FACEBOOK_ID);
		keyIntentParameters.add(TU_NAME);
		return keyIntentParameters;
	}
	
	@Override
	public void showActivity() {
		Integer ioScore = null;
		Integer tuScore = null;

		TextView ioChallengeTextView = (TextView) findViewById(R.id.ioChallenge);
		ProfilePictureView ioPic = (ProfilePictureView) findViewById(R.id.ioPic);
		TextView ioNameTextView = (TextView) findViewById(R.id.ioName);
		//TextView ioSubnameTextView = (TextView) findViewById(R.id.ioSubname);
		
		String ioChallenge = intentParameters.get(IO_CHALLENGE);
		if (ioChallenge != null) {
			ioScore = Float.valueOf(ioChallenge).intValue();
			ioChallengeTextView.setText(ioScore.toString());
		}
		
		ioPic.setProfileId(SessionCache.getFacebook_id(this));
		ioNameTextView.setText(SessionCache.getFirst_name(this).toUpperCase(Locale.getDefault()));
		
		TextView tuChallengeTextView = (TextView) findViewById(R.id.tuChallenge);
		ProfilePictureView tuPic = (ProfilePictureView) findViewById(R.id.tuPic);
		TextView tuNameTextView = (TextView) findViewById(R.id.tuName);
		
		String tuChallenge = intentParameters.get(TU_CHALLENGE);
		if (tuChallenge != null) {
			tuScore = Float.valueOf(tuChallenge).intValue();
			tuChallengeTextView.setText(tuScore.toString());
		}
		
		String tuFacebookId = intentParameters.get(TU_FACEBOOK_ID);
		if (tuFacebookId != null) tuPic.setProfileId(tuFacebookId);

		String tuName = intentParameters.get(TU_NAME);
		if (tuName != null) tuNameTextView.setText(tuName.toUpperCase(Locale.getDefault()));
	}

	@Override
	public int getContentView() {
		return R.layout.activity_challenge_score;
	}

	@Override
	public void onCreation() {
		// Do Nothing
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}

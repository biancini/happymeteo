package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;

public class ChallengeScoreActivity extends ImpulseActivity {
	
	protected final static String IO_CHALLENGE = "ioChallenge";
	protected final static String TU_CHALLENGE = "tuChallenge";
	protected final static String TU_FACEBOOK_ID = "tuFacebookId";
	protected final static String TU_NAME = "tuName";
	protected final static String HAS_CLOSE = "hasClose";
	
	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(IO_CHALLENGE);
		keyIntentParameters.add(TU_CHALLENGE);
		keyIntentParameters.add(TU_FACEBOOK_ID);
		keyIntentParameters.add(TU_NAME);
		keyIntentParameters.add(HAS_CLOSE);
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
		
		Boolean hasClose = Boolean.getBoolean(intentParameters.get(HAS_CLOSE));
		if (hasClose) {
			ImageView resultImageView = (ImageView) findViewById(R.id.result);
			resultImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}
	
	@Override
	public void onBackPressed() {
		Boolean hasClose = Boolean.getBoolean(intentParameters.get(HAS_CLOSE));
		if (hasClose) {
			finish();
		}
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

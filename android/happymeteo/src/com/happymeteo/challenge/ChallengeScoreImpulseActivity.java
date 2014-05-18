package com.happymeteo.challenge;

import java.util.List;

import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;

public class ChallengeScoreImpulseActivity extends ImpulseActivity {
	
	@Override
	public List<String> getKeyIntentParameters() {
		return ChallengeScoreActivity.getIntentParameterKeys();
	}
	
	@Override
	public void showActivity() {
		ChallengeScoreActivity.drawInterface(this, intentParameters);
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

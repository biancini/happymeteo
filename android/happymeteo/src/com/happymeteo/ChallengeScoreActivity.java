package com.happymeteo;

import android.os.Bundle;
import android.widget.TextView;

public class ChallengeScoreActivity extends AppyMeteoNotLoggedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_score);
		super.onCreate(savedInstanceState);

		TextView ioChallengeTextView = (TextView) findViewById(R.id.ioChallenge);
		String ioChallenge = getIntent().getExtras().getString("ioChallenge");
		if(ioChallenge != null) ioChallengeTextView.setText(ioChallenge);
		
		TextView tuChallengeTextView = (TextView) findViewById(R.id.tuChallenge);
		String tuChallenge = getIntent().getExtras().getString("tuChallenge");
		if(tuChallenge != null) tuChallengeTextView.setText(tuChallenge);
		
		TextView resultChallengeTextView = (TextView) findViewById(R.id.resultChallenge);
		String resultChallenge = getIntent().getExtras().getString("resultChallenge");
		if(resultChallenge != null) resultChallengeTextView.setText(resultChallenge);
	}
}

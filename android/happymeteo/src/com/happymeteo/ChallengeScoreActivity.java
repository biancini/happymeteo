package com.happymeteo;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChallengeScoreActivity extends AppyMeteoImpulseActivity {
	
	private final String IO_CHALLENGE = "ioChallenge";
	private final String TU_CHALLENGE = "tuChallenge";
	
	private void setValues() {
		Float ioScore = null;
		Float tuScore = null;

		TextView ioChallengeTextView = (TextView) findViewById(R.id.ioChallenge);
		String ioChallenge = intentParameters.get(IO_CHALLENGE);
		if(ioChallenge != null) {
			ioScore = Float.valueOf(ioChallenge);
			ioChallengeTextView.setText(ioChallenge);
		}
		
		TextView tuChallengeTextView = (TextView) findViewById(R.id.tuChallenge);
		String tuChallenge = intentParameters.get(TU_CHALLENGE);
		if(tuChallenge != null) {
			tuScore = Float.valueOf(tuChallenge);
			tuChallengeTextView.setText(tuChallenge);
		}
		
		TextView resultChallengeTextView = (TextView) findViewById(R.id.resultChallenge);
		if(ioScore != null && tuScore != null) {
			if(ioScore > tuScore)
				resultChallengeTextView.setText("Hai vinto!");
			if(ioScore < tuScore)
				resultChallengeTextView.setText("Hai perso!");
			if(ioScore == tuScore)
				resultChallengeTextView.setText("Hai pareggiato!");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_score);
		super.onCreate(savedInstanceState);
		
		setValues();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setValues();
	}
	
	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(IO_CHALLENGE);
		keyIntentParameters.add(TU_CHALLENGE);
		return keyIntentParameters;
	}
}

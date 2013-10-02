package com.happymeteo;

import com.happymeteo.utils.Const;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ChallengeScoreActivity extends AppyMeteoNotLoggedActivity {
	
	private void setValues(Intent intent) {
		Log.i(Const.TAG, "ChallengeScoreActivity.setValues extras: "+intent.getExtras());
		
		Float ioScore = null;
		Float tuScore = null;

		TextView ioChallengeTextView = (TextView) findViewById(R.id.ioChallenge);
		String ioChallenge = intent.getExtras().getString("ioChallenge");
		if(ioChallenge != null) {
			ioScore = Float.valueOf(ioChallenge);
			ioChallengeTextView.setText(ioChallenge);
		}
		
		TextView tuChallengeTextView = (TextView) findViewById(R.id.tuChallenge);
		String tuChallenge = intent.getExtras().getString("tuChallenge");
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
		
		setValues(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setValues(intent);
	}
}

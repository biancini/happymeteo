package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happymeteo.models.Challenge;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class ChallengeRequestActivity extends AppyMeteoNotLoggedActivity {
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_request);
		super.onCreate(savedInstanceState);
		
		this.activity = this;
		
		final String challengeJson = getIntent().getStringExtra("challenge");
		
		try {
			JSONObject object = new JSONObject(challengeJson);
			final Challenge challenge = new Challenge(object);
			
			Button btnAcceptChallenge = (Button) findViewById(R.id.btnAcceptChallenge);
			
			btnAcceptChallenge.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					ServerUtilities.acceptChallenge(activity, challenge.getChallenge_id(), true);
					
					finish();
				}
			});
			
			Button btnRefuseChallenge = (Button) findViewById(R.id.btnRefuseChallenge);
			
			btnRefuseChallenge.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					ServerUtilities.acceptChallenge(activity, challenge.getChallenge_id(), false);
					
					finish();
				}
			});
		} catch (JSONException e) {
			Log.e(Const.TAG, e.getMessage(), e);
			finish();
		}
	}
}

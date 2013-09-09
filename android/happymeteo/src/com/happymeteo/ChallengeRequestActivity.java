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

public class ChallengeRequestActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge_request);
		final String data = getIntent().getStringExtra("data");
		
		Log.i(Const.TAG, "data: "+data);
		
		try {
			JSONObject object = new JSONObject(data);
			JSONObject challengeJson = object.getJSONObject("challenge");
			final Challenge challenge = new Challenge(challengeJson);
			
			Button btnAcceptChallenge = (Button) findViewById(R.id.btnAcceptChallenge);
			
			btnAcceptChallenge.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					ServerUtilities.acceptChallenge(challenge.getChallenge_id(), false);
					
					finish();
				}
			});
			
			Button btnRefuseChallenge = (Button) findViewById(R.id.btnRefuseChallenge);
			
			btnRefuseChallenge.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					ServerUtilities.acceptChallenge(challenge.getChallenge_id(), true);
					
					finish();
				}
			});
		} catch (JSONException e) {
			Log.e(Const.TAG, e.getMessage(), e);
			finish();
		}
	}
}

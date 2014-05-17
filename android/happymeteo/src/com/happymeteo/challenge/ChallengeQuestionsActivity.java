package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happymeteo.QuestionImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.meteo.MeteoActivity;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.ServerUtilities;

public class ChallengeQuestionsActivity extends QuestionImpulseActivity implements OnPostExecuteListener {
	private Map<String, String> params = null;
	
	private final String CHALLENGE_ID = "challenge_id";
	private final String TURN = "turn";
	private final String SCORE = "score";

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;
		
		switch (id) {
		case Const.QUESTIONS_CHALLENGE_URL_ID:
			RelativeLayout wait = (RelativeLayout) findViewById(R.id.waitGetChallengeQuestions);
			wait.setVisibility(View.GONE);
			
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					final String id_question = jsonObject.getString("id");
					String questionText = Html.fromHtml(jsonObject.getString("question")).toString();
					final int type = jsonObject.getInt("type");
					final String textYes = jsonObject.getString("textYes");
					final String textNo = jsonObject.getString("textNo");
					final boolean mandatory = false;

					writeQuestionText(questionText);
					writeQuestionAnswerArea(type, id_question, mandatory, textYes, textNo);
				}
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
			break;
		case Const.SUBMIT_CHALLENGE_URL_ID:
			try {
				JSONObject jsonObject = new JSONObject(result);
				Bundle extras = new Bundle();
				extras.putBoolean("ChallengeScoreActivity", true);
				extras.putString("ioChallenge", jsonObject.getString("score"));
				extras.putString("tuFacebookId", jsonObject.getString("tuFacebookId"));
				extras.putString("tuName", jsonObject.getString("tuName"));
				
				String enemyScore = intentParameters.get(SCORE);
				if (enemyScore != null) extras.putString("tuChallenge", enemyScore);
				
				invokeActivity(MeteoActivity.class, extras);
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}
	}

	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(CHALLENGE_ID);
		keyIntentParameters.add(TURN);
		keyIntentParameters.add(SCORE);
		return keyIntentParameters;
	}

	@Override
	public void showActivity() {
		params = new HashMap<String, String>();
		questions = new JSONObject();

		linearLayout = (LinearLayout) findViewById(R.id.layoutChallengeQuestions);
		
		ServerUtilities.getChallengeQuestions(this, intentParameters.get(CHALLENGE_ID), intentParameters.get(TURN));
		
		final Button btnBeginChallengeQuestions = (Button) findViewById(R.id.btnBeginChallengeQuestions);
		btnBeginChallengeQuestions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Location location = mLocationClient.getLastLocation();

				if (location != null) {
					Log.d(Const.TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
					
					params.put("latitude", String.valueOf(location.getLatitude()));
					params.put("longitude", String.valueOf(location.getLongitude()));
				}

				params.put("user_id", SessionCache.getUser_id(view.getContext()));
				params.put("questions", questions.toString());
				params.put("challenge_id", intentParameters.get(CHALLENGE_ID));
				params.put("turn", intentParameters.get(TURN));

				ServerUtilities.submitChallenge(ChallengeQuestionsActivity.this, params);
			}
		});
	}

	@Override
	public int getContentView() {
		return R.layout.activity_challenge_questions;
	}
}

package com.happymeteo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.happymeteo.models.Challenge;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.LocationManagerHelper;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class ChallengeQuestionsActivity extends AppyMeteoLoggedActivity implements
		onPostExecuteListener {
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;
	private boolean questionsStarted;
	private Map<String, String> params;
	private LocationManagerHelper locationListener;
	private JSONObject questions;
	private LinearLayout linearLayout;
	private String enemyScore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_questions);
		super.onCreate(savedInstanceState);
		
		final String challengeJson = getIntent().getStringExtra("challenge");
		final String turn = getIntent().getStringExtra("turn");
		enemyScore = getIntent().getStringExtra("score");
		
		try {
			JSONObject object = new JSONObject(challengeJson);
			final Challenge challenge = new Challenge(object);
			
			this.activity = this;
			this.onPostExecuteListener = this;

			// Get the location manager
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			locationListener = new LocationManagerHelper();

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 30000, 100, locationListener);
			} else {
				Log.i(Const.TAG, "GPS is not turned on...");
				if (locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 30000, 100,
							locationListener);
				} else {
					Log.i(Const.TAG, "Network is not turned on...");
				}
			}

			questionsStarted = false;
			params = new HashMap<String, String>();
			questions = new JSONObject();

			linearLayout = (LinearLayout) findViewById(R.id.layoutChallengeQuestions);
			final Button btnBeginChallengeQuestions = (Button) findViewById(R.id.btnBeginChallengeQuestions);
			btnBeginChallengeQuestions.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (!questionsStarted) {
						ServerUtilities.getChallengeQuestions(onPostExecuteListener,
								activity);
						btnBeginChallengeQuestions.setText(R.string.answer_questions_btn);
						questionsStarted = true;
					} else {
						Location location = locationListener.getLocation();
						Log.d(Const.TAG, "location: " + location);

						if (location != null) {
							Log.i(Const.TAG, "Latitude: " + location.getLatitude()
									+ ", Longitude: " + location.getLongitude());
							params.put("latitude",
									String.valueOf(location.getLatitude()));
							params.put("longitude",
									String.valueOf(location.getLongitude()));
						}

						params.put("user_id", HappyMeteoApplication
								.getCurrentUser().getUser_id());
						params.put("questions", questions.toString());
						params.put("challenge_id", challenge.getChallenge_id());
						params.put("turn", turn);

						ServerUtilities.submitChallenge(onPostExecuteListener,
								activity, params);
					}
				}
			});
		} catch (JSONException e) {
			Log.e(Const.TAG, e.getMessage(), e);
			finish();
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		switch (id) {
		case Const.QUESTIONS_CHALLENGE_URL_ID:
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject;
					try {
						jsonObject = jsonArray.getJSONObject(i);
						final String id_question = jsonObject.getString("id");
						final String question = jsonObject
								.getString("question");
						Log.i(Const.TAG, jsonObject.toString());

						LinearLayout linearLayout1 = new LinearLayout(
								getApplicationContext());
						linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

						LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						llp.setMargins(10, 10, 10, 10);

						TextView tv = new TextView(getApplicationContext());
						tv.setText(question);
						tv.setLayoutParams(llp);
						linearLayout1.addView(tv);

						final ToggleButton toggleButton = new ToggleButton(getApplicationContext());
						toggleButton.setLayoutParams(llp);
						toggleButton.setTextOn("Si");
						toggleButton.setTextOff("No");
						toggleButton.setChecked(false);
						toggleButton
								.setOnCheckedChangeListener(new OnCheckedChangeListener() {

									@Override
									public void onCheckedChanged(
											CompoundButton buttonView,
											boolean isChecked) {
										try {
											questions.put(id_question,
													isChecked ? "1" : "0");
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});

						linearLayout1.addView(toggleButton);
						linearLayout.addView(linearLayout1);
						questions.put(id_question, "0");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case Const.SUBMIT_CHALLENGE_URL_ID:
			Log.i(Const.TAG, "result: "+result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				Bundle extras = new Bundle();
				extras.putString("ioChallenge", jsonObject.getString("score"));
				
				if(enemyScore != null) {
					extras.putString("tuChallenge", enemyScore);
				}
				
				invokeActivity(ChallengeScoreActivity.class, extras);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			//finish();
		}
	}
}

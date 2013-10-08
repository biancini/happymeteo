package com.happymeteo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jraf.android.backport.switchwidget.Switch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;
import com.happymeteo.widget.AppyMeteoSeekBar;
import com.happymeteo.widget.AppyMeteoSeekBar.OnAppyMeteoSeekBarChangeListener;

public class ChallengeQuestionsActivity extends AppyMeteoImpulseActivity implements
		onPostExecuteListener, LocationListener {
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;
	private Map<String, String> params;
	private JSONObject questions;
	private LinearLayout linearLayout;
	
	private final String CHALLENGE_ID = "challenge_id";
	private final String TURN = "turn";
	private final String SCORE = "score";
	
	private LocationManager locationManager;
	private String provider;
	private Location location;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_questions);
		super.onCreate(savedInstanceState);
		
		this.activity = this;
		this.onPostExecuteListener = this;
		
		/* Initialize location */
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location localLocation = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (localLocation != null) {
			onLocationChanged(localLocation);
		} else {
			Toast.makeText(this, "location not available",
					Toast.LENGTH_LONG).show();
		}

		params = new HashMap<String, String>();
		questions = new JSONObject();

		linearLayout = (LinearLayout) findViewById(R.id.layoutChallengeQuestions);
		
		ServerUtilities.getChallengeQuestions(onPostExecuteListener, activity, intentParameters.get(CHALLENGE_ID), intentParameters.get(TURN));
		
		final Button btnBeginChallengeQuestions = (Button) findViewById(R.id.btnBeginChallengeQuestions);
		btnBeginChallengeQuestions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Log.d(Const.TAG, "location: " + location);

				if (location != null) {
					Log.i(Const.TAG, "Latitude: " + location.getLatitude()
							+ ", Longitude: " + location.getLongitude());
					params.put("latitude",
							String.valueOf(location.getLatitude()));
					params.put("longitude",
							String.valueOf(location.getLongitude()));
				}

				params.put("user_id", SessionCache.getUser_id(view.getContext()));
				params.put("questions", questions.toString());
				params.put("challenge_id", intentParameters.get(CHALLENGE_ID));
				params.put("turn", intentParameters.get(TURN));

				ServerUtilities.submitChallenge(onPostExecuteListener,
						activity, params);
			}
		});
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
						final int type = jsonObject.getInt("type");
						final String textYes = jsonObject.getString("textYes");
						final String textNo = jsonObject.getString("textNo");
						Log.i(Const.TAG, jsonObject.toString());

						LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						llp.setMargins(10, 10, 10, 10);

						TextView textView = new TextView(
								getApplicationContext());
						textView.setText(question);
						textView.setLayoutParams(llp);
						textView.setTextColor(getResources().getColor(
								R.color.black));
						textView.setBackgroundResource(R.drawable.fascia);
						textView.setGravity(Gravity.CENTER);
						textView.setTextSize(25.0f);
						try {
							Typeface billabong = Typeface.createFromAsset(
									getAssets(), "billabong.ttf");
							textView.setTypeface(billabong);
						} catch (Exception e) {
						}

						linearLayout.addView(textView);

						if (type == 1) {
							LinearLayout.LayoutParams llpImg = new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.WRAP_CONTENT,
									LinearLayout.LayoutParams.WRAP_CONTENT);
							llpImg.weight = 10;
							llpImg.gravity = Gravity.CENTER_VERTICAL;

							LinearLayout linearLayout1 = new LinearLayout(this);
							linearLayout1
									.setOrientation(LinearLayout.HORIZONTAL);

							ImageView imageView1 = new ImageView(this);
							imageView1.setImageResource(R.drawable.triste);
							imageView1.setLayoutParams(llpImg);
							linearLayout1.addView(imageView1);

							LinearLayout.LayoutParams llp_seekBar = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							llp_seekBar.weight = 80;

							final AppyMeteoSeekBar appyMeteoSeekBar = new AppyMeteoSeekBar(this);
							appyMeteoSeekBar.setMax(90);
							appyMeteoSeekBar.setProgress(0);
							appyMeteoSeekBar.setLayoutParams(llp_seekBar);
							
							final TextView tvText = new TextView(this);
							tvText.setText("1°");
							tvText.setBackgroundResource(R.drawable.baloon);
							tvText.setGravity(Gravity.CENTER);
							tvText.setTextColor(getResources().getColor(
									R.color.white));
							tvText.setTextSize(15.0f);
							LinearLayout.LayoutParams llpBaloon = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							llpBaloon.leftMargin = appyMeteoSeekBar.getProgressPosX();
							tvText.setLayoutParams(llpBaloon);
							linearLayout.addView(tvText);
							
							appyMeteoSeekBar.setOnAppyMeteoSeekBarChangeListener(new OnAppyMeteoSeekBarChangeListener() {
								
								@Override
								public void onProgressPosXChanged(AppyMeteoSeekBar seekBar, int progress,
										int progressPosX) {
									String value = String
											.valueOf((progress / 10) + 1);
									try {
										questions.put(id_question, value);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									tvText.setText(value + "°");
									
									LinearLayout.LayoutParams llpBaloon = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,
											LinearLayout.LayoutParams.WRAP_CONTENT);

									llpBaloon.leftMargin = progressPosX;
									
									tvText.setLayoutParams(llpBaloon);
								}
							});

							linearLayout1.addView(appyMeteoSeekBar);

							ImageView imageView2 = new ImageView(this);
							imageView2.setImageResource(R.drawable.felice);
							imageView2.setLayoutParams(llpImg);
							linearLayout1.addView(imageView2);

							linearLayout.addView(linearLayout1);

							questions.put(id_question, "1");
						} else {
							LinearLayout linearLayout1 = new LinearLayout(
									getApplicationContext());
							linearLayout1
									.setOrientation(LinearLayout.HORIZONTAL);
							linearLayout1.setGravity(Gravity.CENTER);

							TextView textYesView = new TextView(this);
							textYesView.setText(textYes);
							textYesView.setLayoutParams(llp);
							linearLayout1.addView(textYesView);

							final Switch switchButton = new Switch(this);
							switchButton.setLayoutParams(llp);
							switchButton.setChecked(true);
							switchButton
									.setOnCheckedChangeListener(new OnCheckedChangeListener() {

										@Override
										public void onCheckedChanged(
												CompoundButton buttonView,
												boolean isChecked) {
											try {
												questions.put(id_question,
														isChecked ? "0" : "1");
											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									});

							linearLayout1.addView(switchButton);

							TextView textNoView = new TextView(this);
							textNoView.setText(textNo);
							textNoView.setLayoutParams(llp);
							linearLayout1.addView(textNoView);

							linearLayout.addView(linearLayout1);
							questions.put(id_question, "0");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
				if(enemyScore != null) {
					extras.putString("tuChallenge", enemyScore);
				}
				
				invokeActivity(HappyMeteoActivity.class, extras);
			} catch (JSONException e) {
				e.printStackTrace();
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
	public void onLocationChanged(Location location) {
		this.location = location;
		Toast.makeText(
				this,
				"location: " + Double.toString(location.getLatitude()) + " "
						+ Double.toString(location.getLongitude()) + " " + location.getProvider(),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "onProviderDisabled: " + provider,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "onProviderEnabled: " + provider,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(this, "onStatusChanged: " + provider + " " + status,
				Toast.LENGTH_LONG).show();
	}
}

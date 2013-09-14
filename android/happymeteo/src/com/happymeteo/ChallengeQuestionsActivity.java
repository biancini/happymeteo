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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_questions);
		super.onCreate(savedInstanceState);

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

		linearLayout = (LinearLayout) findViewById(R.id.layoutQuestions);
		final Button btnBeginQuestions = (Button) findViewById(R.id.btnBeginQuestions);
		btnBeginQuestions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!questionsStarted) {
					ServerUtilities.getChallengeQuestions(0, onPostExecuteListener,
							activity);
					btnBeginQuestions.setText(R.string.answer_questions_btn);
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

					params.put("id_user", HappyMeteoApplication.i()
							.getCurrentUser().getUser_id());
					params.put("questions", questions.toString());

					ServerUtilities.submitChallenge(1, onPostExecuteListener,
							activity, params);
				}
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result) {
		switch (id) {
		case 0:
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

						if (type == 1) {
							final TextView tvText = new TextView(
									getApplicationContext());
							tvText.setText("1");
							tvText.setLayoutParams(llp);
							linearLayout1.addView(tvText);

							linearLayout.addView(linearLayout1);

							LinearLayout.LayoutParams llp_seekBar = new LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT,
									LayoutParams.WRAP_CONTENT);
							llp_seekBar.setMargins(10, 10, 10, 10);

							SeekBar seekBar = new SeekBar(
									getApplicationContext());
							seekBar.setThumb(getResources()
									.getDrawable(
											R.drawable.scrubber_control_selector_holo_light));
							seekBar.setProgressDrawable(getResources()
									.getDrawable(
											R.drawable.progress_horizontal_holo_light));
							seekBar.setMax(90);
							seekBar.setProgress(0);
							seekBar.setLayoutParams(llp_seekBar);
							seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
								}

								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
								}

								@Override
								public void onProgressChanged(SeekBar seekBar,
										int progress, boolean fromUser) {
									String value = String
											.valueOf((progress / 10) + 1);
									try {
										questions.put(id_question, value);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									tvText.setText(value);
								}
							});

							linearLayout.addView(seekBar);
							questions.put(id_question, "1");
						} else {
							final ToggleButton toggleButton = new ToggleButton(
									getApplicationContext());
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
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			finish();
		}
	}
}

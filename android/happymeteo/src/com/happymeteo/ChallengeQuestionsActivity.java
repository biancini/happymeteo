package com.happymeteo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.LocationManagerHelper;
import com.happymeteo.utils.ServerUtilities;

public class ChallengeQuestionsActivity extends Activity {
	
	private boolean questionsStarted;
	private Map<String, String> params;
	private LocationManagerHelper locationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge_questions);
		
		// Get the location manager
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationManagerHelper();
		
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 100, locationListener);
		} else {
		    Log.i(Const.TAG, "GPS is not turned on...");
		    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 100, locationListener);
		    } else {
		    	Log.i(Const.TAG, "Network is not turned on...");
		    }
		}
		
		questionsStarted = false;
		params = new HashMap<String, String>();
		
		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutChallengeQuestions);
		final Button btnBeginQuestions = (Button) findViewById(R.id.btnBeginChallengeQuestions);
		btnBeginQuestions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(!questionsStarted) {
					JSONArray jsonArray = ServerUtilities.getChallengeQuestions();
	
					Log.i(Const.TAG, "jsonArray: " + jsonArray);
	
					if (jsonArray != null) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject;
							try {
								jsonObject = jsonArray.getJSONObject(i);
								final String id = jsonObject.getString("id");
								final String question = jsonObject.getString("question");
								final int type = jsonObject.getInt("type");
								Log.i(Const.TAG, jsonObject.toString());
								
								LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
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
									final TextView tvText = new TextView(getApplicationContext());
									tvText.setText("1");
									tvText.setLayoutParams(llp);
									linearLayout1.addView(tvText);
									
									linearLayout.addView(linearLayout1);
									
									LinearLayout.LayoutParams llp_seekBar = new LinearLayout.LayoutParams(
											LayoutParams.FILL_PARENT,
											LayoutParams.WRAP_CONTENT);
									llp_seekBar.setMargins(10, 10, 10, 10);
									
									SeekBar seekBar = new SeekBar(getApplicationContext());
									seekBar.setThumb(getResources().getDrawable(R.drawable.scrubber_control_selector_holo_light));
									seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_horizontal_holo_light));
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
										public void onProgressChanged(SeekBar seekBar, int progress,
												boolean fromUser) {
											String value = String.valueOf((progress/10)+1);
											params.put(id, value);
											tvText.setText(value);
										}
									});
									
									linearLayout.addView(seekBar);
									params.put(id, "1");
								} else {
									final ToggleButton toggleButton = new ToggleButton(getApplicationContext());
									toggleButton.setLayoutParams(llp);
									toggleButton.setTextOn("Si");
									toggleButton.setTextOff("No");
									toggleButton.setChecked(false);
									toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
									    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
									    	params.put(id, String.valueOf(isChecked));
									    }
									});
									
									linearLayout1.addView(toggleButton);
									linearLayout.addView(linearLayout1);
									params.put(id, String.valueOf(false));
								}
							} catch (JSONException e) {
								Log.e(Const.TAG, "JSONException", e);
							}
						}
					}
					
					btnBeginQuestions.setText(R.string.answer_questions_btn);
					questionsStarted = true;
				} else {
					Location location = locationListener.getLocation();
					Log.d(Const.TAG, "location: "+location);
					
					if(location != null) {
						Log.i(Const.TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
						params.put("latitude", String.valueOf(location.getLatitude()));
						params.put("longitude", String.valueOf(location.getLongitude()));
					}
					
					if(ServerUtilities.submitChallenge(params)) {
						finish();
					}
				}
			}
		});
	}
}

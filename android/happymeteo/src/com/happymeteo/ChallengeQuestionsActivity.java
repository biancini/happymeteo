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
import com.happymeteo.utils.ServerUtilities;

public class ChallengeQuestionsActivity extends Activity {
	
	private boolean questionsStarted;
	private Map<String, String> params;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge_questions);
		
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
					Location location = getBestLocation();
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
	
	/**
	 * try to get the 'best' location selected from all providers
	 */
	private Location getBestLocation() {
	    Location gpslocation = getLocationByProvider(LocationManager.GPS_PROVIDER);
	    Location networkLocation = getLocationByProvider(LocationManager.NETWORK_PROVIDER);
	    // if we have only one location available, the choice is easy
	    if (gpslocation == null) {
	        Log.d(Const.TAG, "No GPS Location available.");
	        return networkLocation;
	    }
	    if (networkLocation == null) {
	        Log.d(Const.TAG, "No Network Location available");
	        return gpslocation;
	    }
	    // a locationupdate is considered 'old' if its older than the configured
	    // update interval. this means, we didn't get a
	    // update from this provider since the last check
	    long old = System.currentTimeMillis() - getGPSCheckMilliSecsFromPrefs();
	    boolean gpsIsOld = (gpslocation.getTime() < old);
	    boolean networkIsOld = (networkLocation.getTime() < old);
	    // gps is current and available, gps is better than network
	    if (!gpsIsOld) {
	        Log.d(Const.TAG, "Returning current GPS Location");
	        return gpslocation;
	    }
	    // gps is old, we can't trust it. use network location
	    if (!networkIsOld) {
	        Log.d(Const.TAG, "GPS is old, Network is current, returning network");
	        return networkLocation;
	    }
	    // both are old return the newer of those two
	    if (gpslocation.getTime() > networkLocation.getTime()) {
	        Log.d(Const.TAG, "Both are old, returning gps(newer)");
	        return gpslocation;
	    } else {
	        Log.d(Const.TAG, "Both are old, returning network(newer)");
	        return networkLocation;
	    }
	}
	
	private long getGPSCheckMilliSecsFromPrefs() {
		return 5 * 60 * 1000;
	}

	/**
	 * get the last known location from a specific provider (network/gps)
	 */
	private Location getLocationByProvider(String provider) {
	    Location location = null;
	    LocationManager locationManager = (LocationManager) getApplicationContext()
	            .getSystemService(Context.LOCATION_SERVICE);
	    try {
	        if (locationManager.isProviderEnabled(provider)) {
	            location = locationManager.getLastKnownLocation(provider);
	        }
	    } catch (IllegalArgumentException e) {
	        Log.d(Const.TAG, "Cannot acces Provider " + provider);
	    }
	    return location;
	}
}

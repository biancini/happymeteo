package com.happymeteo.question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.happymeteo.QuestionImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.ServerUtilities;

public class QuestionActivity extends QuestionImpulseActivity implements OnPostExecuteListener, LocationListener {
	private Map<String, String> params = null;
	
	private final String TIMESTAMP = "timestamp";

	private LocationManager locationManager = null;
	private String provider = null;
	private Location location = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_questions);
		super.onCreate(savedInstanceState);

		/* Initialize location */
		// Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// Define the criteria how to select the location provider -> use default
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location localLocation = locationManager.getLastKnownLocation(provider);

		// Initialize the location fields
		if (localLocation != null) onLocationChanged(localLocation);
		else Toast.makeText(this, "location not available", Toast.LENGTH_LONG).show();

		params = new HashMap<String, String>();
		questions = new JSONObject();

		linearLayout = (LinearLayout) findViewById(R.id.layoutQuestions);

		ServerUtilities.getQuestions(this, SessionCache.getUser_id(this));

		final Button btnAnswerQuestions = (Button) findViewById(R.id.btnAnswerQuestions);
		btnAnswerQuestions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (location != null) {
					params.put("latitude", String.valueOf(location.getLatitude()));
					params.put("longitude", String.valueOf(location.getLongitude()));
				}

				params.put("user_id", SessionCache.getUser_id(view.getContext()));
				params.put("questions", questions.toString());
				params.put("timestamp", intentParameters.get(TIMESTAMP));

				ServerUtilities.submitQuestions(QuestionActivity.this, params);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;

		switch (id) {
		case Const.GET_QUESTIONS_URL_ID:
			RelativeLayout wait = (RelativeLayout) findViewById(R.id.waitGetQuestions);
			wait.setVisibility(View.GONE);
			
			try {
				JSONArray jsonArray = new JSONArray(result);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					final String id_question = jsonObject.getString("id");
					
					String questionText = Html.fromHtml(jsonObject.getString("question")).toString();
					int gender = SessionCache.getGender(getApplicationContext());
					if (gender == User.GENDER_MALE) questionText = questionText.replaceAll("\\[o/a\\]", "o");
					else questionText = questionText.replaceAll("\\[o/a\\]", "a");
					
					final int type = jsonObject.getInt("type");
					final String textYes = jsonObject.getString("textYes");
					final String textNo = jsonObject.getString("textNo");
					Log.d(Const.TAG, jsonObject.toString());

					writeQuestionText(questionText);
					writeQuestionAnswerArea(type, id_question, textYes, textNo);
				}
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
			break;
		case Const.SUBMIT_QUESTIONS_URL_ID:
			try {
				JSONObject jsonObject = new JSONObject(result);
				int today = jsonObject.getInt("today");
				int yesterday = jsonObject.getInt("yesterday");
				int tomorrow = jsonObject.getInt("tomorrow");

				SessionCache.setMeteo(this, today, yesterday, tomorrow);
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}

			finish();
		}
	}

	@Override
	public List<String> getKeyIntentParameters() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(TIMESTAMP);
		return keyIntentParameters;
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
//		Toast.makeText(
//				this,
//				"location: " + Double.toString(location.getLatitude()) + " "
//						+ Double.toString(location.getLongitude()) + " " + location.getProvider(),
//				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
//		Toast.makeText(this, "onProviderDisabled: " + provider, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
//		Toast.makeText(this, "onProviderEnabled: " + provider, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
//		Toast.makeText(this, "onStatusChanged: " + provider + " " + status, Toast.LENGTH_LONG).show();
	}
}

package com.happymeteo.question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.location.Location;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.happymeteo.QuestionImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.ServerUtilities;

public class QuestionActivity extends QuestionImpulseActivity implements OnPostExecuteListener {
	private Map<String, String> params = null;
	
	private List<String> mandatoryQuestionsId = new ArrayList<String>();
	private final String TIMESTAMP = "timestamp";

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
					final int type = jsonObject.getInt("type");
					final String textYes = jsonObject.getString("textYes");
					final String textNo = jsonObject.getString("textNo");
					final boolean mandatory = jsonObject.has("mandatory") ? jsonObject.getBoolean("mandatory") : false;
					if (mandatory) mandatoryQuestionsId.add(id_question);

					writeQuestionText(questionText);
					writeQuestionAnswerArea(type, id_question, mandatory, textYes, textNo);
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
	public void showActivity() {
		params = new HashMap<String, String>();
		questions = new JSONObject();
		linearLayout = (LinearLayout) findViewById(R.id.layoutQuestions);
		ServerUtilities.getQuestions(this, SessionCache.getUser_id(this));

		final Button btnAnswerQuestions = (Button) findViewById(R.id.btnAnswerQuestions);
		btnAnswerQuestions.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					for (String mandatoryQuestionId : mandatoryQuestionsId) {
						try {
							if (questions.get(mandatoryQuestionId) == null) {
								throw new JSONException("Object null");
							}
						} catch (JSONException e) {
							AlertDialogManager.showNotification(QuestionActivity.this,
									R.string.missing_question_answer_notification_title,
									R.string.missing_question_answer_notification_msg,
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// Do nothing
								}
							});
							return;
						}
					}
					
					String timestamp = intentParameters.get(TIMESTAMP);
					Location location = mLocationClient.getLastLocation();
					
					if (location != null) {
						params.put("latitude", String.valueOf(location.getLatitude()));
						params.put("longitude", String.valueOf(location.getLongitude()));
					}
	
					params.put("user_id", SessionCache.getUser_id(view.getContext()));
					params.put("questions", questions.toString());
					params.put("timestamp", timestamp);
	
					ServerUtilities.submitQuestions(QuestionActivity.this, params);
			}
		});
	}

	@Override
	public int getContentView() {
		return R.layout.activity_questions;
	}
}

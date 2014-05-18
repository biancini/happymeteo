package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;

public class ChallengeScoreActivity extends LoggedActivity {
	
	protected final static String IO_CHALLENGE = "ioChallenge";
	protected final static String TU_CHALLENGE = "tuChallenge";
	protected final static String TU_FACEBOOK_ID = "tuFacebookId";
	protected final static String TU_NAME = "tuName";
	
	public static List<String> getIntentParameterKeys() {
		ArrayList<String> keyIntentParameters = new ArrayList<String>();
		keyIntentParameters.add(IO_CHALLENGE);
		keyIntentParameters.add(TU_CHALLENGE);
		keyIntentParameters.add(TU_FACEBOOK_ID);
		keyIntentParameters.add(TU_NAME);
		return keyIntentParameters;
	}
	
	public static void drawInterface(Context context, Map<String, String> extras) {
		Activity activity = (Activity) context;
		Integer ioScore = null;
		Integer tuScore = null;

		TextView ioChallengeTextView = (TextView) activity.findViewById(R.id.ioChallenge);
		ProfilePictureView ioPic = (ProfilePictureView) activity.findViewById(R.id.ioPic);
		TextView ioNameTextView = (TextView) activity.findViewById(R.id.ioName);
		//TextView ioSubnameTextView = (TextView) context.findViewById(R.id.ioSubname);
		
		String ioChallenge = extras.get(IO_CHALLENGE);
		if (ioChallenge != null) {
			ioScore = Float.valueOf(ioChallenge).intValue();
			ioChallengeTextView.setText(ioScore.toString());
		}
		
		ioPic.setProfileId(SessionCache.getFacebook_id(context));
		ioNameTextView.setText(SessionCache.getFirst_name(context).toUpperCase(Locale.getDefault()));
		
		TextView tuChallengeTextView = (TextView) activity.findViewById(R.id.tuChallenge);
		ProfilePictureView tuPic = (ProfilePictureView) activity.findViewById(R.id.tuPic);
		TextView tuNameTextView = (TextView) activity.findViewById(R.id.tuName);
		
		String tuChallenge = extras.get(TU_CHALLENGE);
		if (tuChallenge != null) {
			tuScore = Float.valueOf(tuChallenge).intValue();
			tuChallengeTextView.setText(tuScore.toString());
		}
		
		String tuFacebookId = extras.get(TU_FACEBOOK_ID);
		if (tuFacebookId != null) tuPic.setProfileId(tuFacebookId);

		String tuName = extras.get(TU_NAME);
		if (tuName != null) tuNameTextView.setText(tuName.toUpperCase(Locale.getDefault()));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge_score);
		super.onCreate(savedInstanceState);
		
		Map<String, String> extras = new HashMap<String, String>();
		List<String> keyIntentParamteres = getIntentParameterKeys();

		if (keyIntentParamteres == null || keyIntentParamteres.isEmpty() || getIntent().getExtras() == null) {
			AlertDialogManager.showError(this, this.getString(R.string.error_impulse));
			return;
		}
		
		for(String key : keyIntentParamteres) {
			Log.i(Const.TAG, "initialize: " + key + " => " + getIntent().getExtras().getString(key));
			extras.put(key, getIntent().getExtras().getString(key));
		}
		
		drawInterface(this, extras);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		invokeActivity(ChallengeActivity.class);
	}

}
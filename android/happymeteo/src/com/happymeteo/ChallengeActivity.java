package com.happymeteo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.models.Challenge;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class ChallengeActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	
	private void attachChallengeToView(View rowView, final Challenge challenge) {
		ProfilePictureView profilePictureView = (ProfilePictureView) rowView
				.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(challenge.getAdversary().getFacebook_id());

		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		picker_title.setText("Sfida con "+challenge.getAdversary().getFirst_name());

		TextView picker_result = (TextView) rowView.findViewById(R.id.picker_result);
		picker_title.setText("Sfida con "+challenge.getAdversary().getFirst_name());
		
		ImageView result_image = (ImageView) rowView.findViewById(R.id.result_image);
		
		Float ioScore = null;
		Float tuScore = null;
		
		if(SessionCache.getUser_id(this).equals(challenge.getUser_id_a())) {
			ioScore = Float.valueOf(challenge.getScore_a());
			tuScore = Float.valueOf(challenge.getScore_b());
		}
		
		if(SessionCache.getUser_id(this).equals(challenge.getUser_id_b())) {
			ioScore = Float.valueOf(challenge.getScore_b());
			tuScore = Float.valueOf(challenge.getScore_a());
		}
		
		if(ioScore != null && tuScore != null) {
			if(ioScore > tuScore) {
				result_image.setBackgroundResource(R.drawable.smilepositivo);
				picker_result.setText("Hai vinto! "+ioScore.toString()+"-"+tuScore.toString());
			}
			if(ioScore < tuScore) {
				result_image.setBackgroundResource(R.drawable.smilenegativo);
				picker_result.setText("Hai perso! "+ioScore.toString()+"-"+tuScore.toString());
			}
			if(ioScore == tuScore) {
				result_image.setBackgroundResource(R.drawable.smile);
				picker_result.setText("Hai pareggiato! "+ioScore.toString()+"-"+tuScore.toString());
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge);
		super.onCreate(savedInstanceState);

		Button btnChallengeNew = (Button) findViewById(R.id.btnChallengeNew);
		
		if(!SessionCache.isFacebookSession(this)) {
			btnChallengeNew.setEnabled(false);
		}

		btnChallengeNew.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context,
						FriendsFacebookActivity.class);
				context.startActivity(intent);
			}
		});
		
		ServerUtilities.getChallenges(this, this, SessionCache.getUser_id(this));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) {
			return;
		}
		
		Log.i(Const.TAG, "result: "+result);
		
		try {
			JSONArray challenges = new JSONArray(result);
			
			Log.i(Const.TAG, "challenges.length(): "+challenges.length());
			
			if(challenges.length() == 0)
				return;
			
			new ListChallenges(this, challenges).execute();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private class ListChallenges extends AsyncTask<String, Void, Void> {
		private Activity activity;
		private JSONArray challenges;
		
		public ListChallenges(Activity activity, JSONArray challenges) {
			this.activity = activity;
			this.challenges = challenges;
		}
		
		@Override
		protected Void doInBackground(String... params) {
			final LinearLayout challengeDone = (LinearLayout) activity.findViewById(R.id.challengeDone);
			
			for (int i = 0; i < challenges.length(); i++) {
				JSONObject jsonObject;
				try {
					jsonObject = challenges.getJSONObject(i);
					Challenge challenge = new Challenge(jsonObject);
					
					if(challenge.getTurn() < 3)
						continue;
					
					final View vi = getLayoutInflater().inflate(
							R.layout.activity_challenge_row, null);
					attachChallengeToView(vi, challenge);
					runOnUiThread(new Runnable() {
					     public void run() {
					    	 challengeDone.addView(vi);
					    }
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		
	}
}

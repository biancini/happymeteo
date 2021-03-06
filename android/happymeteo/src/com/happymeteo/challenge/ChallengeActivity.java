package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;
import com.happymeteo.models.Challenge;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class ChallengeActivity extends LoggedActivity {

	private View attachChallengeToView(final Challenge challenge) {
		View rowView = getLayoutInflater().inflate(R.layout.activity_challenge_row, null);
		ProfilePictureView profilePictureView = (ProfilePictureView) rowView.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(challenge.getAdversary().getFacebook_id());

		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		if (challenge.getCreated().equals("")) {
			String titleText = getString(R.string.finegioco_message);
			titleText = titleText.replaceAll("\\[USER\\]", challenge.getAdversary().getFirst_name());
			picker_title.setText(titleText);
		} else {
			String titleText = getString(R.string.finegioco_message_date);
			titleText = titleText.replaceAll("\\[USER\\]", challenge.getAdversary().getFirst_name());
			titleText = titleText.replaceAll("\\[DATE\\]", challenge.getCreated());
			picker_title.setText(titleText);
		}

		TextView picker_result = (TextView) rowView.findViewById(R.id.picker_result);
		Button picker_button = (Button) rowView.findViewById(R.id.picker_button);
		final LoggedActivity activity = this;
		if (challenge.getTurn() == 3) picker_button.setVisibility(View.GONE);
		
		boolean viewToAdd = false;
		
		if (challenge.getTurn() == 0) {
			picker_button.setBackgroundResource(R.drawable.rilancio);
			picker_button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					ServerUtilities.requestChallenge(
						ChallengeActivity.this, 
						SessionCache.getUser_id(view.getContext()),
						challenge.getAdversary().getFacebook_id(),
						GCMRegistrar.getRegistrationId(view.getContext()));
				}
			});
			viewToAdd = true;
		}
		
		if (challenge.getTurn() == 1 && SessionCache.getUser_id(this).equals(challenge.getUser_id_a()) || 
			challenge.getTurn() == 2 && SessionCache.getUser_id(this).equals(challenge.getUser_id_b())) {
			picker_button.setBackgroundResource(R.drawable.rispondi);
			picker_button.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					Bundle extras = new Bundle();
					extras.putString("challenge_id", challenge.getChallenge_id());
					extras.putString("turn", String.valueOf(challenge.getTurn()));
					if (challenge.getTurn() == 2) extras.putString("score", String.valueOf(challenge.getScore_a()));
					activity.invokeActivity(ChallengeQuestionsActivity.class, extras);
				}
			});
			viewToAdd = true;
		}

		if (challenge.getTurn() == 3) {
			final Float ioScore;
			final Float tuScore;
	
			if (SessionCache.getUser_id(this).equals(challenge.getUser_id_a())) {
				ioScore = Float.valueOf(challenge.getScore_a());
				tuScore = Float.valueOf(challenge.getScore_b());
			} else if (SessionCache.getUser_id(this).equals(challenge.getUser_id_b())) {
				ioScore = Float.valueOf(challenge.getScore_b());
				tuScore = Float.valueOf(challenge.getScore_a());
			} else  {
				ioScore = 0f;
				tuScore = 0f;
			}
			
			String resultText = getString(R.string.finegioco_result);
			resultText = resultText.replaceAll("\\[MYSCORE\\]", "" + ioScore.intValue());
			resultText = resultText.replaceAll("\\[YOURSCORE\\]", "" + tuScore.intValue());
			picker_result.setText(resultText);
			rowView.setBackgroundColor(getResources().getColor((ioScore > tuScore) ? R.color.won_grey : R.color.lost_red));
			picker_result.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle extras = new Bundle();
					extras.putString(ChallengeScoreActivity.IO_CHALLENGE, ioScore.toString());
					extras.putString(ChallengeScoreActivity.TU_CHALLENGE, tuScore.toString());
					extras.putString(ChallengeScoreActivity.TU_FACEBOOK_ID, challenge.getAdversary().getFacebook_id());
					extras.putString(ChallengeScoreActivity.TU_NAME, challenge.getAdversary().getFirst_name());
					invokeActivity(ChallengeScoreActivity.class, extras);
				}
			});
			
			viewToAdd = true;
		}
		
		if (viewToAdd) return rowView;
		else return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge);
		super.onCreate(savedInstanceState);

		ImageView btnChallengeNew = (ImageView) findViewById(R.id.btnChallengeNew);
//		Button btnChallengeNew = (Button) findViewById(R.id.btnChallengeNew);
		btnChallengeNew.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ChallengeActivity.this.invokeActivity(FriendsFacebookActivity.class);
			}
		});

		ServerUtilities.getChallenges(this, SessionCache.getUser_id(this));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;
		
		if (id == Const.GET_CHALENGES_URL_ID) {
			RelativeLayout waitGetChallenges = (RelativeLayout) findViewById(R.id.waitGetChallenges);
			waitGetChallenges.setVisibility(View.GONE);
	
			try {
				JSONArray challenges = new JSONArray(result);
				if (challenges.length() == 0) return;
				new ListChallenges(this, challenges).execute();
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}
		
		if (id == Const.REQUEST_CHALLENGE_URL_ID) {
			AlertDialogManager.showNotification(this, R.string.empty, R.string.request_challenge_notification_msg, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
				}
			});
		}
	}

	private class ListChallenges extends AsyncTask<String, Void, Void> {
		private Activity activity = null;
		private JSONArray challenges = null;

		public ListChallenges(Activity activity, JSONArray challenges) {
			this.activity = activity;
			this.challenges = challenges;
		}

		@Override
		protected Void doInBackground(String... params) {
			final List<LinearLayout> challengeTurns = new ArrayList<LinearLayout>();
			challengeTurns.add((LinearLayout) activity.findViewById(R.id.challengeTurn0));
			challengeTurns.add((LinearLayout) activity.findViewById(R.id.challengeTurn1));
			challengeTurns.add((LinearLayout) activity.findViewById(R.id.challengeTurn1));
			challengeTurns.add((LinearLayout) activity.findViewById(R.id.challengeTurn2));
			
			final List<Boolean> visibility = new ArrayList<Boolean>();
			visibility.add(false);
			visibility.add(false);
			visibility.add(false);
			visibility.add(false);
			
			final List<ImageView> imageTurns = new ArrayList<ImageView>();
			imageTurns.add((ImageView) activity.findViewById(R.id.partiteTurn0));
			imageTurns.add((ImageView) activity.findViewById(R.id.partiteTurn1));
			imageTurns.add((ImageView) activity.findViewById(R.id.partiteTurn1));
			imageTurns.add((ImageView) activity.findViewById(R.id.partiteTurn2));

			for(int i = 0; i < challenges.length(); i++) {
				JSONObject jsonObject = null;
				try {
					jsonObject = challenges.getJSONObject(i);
					final Challenge challenge = new Challenge(jsonObject);
					
					final View view = attachChallengeToView(challenge);
					
					if (view != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								challengeTurns.get(challenge.getTurn()).addView(view);
								visibility.set(challenge.getTurn(), true);
							}
						});
					}
				} catch (JSONException e) {
					Log.e(Const.TAG, e.getMessage(), e);
				}
			}
			
			runOnUiThread(new Runnable() {
				public void run() {
					for (int i = 0; i < 4; i++) {
						if (visibility.get(i)) {
							challengeTurns.get(i).setVisibility(View.VISIBLE);
							imageTurns.get(i).setVisibility(View.VISIBLE);
						}
					}
				}
			});

			return null;
		}
	}

}

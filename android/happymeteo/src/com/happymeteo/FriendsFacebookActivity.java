package com.happymeteo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.FeedDialogBuilder;
import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.models.Friend;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onGetExecuteListener;

public class FriendsFacebookActivity extends AppyMeteoLoggedActivity implements
		onGetExecuteListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friends_facebook);
		super.onCreate(savedInstanceState);
		
		if (Session.getActiveSession() == null)
			invokeActivity(IndexActivity.class, null);
		else {
			String accessToken = Session.getActiveSession().getAccessToken();
			String serverUrl = "https://graph.facebook.com/me/friends?fields=name,installed&access_token="
					+ accessToken;
			Log.i(Const.TAG, "serverUrl: " + serverUrl);
			new GetRequest(this, this).execute(serverUrl);
		}
	}
	
	private void attachFriendToView(View rowView, final Friend friend) {
		ProfilePictureView profilePictureView = (ProfilePictureView) rowView
				.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(friend.getId());

		/* Set Profile name */
		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		picker_title.setText(friend.getName());

		/* Set Button text */
		Button picker_button = (Button) rowView.findViewById(R.id.picker_button);
		
		if (friend.isInstalled()) {
			picker_title.setBackgroundColor(getResources().getColor(R.color.black));
			picker_title.setTextColor(getResources().getColor(R.color.white));
			picker_button.setBackgroundResource(R.drawable.pulsante_gioca);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					ServerUtilities.requestChallenge(
						FriendsFacebookActivity.this, 
						SessionCache.getUser_id(view.getContext()),
						friend.getId(),
						GCMRegistrar.getRegistrationId(view.getContext()));
				}
			});
		} else {
			/* Feed Dialog: https://developers.facebook.com/docs/reference/dialogs/feed/ */
			picker_button.setBackgroundResource(R.drawable.pulsante_invita);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					FeedDialogBuilder feedDialogBuilder = new FeedDialogBuilder(FriendsFacebookActivity.this, Session.getActiveSession());
					feedDialogBuilder.setDescription("Vieni in appymeteo!");
					feedDialogBuilder.setPicture(Const.BASE_URL + "/img/facebook_invita.png");
					feedDialogBuilder.setTo(friend.getId());
					WebDialog webDialog = feedDialogBuilder.build();
					webDialog.show();
				}
			});
		}
	}

	@Override
	public void onGetExecute(String result) {
		RelativeLayout wait = (RelativeLayout) findViewById(R.id.waitFriendsWithApp);
		wait.setVisibility(View.GONE);
		
		RelativeLayout wait2 = (RelativeLayout) findViewById(R.id.waitFriendsNoApp);
		wait2.setVisibility(View.GONE);
		
		List<Friend> friendsWithApp = new ArrayList<Friend>();
		List<Friend> friendsNoApp = new ArrayList<Friend>();
		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray data = jsonObject.getJSONArray("data");
			if (data != null) {
				for (int i = 0; i < data.length(); i++) {
					JSONObject profile = data.getJSONObject(i);
					Friend friend = new Friend();
					friend.setId(profile.getString("id"));
					friend.setName(profile.getString("name"));
					try {
						friend.setInstalled(profile.getString("installed") != null
								&& profile.getBoolean("installed"));
					} catch (JSONException e) {
						friend.setInstalled(false);
					}
					if (friend.isInstalled()) {
						friendsWithApp.add(friend);
					} else {
						friendsNoApp.add(friend);
					}
				}
			}
			
			new ListFriend(this, friendsWithApp, friendsNoApp).execute();
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}
	
	private class ListFriend extends AsyncTask<String, Void, Void> {
		private Activity activity;
		private List<Friend> friendsWithApp;
		private List<Friend> friendsNoApp;
		
		public ListFriend(Activity activity, List<Friend> friendsWithApp, List<Friend> friendsNoApp) {
			this.activity = activity;
			this.friendsWithApp = friendsWithApp;
			this.friendsNoApp = friendsNoApp;
		}
		
		private class MyFriendComparable implements Comparator<Friend> {

			@Override
			public int compare(Friend o1, Friend o2) {
				return o1.getName().compareTo(o2.getName());
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			Collections.sort(friendsWithApp, new MyFriendComparable());
			Collections.sort(friendsNoApp, new MyFriendComparable());
			
			final LinearLayout facebookPickerListViewWithApp = (LinearLayout) activity.findViewById(R.id.facebookPickerListViewWithApp);
			
			for (int i = 0; i < friendsWithApp.size(); i++) {
				Friend friend = friendsWithApp.get(i);
				final View vi = getLayoutInflater().inflate(
						R.layout.activity_friends_facebook_list_row, null);
				attachFriendToView(vi, friend);
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 facebookPickerListViewWithApp.addView(vi);
				    }
				});
			}

			final LinearLayout facebookPickerListViewNoApp = (LinearLayout) activity.findViewById(R.id.facebookPickerListViewNoApp);
			
			for (int i = 0; i < friendsNoApp.size(); i++) {
				Friend friend = friendsNoApp.get(i);
				final View vi = getLayoutInflater().inflate(
						R.layout.activity_friends_facebook_list_row, null);
				attachFriendToView(vi, friend);
				runOnUiThread(new Runnable() {
				     public void run() {
				    	 facebookPickerListViewNoApp.addView(vi);
				    }
				});
			}
			
			return null;
		}
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}

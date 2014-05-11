package com.happymeteo.challenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.Session;
import com.happymeteo.IndexActivity;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;
import com.happymeteo.models.Friend;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.ListFriend;
import com.happymeteo.utils.OnGetExecuteListener;

public class FriendsFacebookActivity extends LoggedActivity implements OnGetExecuteListener {
	public static int FRIENDS_WITH_APP_TYPE = 1;
	public static int FRIENDS_NO_APP_TYPE = 0;

	private String[] counter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friends_facebook);
		super.onCreate(savedInstanceState);

		if (Session.getActiveSession() == null) {
			invokeActivity(IndexActivity.class, null);
		} else {
			counter = new String[2];
			counter[FRIENDS_WITH_APP_TYPE] = null;
			counter[FRIENDS_NO_APP_TYPE] = null;
			String accessToken = Session.getActiveSession().getAccessToken();
			String serverUrl = "https://graph.facebook.com/me/friends?fields=name,installed&access_token=" + accessToken;
			new GetRequest(this, this).execute(serverUrl);
		}
	}

	private class MyFriendComparable implements Comparator<Friend> {
		@Override
		public int compare(Friend f1, Friend f2) {
			return f1.getName().compareTo(f2.getName());
		}
	}

	@Override
	public void onGetExecute(String result) {
		final RelativeLayout waitFriendsWithApp = (RelativeLayout) findViewById(R.id.waitFriendsWithApp);
		final RelativeLayout waitFriendsNoApp = (RelativeLayout) findViewById(R.id.waitFriendsNoApp);

		final LinearLayout facebookPickerListViewWithApp = (LinearLayout) findViewById(R.id.facebookPickerListViewWithApp);
		final LinearLayout facebookPickerListViewNoApp = (LinearLayout) findViewById(R.id.facebookPickerListViewNoApp);

		EditText searchFriendsWithApp = (EditText) findViewById(R.id.searchFriendsWithApp);
		EditText searchFriendsNoApp = (EditText) findViewById(R.id.searchFriendsNoApp);

		final List<Friend> friendsWithApp = new ArrayList<Friend>();
		final List<Friend> friendsNoApp = new ArrayList<Friend>();
		
		final List<ListFriend> fiendsWithAppTasks = new ArrayList<ListFriend>(); 
		final List<ListFriend> fiendsNoAppTasks = new ArrayList<ListFriend>(); 
		
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
						friend.setInstalled(profile.getString("installed") != null && profile.getBoolean("installed"));
					} catch (JSONException e) {
						friend.setInstalled(false);
					}

					if (friend.isInstalled()) friendsWithApp.add(friend);
					else friendsNoApp.add(friend);
				}
			}

			Collections.sort(friendsWithApp, new MyFriendComparable());
			Collections.sort(friendsNoApp, new MyFriendComparable());

			searchFriendsWithApp.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					List<Friend> newFriends = new ArrayList<Friend>();
					if (friendsWithApp != null) {
						for (Friend friend : friendsWithApp) {
							if (friend.getName().toLowerCase(Locale.getDefault()).contains(cs.toString().toLowerCase(Locale.getDefault()))) {
								newFriends.add(friend);
							}
						}
					}

					ListFriend curTask = new ListFriend(FRIENDS_WITH_APP_TYPE, FriendsFacebookActivity.this, newFriends, facebookPickerListViewWithApp, waitFriendsWithApp);
					fiendsWithAppTasks.add(curTask);
					curTask.execute();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// Do nothing
				}

				@Override
				public void afterTextChanged(Editable s) {
					Log.i(Const.TAG, "afterTextChanged: " + s.toString());
				}
			});

			searchFriendsNoApp.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence cs, int start, int before, int count) {
					List<Friend> newFriends = new ArrayList<Friend>();
					if (friendsNoApp != null) {
						for (Friend friend : friendsNoApp) {
							if (friend.getName().toLowerCase(Locale.getDefault()).contains(cs.toString().toLowerCase(Locale.getDefault()))) {
								newFriends.add(friend);
							}
						}
					}

					for (ListFriend curTask : fiendsNoAppTasks) {
						if (curTask.getStatus() == Status.RUNNING && !curTask.isCancelled()) curTask.cancel(true);
						if (curTask.getStatus() == Status.PENDING) curTask.cancel(true);
					}
					fiendsNoAppTasks.clear();
					
					ListFriend curTask = new ListFriend(FRIENDS_NO_APP_TYPE, FriendsFacebookActivity.this, newFriends, facebookPickerListViewNoApp, waitFriendsNoApp);
					fiendsNoAppTasks.add(curTask);
					curTask.execute();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// Do nothing
				}

				@Override
				public void afterTextChanged(Editable s) {
					Log.d(Const.TAG, "afterTextChanged: " + s.toString());
				}
			});

			ListFriend curTaskWith = new ListFriend(FRIENDS_WITH_APP_TYPE, this, friendsWithApp, facebookPickerListViewWithApp, waitFriendsWithApp);
			fiendsWithAppTasks.add(curTaskWith);
			curTaskWith.execute();
			
			ListFriend curTaskNo = new ListFriend(FRIENDS_NO_APP_TYPE, this, friendsNoApp, facebookPickerListViewNoApp, waitFriendsNoApp);
			fiendsNoAppTasks.add(curTaskNo);
			curTaskNo.execute();
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		AlertDialogManager.showNotification(this, R.string.empty,
				R.string.request_challenge_notification_msg,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});
	}

	public String clear(final int type, final LinearLayout pickerLayout, final RelativeLayout waitLayout) {
		String id = null;

		synchronized (counter) {
			counter[type] = String.valueOf(UUID.randomUUID());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					waitLayout.setVisibility(View.VISIBLE);
					pickerLayout.removeAllViews();

				}
			});
			id = counter[type];
		}

		return id;
	}

	public void populate(final int type, final String id,
			final LinearLayout pickerLayout,
			final RelativeLayout waitLayout,
			final List<View> views) {
		synchronized (counter) {
			if (counter[type] != null && id != null && id.equals(counter[type])) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						waitLayout.setVisibility(View.GONE);
						for (View view : views) {
							pickerLayout.addView(view);
						}
					}
				});
			}
		}
	}
	
}
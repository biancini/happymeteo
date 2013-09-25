package com.happymeteo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.facebook.Session;
import com.happymeteo.facebook.FriendsAdapter;
import com.happymeteo.models.Friend;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.onPostExecuteListener;

public class FriendsFacebookActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	
	public class MyFriendComparable implements Comparator<Friend>{
	 
	    @Override
	    public int compare(Friend o1, Friend o2) {
	        return o1.getName().compareTo(o2.getName());
	    }
	} 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friends_facebook);
		super.onCreate(savedInstanceState);
		
		if(Session.getActiveSession() == null)
			invokeActivity(IndexActivity.class, null);
		else {
			String accessToken = Session.getActiveSession().getAccessToken();
			String serverUrl = "https://graph.facebook.com/me/friends?fields=name,installed&access_token="+accessToken;
			Log.i(Const.TAG, "serverUrl: "+serverUrl);
			new GetRequest(this, this).execute(serverUrl);
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
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
						friend.setInstalled(profile.getString("installed") != null && profile.getBoolean("installed"));
					} catch(JSONException e) {
						friend.setInstalled(false);
					}
					if(friend.isInstalled()) {
						friendsWithApp.add(friend);
					} else {
						friendsNoApp.add(friend);
					}
				}
			}
			
			Collections.sort(friendsWithApp, new MyFriendComparable());
			Collections.sort(friendsNoApp, new MyFriendComparable());
			
			FriendsAdapter withApp = new FriendsAdapter(this, friendsWithApp);
			FriendsAdapter noApp = new FriendsAdapter(this, friendsNoApp);
			ListView facebookPickerListViewWithApp = (ListView) findViewById(R.id.facebookPickerListViewWithApp);
			facebookPickerListViewWithApp.setAdapter(withApp);
			ListView facebookPickerListViewNoApp = (ListView) findViewById(R.id.facebookPickerListViewNoApp);
			facebookPickerListViewNoApp.setAdapter(noApp);
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}
}

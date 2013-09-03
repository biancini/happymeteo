package com.happymeteo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.happymeteo.facebook.FacebookFriendsAdapter;
import com.happymeteo.models.Friend;
import com.happymeteo.utils.Const;

public class FriendsFacebookActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_facebook);
		
		List<Friend> friendsWithApp = new ArrayList<Friend>();
		List<Friend> friendsNoApp = new ArrayList<Friend>();

		Log.i(Const.TAG, "Create FriendsFacebookActivity");
		String accessToken = HappyMeteoApplication.i().getAccessToken();
		String serverUrl = "https://graph.facebook.com/me/friends?fields=name,installed&access_token="+accessToken;

		try {
			StringBuffer output = new StringBuffer();
			URL url = new URL(serverUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Request failed with status: " + status);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();
			JSONObject jsonObject = new JSONObject(output.toString());
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
			
			FacebookFriendsAdapter withApp = new FacebookFriendsAdapter(getApplicationContext(), friendsWithApp);
			FacebookFriendsAdapter noApp = new FacebookFriendsAdapter(getApplicationContext(), friendsNoApp);
			ListView facebookPickerListViewWithApp = (ListView) findViewById(R.id.facebookPickerListViewWithApp);
			facebookPickerListViewWithApp.setAdapter(withApp);
			ListView facebookPickerListViewNoApp = (ListView) findViewById(R.id.facebookPickerListViewNoApp);
			facebookPickerListViewNoApp.setAdapter(noApp);
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}

		
	}
}

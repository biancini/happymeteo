package com.happymeteo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.happymeteo.models.User;
import com.happymeteo.service.FacebookSessionService;
import com.happymeteo.service.PushNotificationsService;

public class HappyMeteoSkeleton {
	
	private FacebookSessionService facebookSessionService;
	private PushNotificationsService pushNotificationsService;
	private User currentUser;
	private boolean isFacebookSession;
	private SharedPreferences preferences;
	private String ACCESS_TOKEN = "accessToken";
	
	public HappyMeteoSkeleton(Context context) {
		facebookSessionService = new FacebookSessionService();
		pushNotificationsService = new PushNotificationsService();
		currentUser = null;
		isFacebookSession = false;
		preferences = context.getSharedPreferences("HappyMeteo", android.content.Context.MODE_PRIVATE);
	}
	
	public FacebookSessionService getFacebookSessionService() {
		return facebookSessionService;
	}
	
	public PushNotificationsService getPushNotificationsService() {
		return pushNotificationsService;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public boolean isFacebookSession() {
		return isFacebookSession;
	}

	public void setFacebookSession(boolean isFacebookSession) {
		this.isFacebookSession = isFacebookSession;
	}
	
	public String getAccessToken() {
		return preferences.getString(ACCESS_TOKEN, null);
	}
	
	public void setAccessToken(String accessToken) {
		Editor editor = preferences.edit();
		editor.putString(ACCESS_TOKEN, accessToken);
		editor.commit();
	}
}

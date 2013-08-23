package com.happymeteo;

import android.app.Activity;
import android.app.Application;

import com.happymeteo.models.User;
import com.happymeteo.service.FacebookSessionService;
import com.happymeteo.service.PushNotificationsService;

public class HappyMeteoApplication extends Application {
	
	private static FacebookSessionService facebookSessionService;
	private static PushNotificationsService pushNotificationsService;
	private static User currentUser;
	private static boolean isFacebookSession;
	private static Activity mainActivity;

	public static FacebookSessionService getFacebookSessionService() {
		if(facebookSessionService == null) {
			facebookSessionService = new FacebookSessionService();
		}
		return facebookSessionService;
	}
	
	public static PushNotificationsService getPushNotificationsService() {
		if(pushNotificationsService == null) {
			pushNotificationsService = new PushNotificationsService();
		}
		return pushNotificationsService;
	}
	
	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User currentUser) {
		HappyMeteoApplication.currentUser = currentUser;
	}
	
	public static boolean isFacebookSession() {
		return isFacebookSession;
	}

	public static void setFacebookSession(boolean isFacebookSession) {
		HappyMeteoApplication.isFacebookSession = isFacebookSession;
	}
	
	public static Activity getMainActivity() {
		return mainActivity;
	}

	public static void setMainActivity(Activity mainActivity) {
		HappyMeteoApplication.mainActivity = mainActivity;
	}
}

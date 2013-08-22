package com.happymeteo;

import android.app.Application;

import com.happymeteo.service.FacebookSessionService;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.service.SessionService;

public class HappyMeteoApplication extends Application {
	
	private static FacebookSessionService facebookSessionService;
	private static PushNotificationsService pushNotificationsService;
	private static SessionService sessionService;

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
	
	public static SessionService getSessionService() {
		if(sessionService == null) {
			sessionService = new SessionService();
		}
		return sessionService;
	}
}

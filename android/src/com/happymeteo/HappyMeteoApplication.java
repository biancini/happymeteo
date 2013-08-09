package com.happymeteo;

import com.happymeteo.service.FacebookSessionService;
import com.happymeteo.service.PushNotificationsService;

import android.app.Application;
import android.util.Log;

public class HappyMeteoApplication extends Application {
	
	private static FacebookSessionService facebookSessionService;
	private static PushNotificationsService pushNotificationsService;

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
	
	@Override
	public void onCreate() {
		Log.i("HappyMeteo", "onCreate");
		
		/* Initialize PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().initialize(getApplicationContext());
		
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onTerminate() {
		Log.i("HappyMeteo", "onTerminate");
		
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().terminate(getApplicationContext());
		
		// TODO Auto-generated method stub
		super.onTerminate();
	}
}

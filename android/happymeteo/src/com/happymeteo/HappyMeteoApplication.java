package com.happymeteo;

import android.app.Application;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.happymeteo.models.User;
import com.happymeteo.service.FacebookSessionService;
import com.happymeteo.service.PushNotificationsService;

public class HappyMeteoApplication extends Application {
	
	private static HappyMeteoApplication instance;
	
	private FacebookSessionService facebookSessionService;
	private PushNotificationsService pushNotificationsService;
	private User currentUser;
	private boolean isFacebookSession;
	private SharedPreferences preferences;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
		facebookSessionService = new FacebookSessionService();
		pushNotificationsService = new PushNotificationsService();
		currentUser = null;
		isFacebookSession = false;
		preferences = getApplicationContext().getSharedPreferences("HappyMeteo", MODE_PRIVATE);
	}

	public static HappyMeteoApplication i() {
		return instance;
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
	
	public SharedPreferences getSharedPreferences() {
		return preferences;
	}
	
	public boolean isEmulator() {
		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		return manager.getSensorList(Sensor.TYPE_ALL).isEmpty();
	}
}

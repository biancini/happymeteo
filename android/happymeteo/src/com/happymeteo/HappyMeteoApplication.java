package com.happymeteo;

import android.content.Context;

import com.happymeteo.models.User;
import com.happymeteo.service.PushNotificationsService;

public class HappyMeteoApplication {
	
	private static HappyMeteoApplication instance;
	
	public static void initialize(Context context) {
		instance = new HappyMeteoApplication(context);
	}

	public static HappyMeteoApplication i() {
		return instance;
	}
	
	private PushNotificationsService pushNotificationsService;
	private User currentUser;
	
	private void reset() {
		currentUser = null;
	}
	
	public HappyMeteoApplication(Context context) {
		pushNotificationsService = new PushNotificationsService();
		
		reset();
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
		return this.currentUser.getFacebook_id() != null && !this.currentUser.getFacebook_id().equals("");
	}
	
	public void logout(Context context) {
		/* Terminate PushNotificationsService */
		getPushNotificationsService().terminate(context);
		
		/* Reset skeleton */
		reset();
	}
}

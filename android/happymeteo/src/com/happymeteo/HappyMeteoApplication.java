package com.happymeteo;

import android.content.Context;

import com.happymeteo.models.User;

public class HappyMeteoApplication {
	
	private static HappyMeteoApplication instance;
	
	public static void initialize(Context context) {
		instance = new HappyMeteoApplication(context);
	}

	public static HappyMeteoApplication i() {
		return instance;
	}
	
	private User currentUser;
	
	private void reset() {
		currentUser = null;
	}
	
	public HappyMeteoApplication(Context context) {
		reset();
	}
	
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public boolean isFacebookSession() {
		return this.currentUser != null && this.currentUser.getFacebook_id() != null && !this.currentUser.getFacebook_id().equals("");
	}
}

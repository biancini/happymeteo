package com.happymeteo;

import com.happymeteo.models.User;

public class HappyMeteoApplication {
	
	private static User currentUser = null;
	
	public static User getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(User user) {
		currentUser = user;
	}
	
	public static boolean isFacebookSession() {
		return currentUser != null && currentUser.getFacebook_id() != null && !currentUser.getFacebook_id().equals("");
	}
}

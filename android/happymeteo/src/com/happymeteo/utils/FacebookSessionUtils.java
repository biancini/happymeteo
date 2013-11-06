package com.happymeteo.utils;

import java.util.Arrays;

import android.app.Activity;

import com.facebook.Session;
import com.facebook.Session.StatusCallback;

public class FacebookSessionUtils {
	public static Session.StatusCallback getSessionStatusCallback(Activity activity, OnFacebookExecuteListener onFacebookExecuteListener) {
		Session.StatusCallback sessionStatusCallback = new FacebookSessionStatusCallback(activity, onFacebookExecuteListener);
		return sessionStatusCallback;
	}
	
	public static void newNotCachedReadSession(Activity activity, StatusCallback statusCallback) {
		Session session = new Session(activity, null, null, false);
		Session.setActiveSession(session);
		openReadSession(activity, statusCallback, session);
	}
	
	public static void openReadSession(Activity activity, StatusCallback statusCallback, Session session) {
		session.openForSimon(new Session.OpenRequest(activity)
				.setPermissions(Arrays.asList(Const.FACEBOOK_R_PERMISSIONS))
				.setCallback(statusCallback));
	}
}

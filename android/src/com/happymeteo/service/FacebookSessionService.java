package com.happymeteo.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.HappyMeteoApplication;

public class FacebookSessionService {
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Activity activity;
	private Context context;
	
	public void initialize(Context context, Bundle savedInstanceState, Activity activity) {
		this.context = context;
		this.activity = activity;
		
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(context, null,
						statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(context);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				session.openForRead(new Session.OpenRequest(activity).setCallback(statusCallback));
			}
		}
	}
	
	public void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this.activity)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(this.activity, true, statusCallback);
		}
	}

	public void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}
	
	private void updateSession() {
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			/* Logged */
			Toast.makeText(this.context, "Logged with facebook", Toast.LENGTH_SHORT).show();
			
			/* Initialize Push Notification Service */
			HappyMeteoApplication.getPushNotificationsService().initialize(context);
			
			
		} else {
			/* Not logged */
			Toast.makeText(this.context, "Not logged with facebook", Toast.LENGTH_SHORT).show();
		}
	}
	
	public Session.StatusCallback getStatusCallback() {
		return statusCallback;
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			updateSession();
		}
	}

	/*@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_SHORT).show();
		Log.d(CommonUtilities.TAG, "onStart");
		Session.getActiveSession().addCallback(statusCallback);
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_SHORT).show();
		Log.d(CommonUtilities.TAG, "onDestroy");
		Session.getActiveSession().removeCallback(statusCallback);
	}*/
}

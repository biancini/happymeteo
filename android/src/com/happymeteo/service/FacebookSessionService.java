package com.happymeteo.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.HappyMeteoApplication;
import com.happymeteo.MainActivity;
import com.happymeteo.RegisterActivity;
import com.happymeteo.models.User;
import com.happymeteo.utils.ServerUtilities;

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
			Toast.makeText(context, "registrationId: "+HappyMeteoApplication.getPushNotificationsService().getRegistrationId(), Toast.LENGTH_SHORT).show();
			
			/* Call CommonUtilities.FACEBOOK_LOGIN_URL */
			User user = ServerUtilities.facebookLogin(context, Session.getActiveSession().getAccessToken());
			
			if(user != null) {
				if(user.getRegistered() == 0) {
					/* Switch to create account activity if not registered */
					
					// Launch Main Activity
					Intent i = new Intent(context, RegisterActivity.class);
					
					// Registering user on our server					
					// Sending registraiton details to MainActivity
					// i.putExtra("name", name);
					// i.putExtra("email", email);
					activity.startActivity(i);
					activity.finish();
				} else {
					/* Switch to menu activity if registered */
					
					// Launch Main Activity
					Intent i = new Intent(context, MainActivity.class);
					
					// Registering user on our server					
					// Sending registraiton details to MainActivity
					//i.putExtra("name", name);
					//i.putExtra("email", email);
					activity.startActivity(i);
					activity.finish();
				}
			}
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
}
package com.happymeteo.service;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.CreateAccountActivity;
import com.happymeteo.HappyMeteoApplication;
import com.happymeteo.MenuActivity;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
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
			Session.OpenRequest openRequest = new Session.OpenRequest(this.activity);
			openRequest.setCallback(statusCallback);
			List<String> PERMISSION_LIST=Arrays.asList(Const.FACEBOOK_PERMISSION_ARRAY_READ);
			openRequest.setPermissions(PERMISSION_LIST);
			session.openForRead(openRequest);
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
			Log.i(Const.TAG, "accessToken: "+Session.getActiveSession().getAccessToken());
			
			/* Call CommonUtilities.FACEBOOK_LOGIN_URL */
			User user = ServerUtilities.facebookLogin(Session.getActiveSession().getAccessToken());
			
			/* Set current user */
			HappyMeteoApplication.setCurrentUser(user);
			HappyMeteoApplication.setFacebookSession(true);
			
			if(user != null) {
				if(user.getRegistered() == User.USER_NOT_REGISTERED) {
					/* Switch to create account activity if not registered */
					Intent intent = new Intent(context, CreateAccountActivity.class);
					intent.putExtra("facebook_id", user.getFacebook_id());
					intent.putExtra("first_name", user.getFirst_name());
					intent.putExtra("last_name", user.getLast_name());
					intent.putExtra("gender", user.getGender());
					intent.putExtra("email", user.getEmail());
					intent.putExtra("age", user.getAge());
					intent.putExtra("education", user.getEducation());
					intent.putExtra("work", user.getWork());
					intent.putExtra("location", user.getLocation());
					
					Log.i(Const.TAG, "CreateAccountActivity startActivity");
					activity.startActivity(intent);
				} else {
					/* Switch to menu activity if registered */
					Intent intent = new Intent(context, MenuActivity.class);
					activity.startActivity(intent);
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
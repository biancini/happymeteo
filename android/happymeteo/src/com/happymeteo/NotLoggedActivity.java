package com.happymeteo;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;

public abstract class NotLoggedActivity extends SherlockActivity implements
	OnPostExecuteListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Settings.addLoggingBehavior(LoggingBehavior.CACHE);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Settings.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
		
		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(this);
			Session.setActiveSession(session);
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(Const.TAG, this.getClass() + " onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(Const.TAG, this.getClass() + " onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(Const.TAG, this.getClass() + " onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	public void onStart() {
		Log.i(Const.TAG, this.getClass() + " onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(Const.TAG, this.getClass() + " onStop");
		super.onStop();
	}

	@Override
	protected void onResume() {
		Log.i(Const.TAG, this.getClass() + " onResume");
		super.onResume();
	}

	private void openActiveSession(Session.StatusCallback statusCallback, Session session, boolean allowLoginUI) {
		if (session == null) {
			Log.i(Const.TAG, "session null");
			session = new Session(this);
		}
		Session.setActiveSession(session);
		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
			Log.i(Const.TAG, "CREATED_TOKEN_LOADED");
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS)).setCallback(
					statusCallback));
		} else {
			statusCallback.call(session, session.getState(), null);
		}
	}

	public void onFacebookConnect(Session.StatusCallback statusCallback, boolean renew) {
		if (renew) {
			Session session = new Session(this, null, null, false);
			Session.setActiveSession(session);
		}

		Session session = Session.getActiveSession();

		if (!session.isOpened() && !session.isClosed() && session.getState() != SessionState.OPENING) {
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS)).setCallback(
					statusCallback));
		} else {
			if (session.isClosed()) {
				session = new Session(this, null, null, false);
				Session.setActiveSession(session);
				session.openForSimon(new Session.OpenRequest(this)
						.setPermissions(Arrays.asList(Const.FACEBOOK_PERMISSIONS))
						.setCallback(statusCallback));
			} else {
				Log.i(Const.TAG, "onClickLogin openActiveSession");
				openActiveSession(statusCallback, session, false);
			}
		}
	}

	public void onClickLogout() {
		new AlertDialog.Builder(this)
			.setTitle(getApplicationContext().getString(com.happymeteo.R.string.empty))
			.setMessage(getApplicationContext().getString(com.happymeteo.R.string.are_you_sure))
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					/* Clear every data */
					SharedPreferences preferences = getApplicationContext()
							.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
			
					Editor editor = preferences.edit();
					editor.clear();
					editor.commit();

					/* Clear Facebook session */
					//Session session = new Session(AppyMeteoNotLoggedActivity.this, null, null, false);
					if(Session.getActiveSession() != null) {
						Session.getActiveSession().closeAndClearTokenInformation();
					}

					/* Terminate PushNotificationsService */
					PushNotificationsService.terminate(getApplicationContext());

					/* Go to Index */
					invokeActivity(IndexActivity.class);
				}
			})
			.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Do nothing
				}
			}).show();
	}

	public void invokeActivity(Class<? extends Activity> clazz) {
		invokeActivity(clazz, null);
	}

	public void invokeActivity(Class<? extends Activity> clazz, Bundle extras) {
		Log.i(Const.TAG, "invokeActivity: " + this.getClass() + " " + clazz);

		if (!this.getClass().equals(clazz)) {
			Intent intent = new Intent(this, clazz);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (extras != null) intent.putExtras(extras);
			startActivity(intent);
		}
	}
}

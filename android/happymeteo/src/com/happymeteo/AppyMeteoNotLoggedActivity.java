package com.happymeteo;

import java.util.Arrays;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.User;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.ConnectionDetector;
import com.happymeteo.utils.Const;

public class AppyMeteoNotLoggedActivity extends SherlockActivity {
	protected ProgressDialog spinner;
	
	
	private class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.e(Const.TAG, ex.getMessage(), ex);
		}
	}
	
	public void openActiveSession(Session.StatusCallback statusCallback, Session session, boolean allowLoginUI) {
		spinner.show();
		if (session == null) {
			Log.i(Const.TAG, "session null");
			session = new Session(this);
		}
		Session.setActiveSession(session);
		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
			Log.i(Const.TAG, "CREATED_TOKEN_LOADED");
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS))
					.setCallback(statusCallback));
		} else {
			spinner.dismiss();
			statusCallback.call(session, session.getState(), null);
		}
	}
	
	public void onFacebookConnect(Session.StatusCallback statusCallback, boolean renew) {
		if(renew) {
			Session session = new Session(this, null, null, false);
			Session.setActiveSession(session);
		}
		
		Session session = Session.getActiveSession();
		
		if (!session.isOpened() && !session.isClosed() && session.getState() != SessionState.OPENING) {
			spinner.show();
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS))
					.setCallback(statusCallback));
		} else {
			if(session.isClosed()) {
				session = new Session(this, null, null, false);
				Session.setActiveSession(session);
				session.openForSimon(new Session.OpenRequest(this).setPermissions(
						Arrays.asList(Const.FACEBOOK_PERMISSIONS))
						.setCallback(statusCallback));
			} else {
				Log.i(Const.TAG, "onClickLogin openActiveSession");
				openActiveSession(statusCallback, session, false);
			}
		}
	}
	
	public void onClickLogout() {
		User.initialize(getApplicationContext(), "", "", "", "", 0, "", 0, 0, 0, "", 0, 0, 0, 0);
		Session session = new Session(this, null, null, false);
		Session.setActiveSession(session);
		PushNotificationsService.terminate(getApplicationContext());
		invokeActivity(IndexActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(Const.TAG, this.getClass()+" onCreate");
		Thread.setDefaultUncaughtExceptionHandler(
                new DefaultExceptionHandler());
		
		super.onCreate(savedInstanceState);
		
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		/* Check internet */
		if (!cd.isConnectingToInternet()) {
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(this, "Errore di connessione Internet",
					"Connettere Internet per utilizzare Appy Meteo", false,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			return;
		}
		
		spinner = new ProgressDialog(this);
		spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		spinner.setMessage("Connessione a facebook..");
	}
	
	public void invokeActivity(Class<? extends Activity> clazz) {
		invokeActivity(clazz, null);
	}

	public void invokeActivity(Class<? extends Activity> clazz, Bundle extras) {
		Log.i(Const.TAG, "invokeActivity: "+this.getClass()+" "+clazz);
		
		if(!this.getClass().equals(clazz)) {
			Intent intent = new Intent(this, clazz);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if(extras != null) {
				intent.putExtras(extras);
			}
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService
		HappyMeteoApplication.getPushNotificationsService().terminate(getApplicationContext()); */

		Log.i(Const.TAG, this.getClass()+" onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(Const.TAG, this.getClass()+" onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(Const.TAG, this.getClass()+" onNewIntent");
		super.onNewIntent(intent);
	}
	
	@Override
	public void onStart() {
		Log.i(Const.TAG, this.getClass()+" onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(Const.TAG, this.getClass()+" onStop");
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		Log.i(Const.TAG, this.getClass()+" onResume");
		super.onResume();
	}
	
	
}

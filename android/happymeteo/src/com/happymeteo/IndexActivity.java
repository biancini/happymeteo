package com.happymeteo;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class IndexActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener {
	
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private ProgressDialog spinner;
	
	public void openActiveSession(Session session, boolean allowLoginUI) {
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
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_index);
		super.onCreate(savedInstanceState);
		
		HappyMeteoApplication.initialize(this);
		
		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		Button btnLoginHappyMeteo = (Button) findViewById(R.id.btnLoginHappyMeteo);
		Button btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				invokeActivity(CreateAccountActivity.class);
			}
		});

		btnLoginHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				invokeActivity(NormalLoginActivity.class);
			}
		});

		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				onClickLogin();
			}
		});

		spinner = new ProgressDialog(this);
		spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		spinner.setMessage("Connession facebook..");

		Settings.addLoggingBehavior(LoggingBehavior.CACHE);
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Settings.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
		
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				Log.i(Const.TAG, "Session.restoreSession savedInstanceState");
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			openActiveSession(session, false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	private void updateView(Session session) {
		spinner.setMessage("state: "+session.getState());
		
		if (session.isOpened()) {
			spinner.dismiss();
			ServerUtilities.facebookLogin(this, this, Session.getActiveSession().getAccessToken());
		} else {
			Log.i(Const.TAG, "not opened session permissions: "+session.getPermissions());
			Log.i(Const.TAG, "not opened session accessToken: "+session.getAccessToken());
			spinner.setMessage("not opened: "+session.getState());
			Log.i(Const.TAG, "not opened");
		}
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		
		Log.i(Const.TAG, "onClickLogin session.isOpened(): "+session.isOpened());
		Log.i(Const.TAG, "onClickLogin session.isClosed(): "+session.isClosed());
		Log.i(Const.TAG, "onClickLogin state: "+session.getState());
		Log.i(Const.TAG, "onClickLogin accessToken: "+session.getAccessToken());
		Log.i(Const.TAG, "onClickLogin permissions: "+session.getPermissions());
		
		if (!session.isOpened() && !session.isClosed()) {
			spinner.show();
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS))
					.setCallback(statusCallback));
		} else {
			Log.i(Const.TAG, "onClickLogin openActiveSession");
			openActiveSession(session, false);
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.i(Const.TAG, "SessionStatusCallback state: " + state);

			// If there is an exception...
			if (exception != null) {
				spinner.setMessage(exception.getMessage());
				return;
			}

			updateView(session);
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) {
			spinner.show();
			Session session = new Session(this, null, null, false);
			Session.setActiveSession(session);
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS))
					.setCallback(statusCallback));
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);
	
				User user = new User(jsonObject);
	
				if (user != null) {
					HappyMeteoApplication.i().setCurrentUser(user);
	
					if (user.getRegistered() == User.USER_NOT_REGISTERED) {
						invokeActivity(CreateAccountActivity.class);
					} else {
						invokeActivity(HappyMeteoActivity.class);
					}
					return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

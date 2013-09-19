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
import com.facebook.Session.Builder;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class IndexActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener {
	
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private boolean grantedPublishPermission = false;
	private boolean openActiveSession = false;
	private boolean closeAndClear = false;
	private ProgressDialog spinner;

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
		spinner.setMessage(this.getString(com.happymeteo.R.string.loading));
		spinner.show();

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
			if (session == null) {
				Log.i(Const.TAG, "session null");
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				Log.i(Const.TAG, "CREATED_TOKEN_LOADED");
				openActiveSession = true;
				Session.openActiveSession(this, true, statusCallback);
			} else {
				spinner.dismiss();
			}
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
		
		if(closeAndClear && session.isClosed()) {
			Session newSession = new Session(this);
			Session.setActiveSession(newSession);
			openActiveSession = !newSession.getPermissions().isEmpty();
			newSession.openForRead(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSION_READ_ARRAY))
					.setCallback(statusCallback));
			
			Log.i(Const.TAG, "closeAndClear error state: "+newSession.getState());
			Log.i(Const.TAG, "closeAndClear error accessToken: "+newSession.getAccessToken());
			Log.i(Const.TAG, "closeAndClear error permissions: "+newSession.getPermissions());
			return;
		}
		
		if (session.isOpened()) {
			if (!grantedPublishPermission) {
				Log.i(Const.TAG, "granted");
				spinner.setMessage("granted");
				Log.i(Const.TAG, "granted permissions: "+session.getPermissions());
				Log.i(Const.TAG, "granted accessToken: "+session.getAccessToken());
				
				if(openActiveSession) {
					Log.i(Const.TAG, "granted openForPublish");
					Session session2 = new Builder(this).build();
					session2.openForPublish(new Session.OpenRequest(this).setPermissions(
							Arrays.asList(Const.FACEBOOK_PERMISSION_PUBLISH_ARRAY))
							.setCallback(statusCallback));
				} else {
					Log.i(Const.TAG, "granted requestNewPublishPermissions");
					session.requestNewPublishPermissions(new NewPermissionsRequest(
							this,
							Arrays.asList(Const.FACEBOOK_PERMISSION_PUBLISH_ARRAY))
							.setCallback(statusCallback));
				}
				
				grantedPublishPermission = true;
			} else {
				spinner.dismiss();
				ServerUtilities.facebookLogin(this, this, Session.getActiveSession().getAccessToken());
			}
		} else {
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
			Log.i(Const.TAG, "onClickLogin openForRead");
			openActiveSession = !session.getPermissions().isEmpty();
			session.openForRead(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSION_READ_ARRAY))
					.setCallback(statusCallback));
		} else {
			Log.i(Const.TAG, "onClickLogin openActiveSession");
			openActiveSession = true;
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private void onClickLogout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.i(Const.TAG, "state: " + state);

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
		
		Log.e(Const.TAG, "exception: "+exception);
		
		if(exception != null) {
			Session session = Session.getActiveSession();
			session.closeAndClearTokenInformation();
			closeAndClear = true;
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

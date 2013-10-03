package com.happymeteo;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class IndexActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener {
	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_index);
		super.onCreate(savedInstanceState);

		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putBoolean("create", true);
				invokeActivity(CreateAccountActivity.class, extras);
			}
		});

		Button btnLoginHappyMeteo = (Button) findViewById(R.id.btnLoginHappyMeteo);
		btnLoginHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				invokeActivity(NormalLoginActivity.class);
			}
		});

		Button btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				onFacebookConnect(statusCallback, false);
			}
		});
		
		Log.i(Const.TAG, "user_id: "+SessionCache.getUser_id(getApplicationContext()));
		if(SessionCache.isInitialized(getApplicationContext())) {
			invokeActivity(HappyMeteoActivity.class);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Session session = Session.getActiveSession();
		session.addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session session = Session.getActiveSession();
		session.removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		session.onActivityResult(this, requestCode,
					resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
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

	private void updateView(Session session) {
		spinner.setMessage("state: " + session.getState());

		if (session.isOpened()) {
			spinner.dismiss();
			Log.i(Const.TAG, "permissions: "+Session.getActiveSession().getPermissions());
			ServerUtilities.facebookLogin(this, this, Session
					.getActiveSession().getAccessToken());
		} else {
			spinner.setMessage("not opened: " + session.getState());
			Log.i(Const.TAG, "not opened");
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) {
			spinner.show();
			Session session = new Session(this, null, null, false);
			Session.setActiveSession(session);
			session.openForSimon(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSIONS)).setCallback(
					statusCallback));
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);

				SessionCache.initialize(this, jsonObject);
				if (SessionCache.getRegistered(this) == SessionCache.USER_NOT_REGISTERED) {
					Bundle extras = new Bundle();
					extras.putBoolean("create", true);
					invokeActivity(CreateAccountActivity.class, extras);
				} else {
					invokeActivity(HappyMeteoActivity.class);
				}
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

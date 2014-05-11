package com.happymeteo;

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
import com.happymeteo.meteo.MeteoActivity;
import com.happymeteo.models.SessionCache;
import com.happymeteo.settings.CreateAccountActivity;
import com.happymeteo.settings.NormalLoginActivity;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.FacebookSessionUtils;
import com.happymeteo.utils.OnFacebookExecuteListener;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.ServerUtilities;

public class IndexActivity extends NotLoggedActivity implements
		OnPostExecuteListener, OnFacebookExecuteListener {
	private Session.StatusCallback statusCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_index);
		super.onCreate(savedInstanceState);
		
		setPersistentActivity(true);

		/* Get Facebook Status Callback */
		statusCallback = FacebookSessionUtils.getSessionStatusCallback(this, this);

		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				invokeActivity(CreateAccountActivity.class);
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

		if (SessionCache.isInitialized(getApplicationContext())) {
			invokeActivity(MeteoActivity.class);
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
		session.onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	public void OnFacebookExecute(Session session, SessionState state) {
		if(!session.getAccessToken().isEmpty()) {
			ServerUtilities.facebookLogin(this, session.getAccessToken());
		}
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) {
			FacebookSessionUtils.newNotCachedReadSession(this, statusCallback);
		} else {
			try {
				JSONObject jsonObject = new JSONObject(result);

				SessionCache.initialize(this, jsonObject);
				if (SessionCache.getRegistered(this) == SessionCache.USER_NOT_REGISTERED) {
					invokeActivity(CreateAccountActivity.class);
				} else {
					invokeActivity(MeteoActivity.class);
				}
				return;
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}
	}
}

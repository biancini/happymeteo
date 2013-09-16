package com.happymeteo;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.happymeteo.models.User;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.ConnectionDetector;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.onPostExecuteListener;

public class IndexActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener {

	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private boolean grantedPublishPermission = false;
	private ProgressDialog spinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		setContentView(R.layout.activity_index);
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
		spinner.setMessage(this.getString(com.happymeteo.R.string.loading));

		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback,
						savedInstanceState);
			}
			if (session == null) {
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				grantedPublishPermission = true;
				Session.openActiveSession(this, true, statusCallback);
			}
		}

		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		Button btnLoginHappyMeteo = (Button) findViewById(R.id.btnLoginHappyMeteo);
		Button btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, CreateAccountActivity.class);
				context.startActivity(intent);
			}
		});

		btnLoginHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, NormalLoginActivity.class);
				context.startActivity(intent);
			}
		});

		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				// HappyMeteoApplication.i().getFacebookSessionService().onClickLogin(activity);
				// String accessToken =
				// Session.getActiveSession().getAccessToken();
				//onClickLogout();
				onClickLogin();
			}
		});
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
		if (session.isOpened()) {
			if (!grantedPublishPermission) {
				spinner.setMessage("granted");
				session.requestNewPublishPermissions(new NewPermissionsRequest(
						this,
						Arrays.asList(Const.FACEBOOK_PERMISSION_PUBLISH_ARRAY))
						.setCallback(statusCallback));
				grantedPublishPermission = true;
			} else {
				spinner.setMessage("login");
				/*String accessToken = Session.getActiveSession()
						.getAccessToken();
				HappyMeteoApplication.i().setAccessToken(accessToken);
				ServerUtilities.facebookLogin(this, this, accessToken);*/
			}
		} else {
			spinner.setMessage("not opened: "+session.getState());
		}
	}

	private void onClickLogin() {
		spinner.show();
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setPermissions(
					Arrays.asList(Const.FACEBOOK_PERMISSION_READ_ARRAY))
					.setCallback(statusCallback));
		} else {
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
	public void onPostExecute(int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);

			// Get Long Lived Token
			/*
			 * String access_token = jsonObject.getString("access_token");
			 * Session session = Session.getActiveSession(); AccessToken
			 * accessToken = AccessToken
			 * .createFromExistingAccessToken(access_token, null, null, null,
			 * Arrays.asList(Const.FACEBOOK_PERMISSION_ARRAY));
			 * session.open(accessToken, statusCallback);
			 */

			User user = new User(jsonObject);

			if (user != null) {
				HappyMeteoApplication.i().setFacebookSession(true);
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

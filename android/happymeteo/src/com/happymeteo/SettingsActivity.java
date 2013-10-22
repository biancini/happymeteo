package com.happymeteo;

import org.jraf.android.backport.switchwidget.Switch;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.OnGetExecuteListener;
import com.happymeteo.utils.OnPostExecuteListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingsActivity extends AppyMeteoLoggedActivity implements OnGetExecuteListener, OnPostExecuteListener {
	private TextView settingsFacebookText;
	private Switch settingsFacebookSwitch;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private boolean nextTime = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_settings);
		super.onCreate(savedInstanceState);
		
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
		
		settingsFacebookText  = (TextView) findViewById(R.id.settingsFacebookText);
		settingsFacebookSwitch  = (Switch) findViewById(R.id.settingsFacebookSwitch);
		
		btnCreateUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				invokeActivity(SettingsUtenteActivity.class);
			}
		});
		
		btnChangePassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				invokeActivity(ChangePasswordActivity.class);
			}
		});
		
		changeFacebookId(SessionCache.getFacebook_id(this), false);
		
		settingsFacebookSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				final boolean checked = isChecked;
				Log.i(Const.TAG, "nextTime: " + nextTime);
				
				if(nextTime) {
					new AlertDialog.Builder(SettingsActivity.this)
					.setTitle(getApplicationContext().getString(com.happymeteo.R.string.empty))
					.setMessage(getApplicationContext().getString(com.happymeteo.R.string.are_you_sure))
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (checked) {
								onFacebookConnect(statusCallback, true);
							} else {
								settingsFacebookText.setText(R.string.link_user_to_facebook);
								String userId = SessionCache.getUser_id(SettingsActivity.this);
								ServerUtilities.updateFacebook(SettingsActivity.this, userId, "");
							}
						}
					})
					.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							nextTime = false;
							settingsFacebookSwitch.setChecked(!settingsFacebookSwitch.isChecked());
						}
					}).show();
				} else {
					nextTime = true;
				}
			}
		});
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
		public void call(Session session, SessionState state, Exception exception) {
			Log.i(Const.TAG, "SessionStatusCallback state: " + state);

			// If there is an exception...
			if (exception != null) {
				Log.e(Const.TAG, "Exception", exception);
				return;
			}

			updateView(session);
		}
	}

	private void updateView(Session session) {
		Log.i(Const.TAG, "state: " + session.getState());
		Log.i(Const.TAG, "session.isOpened(): " + session.isOpened());

		if (session.isOpened()) {
			String accessToken = session.getAccessToken();
			Log.i(Const.TAG, "accessToken: " + accessToken);
			String serverUrl = "https://graph.facebook.com/me?access_token="
					+ accessToken;
			new GetRequest(this, this).execute(serverUrl);
		}
	}
	
	@Override
	public void onGetExecute(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			String facebookId = jsonObject.getString("id");
			String userId = SessionCache.getUser_id(this);
			ServerUtilities.updateFacebook(SettingsActivity.this, userId, facebookId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) return;

		try {
			JSONObject jsonObject = new JSONObject(result);
			String facebook_id = jsonObject.getString("facebook_id");
			changeFacebookId(facebook_id, true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void changeFacebookId(String facebook_id, boolean changeValue) {
		Log.i(Const.TAG, "changeValue: "+changeValue);
		if(changeValue) {
			Log.i(Const.TAG, "facebook_id: "+facebook_id);
			SessionCache.setFacebook_id(this, facebook_id);
			Log.i(Const.TAG, "facebook_id after: "+SessionCache.getFacebook_id(this));
		}
		settingsFacebookSwitch.setChecked(facebook_id != null && !facebook_id.equals(""));
		if (facebook_id != null && !facebook_id.equals("")) {
			settingsFacebookText.setText(R.string.unlink_user_to_facebook);
		} else {
			settingsFacebookText.setText(R.string.link_user_to_facebook);
		}
	}
}

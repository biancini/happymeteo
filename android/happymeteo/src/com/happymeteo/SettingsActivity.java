package com.happymeteo;

import org.jraf.android.backport.switchwidget.Switch;
import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.IsEmail;
import ua.org.zasadnyy.zvalidations.validations.IsPositiveInteger;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onGetExecuteListener;
import com.happymeteo.utils.onPostExecuteListener;

public class SettingsActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener, onGetExecuteListener {
	private String user_id;
	private String facebook_id;
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private EditText create_account_fist_name;
	private EditText create_account_last_name;
	private Spinner create_account_gender;
	private EditText create_account_email;
	private Spinner create_account_age;
	private Spinner create_account_education;
	private Spinner create_account_work;
	private EditText create_account_cap;
	
	private TextView settingsFacebookText;
	private Switch settingsFacebookSwitch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.activity = this;
		this.onPostExecuteListener = this;

		this.user_id = "";
		this.facebook_id = "";
		
		create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		create_account_email = (EditText) findViewById(R.id.create_account_email);
		create_account_age = (Spinner) findViewById(R.id.create_account_age);
		create_account_education = (Spinner) findViewById(R.id.create_account_education);
		create_account_work = (Spinner) findViewById(R.id.create_account_work);
		create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		
		settingsFacebookText  = (TextView) findViewById(R.id.settingsFacebookText);
		settingsFacebookSwitch  = (Switch) findViewById(R.id.settingsFacebookSwitch);
		Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
		
		this.user_id = SessionCache.getUser_id(this);
		this.facebook_id = SessionCache.getFacebook_id(this);
		create_account_fist_name.setText(SessionCache.getFirst_name(this));
		create_account_last_name.setText(SessionCache.getLast_name(this));
		create_account_gender.setSelection(SessionCache.getGender(this));
		create_account_email.setText(SessionCache.getEmail(this));
		create_account_age.setSelection(SessionCache.getAge(this));
		create_account_education.setSelection(SessionCache.getEducation(this));
		create_account_work.setSelection(SessionCache.getWork(this));
		create_account_cap.setText(SessionCache.getCap(this));
		
		final Form mForm = new Form();
	    mForm.addField(Field.using(create_account_fist_name).validate(NotEmpty.build(this)));
	    mForm.addField(Field.using(create_account_last_name).validate(NotEmpty.build(this)));
	    mForm.addField(Field.using(create_account_email).validate(NotEmpty.build(this)).validate(IsEmail.build(this)));
	    mForm.addField(Field.using(create_account_cap).validate(NotEmpty.build(this)).validate(IsPositiveInteger.build(this)));
		
	    settingsFacebookSwitch.setChecked(facebook_id != null && !facebook_id.equals(""));
		if (facebook_id != null && !facebook_id.equals("")) {
			settingsFacebookText.setText(R.string.unlink_user_to_facebook);
		} else {
			settingsFacebookText.setText(R.string.link_user_to_facebook);
		}

		settingsFacebookSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					onFacebookConnect(statusCallback, true);
				} else {
					facebook_id = "";
					settingsFacebookText.setText(R.string.link_user_to_facebook);
				}
			}
		});

		btnCreateUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(mForm.isValid()) {
					ServerUtilities.createAccount(
							onPostExecuteListener, activity, user_id, facebook_id, 
							create_account_fist_name.getText().toString(), 
							create_account_last_name.getText().toString(), 
							create_account_gender.getSelectedItemPosition(),
							create_account_email.getText().toString(),
							create_account_age.getSelectedItemPosition(),
							create_account_education.getSelectedItemPosition(),
							create_account_work.getSelectedItemPosition(),
							create_account_cap.getText().toString(), "");
				}
			}
		});
		
		btnChangePassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				invokeActivity(ChangePasswordActivity.class);
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
		public void call(Session session, SessionState state,
				Exception exception) {
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
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception == null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) { // CONFIRMED_OR_FACEBOOK
					SessionCache.initialize(this, jsonObject.getString("user_id"), facebook_id,
							create_account_fist_name.getText().toString(),
							create_account_last_name.getText().toString(),
							create_account_gender.getSelectedItemPosition(),
							create_account_email.getText().toString(),
							create_account_age.getSelectedItemPosition(),
							create_account_education.getSelectedItemPosition(),
							create_account_work.getSelectedItemPosition(),
							create_account_cap.getText().toString(),
							SessionCache.USER_REGISTERED, 1, 1, 1);
					invokeActivity(HappyMeteoActivity.class);
				} else { // NOT_CONFIRMED
					AlertDialogManager alert = new AlertDialogManager();
					alert.showAlertDialog(
							activity,
							"Account non verificato",
							"Presto verra  inviata una email di conferma all'email indicata",
							true, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onGetExecute(String result, Exception exception) {
		Log.i(Const.TAG, "onGetExecute: " + result);
		spinner.dismiss();
		
		if(exception != null) {
			return;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			facebook_id = jsonObject.getString("id");
			settingsFacebookSwitch.setChecked(facebook_id != null && !facebook_id.equals(""));
			if (facebook_id != null && !facebook_id.equals("")) {
				settingsFacebookText.setText(R.string.unlink_user_to_facebook);
			} else {
				settingsFacebookText.setText(R.string.link_user_to_facebook);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

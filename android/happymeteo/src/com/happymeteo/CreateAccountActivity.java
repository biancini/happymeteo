package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.IsEmail;
import ua.org.zasadnyy.zvalidations.validations.IsPassword;
import ua.org.zasadnyy.zvalidations.validations.IsPositiveInteger;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.GetRequest;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onGetExecuteListener;
import com.happymeteo.utils.onPostExecuteListener;

public class CreateAccountActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener, onGetExecuteListener {
	private String user_id;
	private String facebook_id;
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Button btnCreateUserFacebook;
	private EditText create_account_fist_name;
	private EditText create_account_last_name;
	private Spinner create_account_gender;
	private EditText create_account_email;
	private EditText create_account_password;
	private Spinner create_account_age;
	private Spinner create_account_education;
	private Spinner create_account_work;
	private EditText create_account_cap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		boolean create = getIntent().getExtras().getBoolean("create", true);

		this.activity = this;
		this.onPostExecuteListener = this;

		this.user_id = "";
		this.facebook_id = "";
		
		String welcome = "<u><b>Registrati</b> su appymeteo!</u>";
		
		TextView create_account_welcome = (TextView) findViewById(R.id.create_account_welcome);
		create_account_welcome.setText(Html.fromHtml(welcome));

		create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		create_account_email = (EditText) findViewById(R.id.create_account_email);
		create_account_password = (EditText) findViewById(R.id.create_account_password);
		create_account_age = (Spinner) findViewById(R.id.create_account_age);
		create_account_education = (Spinner) findViewById(R.id.create_account_education);
		create_account_work = (Spinner) findViewById(R.id.create_account_work);
		create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		btnCreateUserFacebook  = (Button) findViewById(R.id.btnCreateUserFacebook);
		
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
		
		if (SessionCache.isFacebookSession(this) || !create) {
			create_account_password.setVisibility(View.GONE);
		} else {
			mForm.addField(Field.using(create_account_password).validate(NotEmpty.build(this)).validate(IsPassword.build(this)));
		}

		if (create) {
			btnCreateUserFacebook.setVisibility(View.GONE);
		} else {
			btnCreateUser.setText(R.string.modify_account);
		}

		if (!facebook_id.equals("")) {
			btnCreateUserFacebook.setText(R.string.unlink_user_to_facebook);
		} else {
			btnCreateUserFacebook.setText(R.string.link_user_to_facebook);
		}

		btnCreateUserFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (facebook_id.equals("")) {
					onFacebookConnect(statusCallback, true);
				} else {
					facebook_id = "";
					btnCreateUserFacebook.setText(R.string.link_user_to_facebook);
				}
			}
		});

		btnCreateUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(mForm.isValid()) {
					String password;
					try {
						password = SHA1.hexdigest(Const.PASSWORD_SECRET_KEY, create_account_password.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
						password = "";
					}
	
					ServerUtilities.createAccount(
							onPostExecuteListener, activity, user_id, facebook_id, 
							create_account_fist_name.getText().toString(), 
							create_account_last_name.getText().toString(), 
							create_account_gender.getSelectedItemPosition(),
							create_account_email.getText().toString(),
							create_account_age.getSelectedItemPosition(),
							create_account_education.getSelectedItemPosition(),
							create_account_work.getSelectedItemPosition(),
							create_account_cap.getText().toString(), password);
	
					Log.i(Const.TAG, "facebook_id: "+SessionCache.getFacebook_id(view.getContext()));
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
		
		if(exception == null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				facebook_id = jsonObject.getString("id");
				if (!facebook_id.equals("")) {
					btnCreateUserFacebook.setText(R.string.unlink_user_to_facebook);
				} else {
					btnCreateUserFacebook.setText(R.string.link_user_to_facebook);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.happymeteo.models.User;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class CreateAccountActivity extends AppyMeteoNotLoggedActivity implements onPostExecuteListener {
	private String user_id;
	private String facebook_id;
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

		this.activity = this;
		this.onPostExecuteListener = this;

		this.user_id = "";
		this.facebook_id = "";

		final EditText create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		final EditText create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		final Spinner create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		final EditText create_account_email = (EditText) findViewById(R.id.create_account_email);
		final EditText create_account_password = (EditText) findViewById(R.id.create_account_password);
		final Spinner create_account_age = (Spinner) findViewById(R.id.create_account_age);
		final Spinner create_account_education = (Spinner) findViewById(R.id.create_account_education);
		final Spinner create_account_work = (Spinner) findViewById(R.id.create_account_work);
		final EditText create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		//Button btnCreateUserFacebook = (Button) findViewById(R.id.btnCreateUserFacebook);

		if (User.isFacebookSession(this)) {
			create_account_password.setVisibility(View.GONE);
		}

		this.user_id = User.getUser_id(this);
		this.facebook_id = User.getFacebook_id(this);
		create_account_fist_name.setText(User.getFirst_name(this));
		create_account_last_name.setText(User.getLast_name(this));
		create_account_gender.setSelection(User.getGender(this));
		create_account_email.setText(User.getEmail(this));
		create_account_age.setSelection(User.getAge(this));
		create_account_education.setSelection(User.getEducation(this));
		create_account_work.setSelection(User.getWork(this));
		create_account_cap.setText(User.getCap(this));

		if (!this.user_id.equals("")) {
			btnCreateUser.setText(R.string.modify_account);
		}

		/*if (!facebook_id.equals("")) {
			btnCreateUserFacebook.setText(R.string.unlink_user_to_facebook);
		} else {
			btnCreateUserFacebook.setText(R.string.link_user_to_facebook);
		}

		btnCreateUserFacebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (facebook_id.equals("")) {
					HappyMeteoApplication.getFacebookSessionService()
							.openConnession(onCompleteListener);

				} else {
					facebook_id = "";
					btnCreateUserFacebook
							.setText(R.string.link_user_to_facebook);
				}
			}
		});*/

		btnCreateUser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Log.i(Const.TAG, "this.facebook_id: " + facebook_id);
				Log.i(Const.TAG, "create_account_fist_name: "
						+ create_account_fist_name.getText());
				Log.i(Const.TAG, "create_account_last_name: "
						+ create_account_last_name.getText());
				Log.i(Const.TAG, "create_account_gender: "
						+ create_account_gender.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_email: "
						+ create_account_email.getText());
				Log.i(Const.TAG,
						"create_account_age: "
								+ create_account_age.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_education: "
						+ create_account_education.getSelectedItemPosition());
				Log.i(Const.TAG,
						"create_account_work: "
								+ create_account_work.getSelectedItemPosition());
				Log.i(Const.TAG,
						"create_account_cap: " + create_account_cap.getText());

				String password;
				try {
					password = SHA1.hexdigest(create_account_password.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
					password = "";
				}

				ServerUtilities.createAccount(onPostExecuteListener, activity,
						user_id, facebook_id, create_account_fist_name
								.getText().toString(), create_account_last_name
								.getText().toString(), create_account_gender
								.getSelectedItemPosition(),
						create_account_email.getText().toString(),
						create_account_age.getSelectedItemPosition(),
						create_account_education.getSelectedItemPosition(),
						create_account_work.getSelectedItemPosition(),
						create_account_cap.getText().toString(), password);

				User.initialize(view.getContext(), facebook_id, create_account_fist_name
						.getText().toString(), create_account_last_name
						.getText().toString(), create_account_gender
						.getSelectedItemPosition(), create_account_email
						.getText().toString(), create_account_age
						.getSelectedItemPosition(), create_account_education
						.getSelectedItemPosition(), create_account_work
						.getSelectedItemPosition(), create_account_cap
						.getText().toString(), User.USER_REGISTERED, 1, 1, 1);
			}
		});
	}

	/*@Override
	public void onComplete(Bundle values, FacebookException error,
			AppyMeteoNotLoggedActivity caller) {
		if (values != null) {
			String accessToken = values.getString("access_token");
			String serverUrl = "https://graph.facebook.com/me?access_token="
					+ accessToken;
			new GetRequest(this).execute(serverUrl);
		}
	}*/

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) { // CONFIRMED_OR_FACEBOOK
				User.setUser_id(this, jsonObject.getString("user_id"));
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

package com.happymeteo.settings;

import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.IsEmail;
import ua.org.zasadnyy.zvalidations.validations.IsPassword;
import ua.org.zasadnyy.zvalidations.validations.IsPositiveInteger;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.happymeteo.Activity;
import com.happymeteo.NotLoggedActivity;
import com.happymeteo.R;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;

public class CreateAccountActivity extends NotLoggedActivity implements OnPostExecuteListener {
	private String user_id;
	private String facebook_id;
	private EditText create_account_fist_name;
	private EditText create_account_last_name;
	private Spinner create_account_gender;
	private EditText create_account_email;
	private EditText create_account_password;
	private EditText create_account_confirm_password;
	private Spinner create_account_age;
	private Spinner create_account_education;
	private Spinner create_account_work;
	private EditText create_account_cap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);

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
		create_account_confirm_password = (EditText) findViewById(R.id.create_account_confirm_password);
		create_account_age = (Spinner) findViewById(R.id.create_account_age);
		create_account_education = (Spinner) findViewById(R.id.create_account_education);
		create_account_work = (Spinner) findViewById(R.id.create_account_work);
		create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);

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
		mForm.addField(Field.using(create_account_fist_name).validate(
				NotEmpty.build(this)));
		mForm.addField(Field.using(create_account_last_name).validate(
				NotEmpty.build(this)));
		mForm.addField(Field.using(create_account_email)
				.validate(NotEmpty.build(this)).validate(IsEmail.build(this)));
		mForm.addField(Field.using(create_account_cap)
				.validate(NotEmpty.build(this))
				.validate(IsPositiveInteger.build(this)));

		if (SessionCache.isFacebookSession(this)) {
			create_account_password.setVisibility(View.GONE);
			create_account_confirm_password.setVisibility(View.GONE);
		} else {
			mForm.addField(Field.using(create_account_password)
					.validate(NotEmpty.build(this))
					.validate(IsPassword.build(this)));
			mForm.addField(Field.using(create_account_confirm_password)
					.validate(NotEmpty.build(this)));
		}

		btnCreateUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mForm.isValid()) {

					if (create_account_password
							.getText()
							.toString()
							.equals(create_account_confirm_password.getText().toString())) {
						
						String password = "";

						if (!SessionCache.isFacebookSession(view.getContext())) {
							try {
								password = SHA1.hexdigest(
										Const.PASSWORD_SECRET_KEY,
										create_account_password.getText()
												.toString());
							} catch (Exception e) {
								Log.e(Const.TAG, e.getMessage(), e);
								password = "";
							}
						}

						ServerUtilities.createAccount(
								CreateAccountActivity.this,
								user_id,
								facebook_id,
								create_account_fist_name.getText().toString(),
								create_account_last_name.getText().toString(),
								create_account_gender.getSelectedItemPosition(),
								create_account_email.getText().toString(),
								create_account_age.getSelectedItemPosition(),
								create_account_education.getSelectedItemPosition(),
								create_account_work.getSelectedItemPosition(),
								create_account_cap.getText().toString(),
								password);

						Log.i(Const.TAG, "facebook_id: " + SessionCache.getFacebook_id(view.getContext()));
					} else {
						create_account_confirm_password
							.setError(getApplicationContext().getString(R.string.error_password));
					}
				}
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception == null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) { // CONFIRMED_OR_FACEBOOK
					SessionCache.initialize(this,
							jsonObject.getString("user_id"), facebook_id,
							create_account_fist_name.getText().toString(),
							create_account_last_name.getText().toString(),
							create_account_gender.getSelectedItemPosition(),
							create_account_email.getText().toString(),
							create_account_age.getSelectedItemPosition(),
							create_account_education.getSelectedItemPosition(),
							create_account_work.getSelectedItemPosition(),
							create_account_cap.getText().toString(),
							SessionCache.USER_REGISTERED, 1, 1, 1);
					invokeActivity(Activity.class);
				} else { // NOT_CONFIRMED
					AlertDialogManager.showNotification(this,
							R.string.not_confirmed_user_notification_title,
							R.string.not_confirmed_user_notification_msg,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
				}
			} catch (JSONException e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}
	}
}

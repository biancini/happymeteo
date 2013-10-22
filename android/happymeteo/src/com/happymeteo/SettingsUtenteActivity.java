package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.IsEmail;
import ua.org.zasadnyy.zvalidations.validations.IsPositiveInteger;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.OnPostExecuteListener;

public class SettingsUtenteActivity extends AppyMeteoLoggedActivity implements OnPostExecuteListener {
	private String user_id;
	private EditText create_account_fist_name;
	private EditText create_account_last_name;
	private Spinner create_account_gender;
	private EditText create_account_email;
	private Spinner create_account_age;
	private Spinner create_account_education;
	private Spinner create_account_work;
	private EditText create_account_cap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_settings_utente);
		super.onCreate(savedInstanceState);

		create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		create_account_email = (EditText) findViewById(R.id.create_account_email);
		create_account_age = (Spinner) findViewById(R.id.create_account_age);
		create_account_education = (Spinner) findViewById(R.id.create_account_education);
		create_account_work = (Spinner) findViewById(R.id.create_account_work);
		create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		
		this.user_id = SessionCache.getUser_id(this);
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
		
	    btnCreateUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mForm.isValid()) {
					ServerUtilities.createAccount(
							SettingsUtenteActivity.this, user_id, 
							SessionCache.getFacebook_id(view.getContext()), 
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
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception == null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) { // CONFIRMED_OR_FACEBOOK
					String facebook_id = SessionCache.getFacebook_id(this);
					SessionCache.initialize(this, jsonObject.getString("user_id"), 
							facebook_id,
							create_account_fist_name.getText().toString(),
							create_account_last_name.getText().toString(),
							create_account_gender.getSelectedItemPosition(),
							create_account_email.getText().toString(),
							create_account_age.getSelectedItemPosition(),
							create_account_education.getSelectedItemPosition(),
							create_account_work.getSelectedItemPosition(),
							create_account_cap.getText().toString(),
							SessionCache.USER_REGISTERED, jsonObject.getInt("today"), 
							jsonObject.getInt("yesterday"), jsonObject.getInt("tomorrow"));
					invokeActivity(HappyMeteoActivity.class);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

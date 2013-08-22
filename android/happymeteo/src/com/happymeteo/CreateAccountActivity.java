package com.happymeteo;

import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class CreateAccountActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		/* Get parameters */
		int facebook_id = getIntent().getIntExtra("facebook_id", 0);
		String first_name = getIntent().getStringExtra("first_name") == null ? "" : getIntent().getStringExtra("first_name");
		String last_name = getIntent().getStringExtra("last_name") == null ? "" : getIntent().getStringExtra("last_name");
		int gender = getIntent().getIntExtra("gender", 0);
		String email = getIntent().getStringExtra("email") == null ? "" : getIntent().getStringExtra("email");
		int age = getIntent().getIntExtra("age", 0);
		int education = getIntent().getIntExtra("education", 0);
		int work = getIntent().getIntExtra("work", 0);
		String location = getIntent().getStringExtra("location") == null ? "" : getIntent().getStringExtra("location");
		
		final EditText create_account_facebook = (EditText) findViewById(R.id.create_account_facebook);
		create_account_facebook.setText(String.valueOf(facebook_id));
		
		final EditText create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		create_account_fist_name.setText(first_name);
		
		final EditText create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		create_account_last_name.setText(last_name);
		
		final Spinner create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		create_account_gender.setSelection(gender);
		
		final EditText create_account_email = (EditText) findViewById(R.id.create_account_email);
		create_account_email.setText(email);
		
		final Spinner create_account_age = (Spinner) findViewById(R.id.create_account_age);
		create_account_age.setSelection(age);
		
		final Spinner create_account_education = (Spinner) findViewById(R.id.create_account_education);
		create_account_education.setSelection(education);
		
		final Spinner create_account_work = (Spinner) findViewById(R.id.create_account_work);
		create_account_work.setSelection(work);
		
		final EditText create_account_location = (EditText) findViewById(R.id.create_account_location);
		create_account_location.setText(location);
		
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		btnCreateUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.i(Const.TAG, "create_account_facebook: "+create_account_facebook.getText());
				Log.i(Const.TAG, "create_account_fist_name: "+create_account_fist_name.getText());
				Log.i(Const.TAG, "create_account_last_name: "+create_account_last_name.getText());
				Log.i(Const.TAG, "create_account_gender: "+create_account_gender.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_email: "+create_account_email.getText());
				Log.i(Const.TAG, "create_account_age: "+create_account_age.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_education: "+create_account_education.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_work: "+create_account_work.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_location: "+create_account_location.getText());
				
				if(ServerUtilities.createAccount(getApplicationContext(), 
						create_account_facebook.getText().toString(), 
						create_account_fist_name.getText().toString(), 
						create_account_last_name.getText().toString(), 
						create_account_gender.getSelectedItemPosition(), 
						create_account_email.getText().toString(), 
						create_account_age.getSelectedItemPosition(), 
						create_account_education.getSelectedItemPosition(), 
						create_account_work.getSelectedItemPosition(), 
						create_account_location.getText().toString())) {
					
					User user = new User(Integer.parseInt(create_account_facebook.getText().toString()), 
						create_account_fist_name.getText().toString(), 
						create_account_last_name.getText().toString(), 
						create_account_gender.getSelectedItemPosition(), 
						create_account_email.getText().toString(), 
						create_account_age.getSelectedItemPosition(), 
						create_account_education.getSelectedItemPosition(), 
						create_account_work.getSelectedItemPosition(), 
						create_account_location.getText().toString(), 
						User.USER_REGISTERED);
					
					/* Put user in session */
					HappyMeteoApplication.getSessionService().put("user", user);
					
					/* get activity from session */
					Activity activity = (Activity) HappyMeteoApplication.getSessionService().get("activity");
					
					/* Switch to menu activity if registered */
					Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
					activity.startActivity(intent);
				} else {
					// TODO: Error
				}
			}
		});
		
		Button btnBack = (Button) findViewById(R.id.btnBackCreateAccount);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

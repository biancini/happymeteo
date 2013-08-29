package com.happymeteo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

public class CreateAccountActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		Log.i(Const.TAG, "Create CreateAccountActivity");
		
		final EditText create_account_facebook = (EditText) findViewById(R.id.create_account_facebook);
		final EditText create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		final EditText create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		final Spinner create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		final EditText create_account_email = (EditText) findViewById(R.id.create_account_email);
		final EditText create_account_password = (EditText) findViewById(R.id.create_account_password);
		final Spinner create_account_age = (Spinner) findViewById(R.id.create_account_age);
		final Spinner create_account_education = (Spinner) findViewById(R.id.create_account_education);
		final Spinner create_account_work = (Spinner) findViewById(R.id.create_account_work);
		final EditText create_account_location = (EditText) findViewById(R.id.create_account_location);
		
		if(HappyMeteoApplication.i().isFacebookSession()) {
			create_account_password.setVisibility(View.INVISIBLE);
			
			/* Get parameters */
			String facebook_id = getIntent().getStringExtra("facebook_id");
			String first_name = getIntent().getStringExtra("first_name") == null ? "" : getIntent().getStringExtra("first_name");
			String last_name = getIntent().getStringExtra("last_name") == null ? "" : getIntent().getStringExtra("last_name");
			int gender = getIntent().getIntExtra("gender", 0);
			String email = getIntent().getStringExtra("email") == null ? "" : getIntent().getStringExtra("email");
			int age = getIntent().getIntExtra("age", 0);
			int education = getIntent().getIntExtra("education", 0);
			int work = getIntent().getIntExtra("work", 0);
			String location = getIntent().getStringExtra("location") == null ? "" : getIntent().getStringExtra("location");
			
			create_account_facebook.setText(String.valueOf(facebook_id));
			create_account_fist_name.setText(first_name);
			create_account_last_name.setText(last_name);
			create_account_gender.setSelection(gender);
			create_account_email.setText(email);
			create_account_age.setSelection(age);
			create_account_education.setSelection(education);
			create_account_work.setSelection(work);
			create_account_location.setText(location);
		}
		
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
				
				String password;
				try {
					password = SHA1.hexdigest(create_account_password.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					password = "";
				}
				
				switch(ServerUtilities.createAccount(
						create_account_facebook.getText().toString(), 
						create_account_fist_name.getText().toString(), 
						create_account_last_name.getText().toString(), 
						create_account_gender.getSelectedItemPosition(), 
						create_account_email.getText().toString(), 
						create_account_age.getSelectedItemPosition(), 
						create_account_education.getSelectedItemPosition(), 
						create_account_work.getSelectedItemPosition(), 
						create_account_location.getText().toString(),
						password)) {
					
					case CONFIRMED_OR_FACEBOOK:
						User user = new User(create_account_facebook.getText().toString(), 
							create_account_fist_name.getText().toString(), 
							create_account_last_name.getText().toString(), 
							create_account_gender.getSelectedItemPosition(), 
							create_account_email.getText().toString(), 
							create_account_age.getSelectedItemPosition(), 
							create_account_education.getSelectedItemPosition(), 
							create_account_work.getSelectedItemPosition(), 
							create_account_location.getText().toString(), 
							User.USER_REGISTERED);
						
						/* Set current user */
						HappyMeteoApplication.i().setCurrentUser(user);
						
						/* Switch to menu activity if registered */
						Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
						startActivity(intent);
						break;
					
					case NOT_CONFIRMED:
						AlertDialogManager alert = new AlertDialogManager();
						alert.showAlertDialog(view.getContext(), "Creazione completata!",
								"Presto verrà inviata una email di conferma all'email indicata", true, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										finish();
									}
								});
						break;
						
					case ERROR:
						// TODO
						break;
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

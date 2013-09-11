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

import com.happymeteo.models.CreateAccountDTO;
import com.happymeteo.models.User;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;

public class CreateAccountActivity extends Activity {
	private String user_id;
	private String facebook_id;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		Log.i(Const.TAG, "Create CreateAccountActivity");
		
		this.user_id = "";
		
		final EditText create_account_fist_name = (EditText) findViewById(R.id.create_account_fist_name);
		final EditText create_account_last_name = (EditText) findViewById(R.id.create_account_last_name);
		final Spinner create_account_gender = (Spinner) findViewById(R.id.create_account_gender);
		final EditText create_account_email = (EditText) findViewById(R.id.create_account_email);
		final EditText create_account_password = (EditText) findViewById(R.id.create_account_password);
		final Spinner create_account_age = (Spinner) findViewById(R.id.create_account_age);
		final Spinner create_account_education = (Spinner) findViewById(R.id.create_account_education);
		final Spinner create_account_work = (Spinner) findViewById(R.id.create_account_work);
		final EditText create_account_location = (EditText) findViewById(R.id.create_account_location);
		final EditText create_account_cap = (EditText) findViewById(R.id.create_account_cap);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		
		if(HappyMeteoApplication.i().isFacebookSession()) {
			create_account_password.setVisibility(View.GONE);
		}
		
		User user = HappyMeteoApplication.i().getCurrentUser();
		
		if(user != null) {
			this.user_id = user.getUser_id();
			Log.i(Const.TAG, "Create CreateAccountActivity: "+user.getUser_id());
			this.facebook_id = user.getFacebook_id();
			create_account_fist_name.setText(user.getFirst_name());
			create_account_last_name.setText(user.getLast_name());
			create_account_gender.setSelection(user.getGender());
			create_account_email.setText(user.getEmail());
			create_account_age.setSelection(user.getAge());
			create_account_education.setSelection(user.getEducation());
			create_account_work.setSelection(user.getWork());
			create_account_location.setText(user.getLocation());
			create_account_cap.setText(user.getCap());
			
			if(this.user_id != "") {
				btnCreateUser.setText(R.string.modify_account);
			}
		}
		
		
		btnCreateUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Log.i(Const.TAG, "this.facebook_id: "+facebook_id);
				Log.i(Const.TAG, "create_account_fist_name: "+create_account_fist_name.getText());
				Log.i(Const.TAG, "create_account_last_name: "+create_account_last_name.getText());
				Log.i(Const.TAG, "create_account_gender: "+create_account_gender.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_email: "+create_account_email.getText());
				Log.i(Const.TAG, "create_account_age: "+create_account_age.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_education: "+create_account_education.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_work: "+create_account_work.getSelectedItemPosition());
				Log.i(Const.TAG, "create_account_location: "+create_account_location.getText());
				Log.i(Const.TAG, "create_account_cap: "+create_account_cap.getText());
				
				String password;
				try {
					password = SHA1.hexdigest(create_account_password.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
					password = "";
				}
				
				CreateAccountDTO cDto = ServerUtilities.createAccount(
						view.getContext(),
						user_id,
						facebook_id, 
						create_account_fist_name.getText().toString(), 
						create_account_last_name.getText().toString(), 
						create_account_gender.getSelectedItemPosition(), 
						create_account_email.getText().toString(), 
						create_account_age.getSelectedItemPosition(), 
						create_account_education.getSelectedItemPosition(), 
						create_account_work.getSelectedItemPosition(), 
						create_account_location.getText().toString(),
						create_account_cap.getText().toString(),
						password);
				
				switch(cDto.status) {
					
					case CONFIRMED_OR_FACEBOOK:
						User user = new User(
							cDto.user_id,
							facebook_id, 
							create_account_fist_name.getText().toString(), 
							create_account_last_name.getText().toString(), 
							create_account_gender.getSelectedItemPosition(), 
							create_account_email.getText().toString(), 
							create_account_age.getSelectedItemPosition(), 
							create_account_education.getSelectedItemPosition(), 
							create_account_work.getSelectedItemPosition(), 
							create_account_location.getText().toString(), 
							create_account_cap.getText().toString(), 
							User.USER_REGISTERED);
						
						/* Set current user */
						HappyMeteoApplication.i().setCurrentUser(user);
						
						/* Switch to menu activity if registered */
						Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
						startActivity(intent);
						break;
					
					case NOT_CONFIRMED:
						AlertDialogManager alert = new AlertDialogManager();
						alert.showAlertDialog(view.getContext(), "Account non verificato",
								"Presto verra  inviata una email di conferma all'email indicata", true, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										finish();
									}
								});
						break;
						
					case ERROR:
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

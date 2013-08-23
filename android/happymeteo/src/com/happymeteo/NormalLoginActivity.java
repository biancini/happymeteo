package com.happymeteo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;

public class NormalLoginActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_normal_login);

		final EditText normal_login_email = (EditText) findViewById(R.id.normal_login_email);
		final EditText normal_login_password = (EditText) findViewById(R.id.normal_login_password);

		Button btnGoNormalLogin = (Button) findViewById(R.id.btnGoNormalLogin);
		btnGoNormalLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/* GO */
				String email = normal_login_email.getText().toString();
				String password = "";
				try {
					password = SHA1.hexdigest(normal_login_password.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Log.i(Const.TAG, "email: " + email);
				Log.i(Const.TAG, "password: " + password);
				
				User user = ServerUtilities.normalLogin(email, password);
				
				if(user != null) {
					/* Set current user */
					HappyMeteoApplication.setCurrentUser(user);
					HappyMeteoApplication.setFacebookSession(false);
					
					/* Switch to menu activity if registered */
					Activity activity = HappyMeteoApplication.getMainActivity();
					Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
					activity.startActivity(intent);
				} else {
					//TODO
				}
			}
		});

		Button btnBack = (Button) findViewById(R.id.btnBackNormalLogin);
		btnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

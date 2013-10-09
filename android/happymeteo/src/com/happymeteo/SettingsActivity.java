package com.happymeteo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppyMeteoLoggedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_settings);
		super.onCreate(savedInstanceState);
		Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
		Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

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
	}
}

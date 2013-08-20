package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		/* Get parameters */
		int facebook_id = getIntent().getIntExtra("facebook_id", 0);
		//String email = getIntent().getStringExtra("email");
		String name = getIntent().getStringExtra("name") == null ? "" : getIntent().getStringExtra("name");
		String surname = getIntent().getStringExtra("surname") == null ? "" : getIntent().getStringExtra("surname");
		
		EditText create_account_email = (EditText) findViewById(R.id.create_account_email);
		
		EditText create_account_name = (EditText) findViewById(R.id.create_account_name);
		create_account_name.setText(name);
		
		EditText create_account_surname = (EditText) findViewById(R.id.create_account_surname);
		create_account_surname.setText(surname);
		
		EditText create_account_facebook = (EditText) findViewById(R.id.create_account_facebook);
		create_account_facebook.setText(String.valueOf(facebook_id));
		
		Button btnBack = (Button) findViewById(R.id.btnBackCreateAccount);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InformationPageActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information_page);
		
		Button btnBack = (Button) findViewById(R.id.btnBackInformationPage);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

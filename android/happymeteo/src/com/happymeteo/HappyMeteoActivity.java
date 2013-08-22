package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HappyMeteoActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_happy_meteo);
		
		Button btnBack = (Button) findViewById(R.id.btnBackHappyMeteo);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HappyMapActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_happy_map);
		
		Button btnBack = (Button) findViewById(R.id.btnBackHappyMap);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

package com.happymeteo;

import org.json.JSONObject;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HappyContextActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_happy_context);
		
		Button btnBack = (Button) findViewById(R.id.btnBackHappyContext);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		JSONObject json = ServerUtilities.happyContext(getApplicationContext());
		Log.i(Const.TAG, "json: " + json);
	}

}

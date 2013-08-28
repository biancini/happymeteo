package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.happymeteo.utils.Const;

public class LoadingActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		Log.i(Const.TAG, "Create LoadingActivity");
		
		int action = getIntent().getIntExtra("action", 0);
		
		switch(action) {
			case 1:
				HappyMeteoApplication.getFacebookSessionService().onClickLogin(this);
				break;
		}
	}
}

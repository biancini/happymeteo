package com.happymeteo;

import android.app.Application;

public class HappyMeteoApplication extends Application {
	
	private static HappyMeteoSkeleton instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = new HappyMeteoSkeleton(getApplicationContext());
	}

	public static HappyMeteoSkeleton i() {
		return instance;
	}
}

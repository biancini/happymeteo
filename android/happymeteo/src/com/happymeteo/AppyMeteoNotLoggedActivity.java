package com.happymeteo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.happymeteo.utils.Const;

public class AppyMeteoNotLoggedActivity extends SherlockActivity {
	
	class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
		public DefaultExceptionHandler() {
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.e(Const.TAG, ex.getMessage(), ex);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(
                new DefaultExceptionHandler());
		
		super.onCreate(savedInstanceState);
	}

	public void invokeActivity(Class<? extends Activity> clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}
}

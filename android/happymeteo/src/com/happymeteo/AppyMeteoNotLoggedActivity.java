package com.happymeteo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.ConnectionDetector;
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
		
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

		/* Check internet */
		if (!cd.isConnectingToInternet()) {
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(this, "Errore di connessione Internet",
					"Connettere Internet per utilizzare Appy Meteo", false,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			return;
		}
		
		super.onCreate(savedInstanceState);
	}
	
	public void invokeActivity(Class<? extends Activity> clazz) {
		invokeActivity(clazz, null);
	}

	public void invokeActivity(Class<? extends Activity> clazz, Bundle extras) {
		Log.i(Const.TAG, "invokeActivity: "+this.getClass()+" "+clazz);
		
		if(!this.getClass().equals(clazz)) {
			Intent intent = new Intent(this, clazz);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if(extras != null) {
				intent.putExtras(extras);
			}
			startActivity(intent);
		}
	}
	
	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService
		HappyMeteoApplication.getPushNotificationsService().terminate(getApplicationContext()); */

		Log.i(Const.TAG, this.getClass()+" onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(Const.TAG, this.getClass()+" onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(Const.TAG, this.getClass()+" onNewIntent");
		super.onNewIntent(intent);
	}
	
	@Override
	public void onStart() {
		Log.i(Const.TAG, this.getClass()+" onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(Const.TAG, this.getClass()+" onStop");
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		Log.i(Const.TAG, this.getClass()+" onResume");
		super.onResume();
	}
}

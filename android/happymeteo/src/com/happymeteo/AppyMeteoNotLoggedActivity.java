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
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService
		HappyMeteoApplication.i().getPushNotificationsService().terminate(getApplicationContext()); */

		Log.i(Const.TAG, this.getClass()+" onDestroy");
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		Log.i(Const.TAG, this.getClass()+" onSaveInstanceState");
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		Log.i(Const.TAG, this.getClass()+" onNewIntent");
	}
}

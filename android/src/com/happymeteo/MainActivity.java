package com.happymeteo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class MainActivity extends Activity {
	
	/* The BroadcastReceiver needs to be in the same class where registerReceiver is called */
	public BroadcastReceiver broadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Initialize PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().initialize(
				getApplicationContext());
		
		if(!HappyMeteoApplication.getPushNotificationsService().getRegistrationId().equals("")) {
			/* Register device on happymeteo backend */
			ServerUtilities.register(getApplicationContext(), HappyMeteoApplication.getPushNotificationsService().getRegistrationId());
		}
	}

	@Override
	protected void onDestroy() {
		Log.i(Const.TAG, "unregisterReceiver "+broadcastReceiver);
		
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().terminate(
				getApplicationContext());

		super.onDestroy();
	}

}

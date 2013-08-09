package com.happymeteo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.pushnotifications.CommonUtilities;
import com.happymeteo.pushnotifications.ConnectionDetector;
import com.happymeteo.pushnotifications.WakeLocker;

public class PushNotificationsService extends BroadcastReceiver {
	
	public void initialize(Context context) {
		/* Check internet connection */
		ConnectionDetector cd = new ConnectionDetector(context);
		if (!cd.isConnectingToInternet()) {
			Toast.makeText(context, "Please connect to working Internet connection", Toast.LENGTH_SHORT).show();
			return;
		}
		
		/* Make sure the device has the proper dependencies. */
		GCMRegistrar.checkDevice(context);
		
		/* Make sure the manifest was properly set */
		GCMRegistrar.checkManifest(context);
		
		/* Get Registration Id */
		final String registrationId = GCMRegistrar.getRegistrationId(context);
		
		Toast.makeText(context, "registrationId: "+registrationId, Toast.LENGTH_SHORT).show();
		Log.i("HappyMeteo", "registrationId: "+registrationId);
		
		if (registrationId.equals("")) {
			Toast.makeText(context, "Register now: "+GCMRegistrar.isRegisteredOnServer(context), Toast.LENGTH_SHORT).show();
			Log.i("HappyMeteo", "Register now: "+GCMRegistrar.isRegisteredOnServer(context));
			
			/* Registration is not present, register now with GCM */			
			GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		for(String key : intent.getExtras().keySet()) {
			Toast.makeText(context, "(k, v): (" + key + "," + 
				intent.getExtras().getString(key) + ")", Toast.LENGTH_LONG).show();
		}
		
		String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
		// Waking up mobile if it is sleeping
		WakeLocker.acquire(context);
		
		/**
		 * Take appropriate action on this message
		 * depending upon your app requirement
		 * For now i am just displaying it on the screen
		 * */
		
		// Showing received message
		Toast.makeText(context, "New Message: " + newMessage, Toast.LENGTH_LONG).show();
		
		// Releasing wake lock
		WakeLocker.release();
	}

	public void terminate(Context context) {
		Log.i("HappyMeteo", "unregister");
		
		GCMRegistrar.unregister(context);
		GCMRegistrar.onDestroy(context);
	}
}

package com.happymeteo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.utils.ConnectionDetector;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.WakeLocker;

public class PushNotificationsService extends BroadcastReceiver {
	
	private String registrationId;
	
	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

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
		registrationId = GCMRegistrar.getRegistrationId(context);
		
		Toast.makeText(context, "registrationId: "+registrationId, Toast.LENGTH_SHORT).show();
		Log.i("HappyMeteo", "registrationId: "+registrationId);
		
		if (registrationId.equals("")) {
			Toast.makeText(context, "Register now: "+GCMRegistrar.isRegisteredOnServer(context), Toast.LENGTH_SHORT).show();
			Log.i("HappyMeteo", "Register now: "+GCMRegistrar.isRegisteredOnServer(context));
			
			/* Registration is not present, register now with GCM */			
			GCMRegistrar.register(context, Const.SENDER_ID);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		for(String key : intent.getExtras().keySet()) {
			Toast.makeText(context, "(k, v): (" + key + "," + 
				intent.getExtras().getString(key) + ")", Toast.LENGTH_LONG).show();
		}
		
		String newMessage = intent.getExtras().getString(Const.EXTRA_MESSAGE);
		// Waking up mobile if it is sleeping
		WakeLocker.acquire(context);
		
		/**
		 * Take appropriate action on this message
		 * depending upon your app requirement
		 * For now i am just displaying it on the screen
		 * */
		
		// Showing received message
		Toast.makeText(context, "New Message: " + newMessage, Toast.LENGTH_LONG).show();
		
		Log.i(Const.TAG, "New Message: " + newMessage);
		
		// Releasing wake lock
		WakeLocker.release();
	}

	public void terminate(Context context) {
		Log.i("HappyMeteo", "unregister");
		GCMRegistrar.unregister(context);
		GCMRegistrar.onDestroy(context);
	}
}

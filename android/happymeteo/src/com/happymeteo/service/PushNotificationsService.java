package com.happymeteo.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.utils.Const;

public class PushNotificationsService {
	
	private String registrationId;
	
	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public void initialize(Context context) {
		/* Make sure the device has the proper dependencies. */
		GCMRegistrar.checkDevice(context);
		
		/* Make sure the manifest was properly set */
		GCMRegistrar.checkManifest(context);
		
		/* Get Registration Id */
		registrationId = GCMRegistrar.getRegistrationId(context);
		
		Log.i("HappyMeteo", "registrationId: "+registrationId);
		if (registrationId.equals("")) {
			Log.i("HappyMeteo", "Register now: "+GCMRegistrar.isRegisteredOnServer(context));
			
			/* Registration is not present, register now with GCM */			
			GCMRegistrar.register(context, Const.GOOGLE_ID);
		}
	}
	
	/*public void onReceive(Context context, Intent intent) {
		String newMessage = intent.getExtras().getString(Const.EXTRA_MESSAGE);
		// Waking up mobile if it is sleeping
		WakeLocker.acquire(context);
		
		// Showing received message
		Log.i(Const.TAG, "New Message: " + newMessage);
		
		// Releasing wake lock
		WakeLocker.release();
	}*/

	public void terminate(Context context) {
		Log.i("HappyMeteo", "unregister");
		GCMRegistrar.unregister(context);
		GCMRegistrar.onDestroy(context);
	}
}

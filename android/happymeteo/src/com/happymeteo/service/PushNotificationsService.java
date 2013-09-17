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

	public boolean initialize(Context context) {
		/* Make sure the device has the proper dependencies. */
		GCMRegistrar.checkDevice(context);
		
		/* Make sure the manifest was properly set */
		GCMRegistrar.checkManifest(context);
		
		/* Get Registration Id */
		registrationId = GCMRegistrar.getRegistrationId(context);
		
		Log.i(Const.TAG, "registrationId: "+registrationId);
		if (registrationId.equals("")) {
			Log.i(Const.TAG, "Register now: "+GCMRegistrar.isRegisteredOnServer(context));
			
			/* Registration is not present, register now with GCM */			
			GCMRegistrar.register(context, Const.GOOGLE_ID);
			return false;
		}
		
		return true;
	}

	public void terminate(Context context) {
		if(GCMRegistrar.isRegistered(context)) {
			GCMRegistrar.unregister(context);
		}
	}
}

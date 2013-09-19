package com.happymeteo.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class PushNotificationsService {
	public static void register(Context context, String userId) {
		/* Make sure the device has the proper dependencies. */
		GCMRegistrar.checkDevice(context);
		
		/* Make sure the manifest was properly set */
		GCMRegistrar.checkManifest(context);
		
		/* Get Registration Id */
		String registrationId = GCMRegistrar.getRegistrationId(context);
		
		Log.i(Const.TAG, "registrationId: "+registrationId);
		if (registrationId.equals("")) {
			Log.i(Const.TAG, "Register now: "+GCMRegistrar.isRegisteredOnServer(context));
			
			/* Registration is not present, register now with GCM */			
			GCMRegistrar.register(context, Const.GOOGLE_ID);
		} else {
			ServerUtilities.registerDevice(context, registrationId, userId);
		}
	}

	public static void terminate(Context context) {
		if(GCMRegistrar.isRegistered(context)) {
			GCMRegistrar.unregister(context);
		}
	}
}

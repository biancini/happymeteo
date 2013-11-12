package com.happymeteo.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class PushNotificationsService {
	public static void register(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		
		String registrationId = GCMRegistrar.getRegistrationId(context);
		String userId = SessionCache.getUser_id(context);
		
		Log.d(Const.TAG, "registrationId: " + registrationId);
		if (registrationId.isEmpty()) {
			GCMRegistrar.register(context, Const.GOOGLE_ID);
		}
		
		if (!registrationId.isEmpty() && userId != null && !userId.isEmpty()) {
			ServerUtilities.registerDevice(context, registrationId, userId);
		}
	}

	public static void terminate(Context context) {
		if (GCMRegistrar.isRegistered(context)) GCMRegistrar.unregister(context);
	}
}

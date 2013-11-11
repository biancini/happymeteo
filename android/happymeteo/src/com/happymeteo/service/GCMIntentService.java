package com.happymeteo.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class GCMIntentService extends GCMBaseIntentService {
	
	public GCMIntentService() {
		super(Const.GOOGLE_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		/* Register device on happymeteo backend */
		ServerUtilities.registerDevice(
				context,
				registrationId,
				SessionCache.getUser_id(this));
	}

	/**
	 * Method called on device Unregistered
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		/* Unregister device on happymeteo backend */
		ServerUtilities.unregisterDevice(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(Const.TAG, "Received message: " + intent.getExtras());
		String notification_id = intent.getExtras().getString("notification_id");
		PushNotificationsService.getNotification(context, notification_id);
	}

	/**
	 * Method called on receiving a deleted message
	 * 
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(Const.TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);

		// notifies user
		generateNotification(context, message);
	} */

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		Log.w(Const.TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(Const.TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}
}
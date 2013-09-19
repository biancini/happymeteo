package com.happymeteo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
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
		Log.i(Const.TAG, "Device registered: regId = " + registrationId);
		if(HappyMeteoApplication.i().getCurrentUser() != null) {
			/* Register device on happymeteo backend */
			ServerUtilities.registerDevice(
					context,
					registrationId,
					HappyMeteoApplication.i().getCurrentUser().getUser_id());
		}
	}

	/**
	 * Method called on device Unregistered
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(Const.TAG, "Device unregistered");
		
		/* Unregister device on happymeteo backend */
		ServerUtilities.unregisterDevice(getApplicationContext(), registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(Const.TAG, "Received message");

		/* Notifies user */
		generateNotification(context, intent.getExtras());
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
		Log.i(Const.TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(Const.TAG, "Received recoverable error: " + errorId);

		return super.onRecoverableError(context, errorId);
	}
	
	private static String getMessageFromCollapseKey(String collapse_key) {
		return collapse_key;
	}
	
	private static Class<? extends Activity> getActivityFromCollapseKey(String collapse_key) {
		if(collapse_key.equals("questions"))
			return QuestionActivity.class;
		if(collapse_key.equals("request_challenge"))
			return ChallengeRequestActivity.class;
		if(collapse_key.equals("accepted_challenge"))
			return ChallengeQuestionsActivity.class;
		return null;
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, Bundle extras) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		
		String collapse_key = extras.getString("collapse_key");
		String message = getMessageFromCollapseKey(collapse_key);
		Class<? extends Activity> clazz = getActivityFromCollapseKey(collapse_key);
		
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, clazz);
		notificationIntent.putExtras(extras);
		
		Log.i(Const.TAG, "extras: "+extras.toString());
		
		// set intent so it does not start a new activity
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// 		| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
}
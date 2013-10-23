package com.happymeteo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.happymeteo.Activity;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.challenge.ChallengeQuestionsActivity;
import com.happymeteo.challenge.ChallengeRequestActivity;
import com.happymeteo.challenge.ChallengeScoreActivity;
import com.happymeteo.models.SessionCache;
import com.happymeteo.question.QuestionActivity;
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
		Log.w(Const.TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		Log.i(Const.TAG, "Received recoverable error: " + errorId);

		return super.onRecoverableError(context, errorId);
	}
	
	private static String getMessageFromCollapseKey(Context context, String collapse_key) {
		int text = -1;
		if (collapse_key.equals("questions")) text = R.string.notify_domande;
		if (collapse_key.equals("request_challenge")) text = R.string.notify_gioca;
		if (collapse_key.equals("accepted_challenge_turn1_true")) text = R.string.notify_richiesta;
		if (collapse_key.equals("accepted_challenge_turn1_false")) text = R.string.notify_rifiuto;
		if (collapse_key.equals("accepted_challenge_turn2")) text = R.string.notify_turno;
		if (collapse_key.equals("accepted_challenge_turn3")) text = R.string.notify_finegioco;
		
		if (text > -1) return context.getString(text);
		return collapse_key;
	}
	
	private static Class<? extends ImpulseActivity> getActivityFromCollapseKey(String collapse_key) {
		if(collapse_key.equals("questions"))
			return QuestionActivity.class;
		if(collapse_key.equals("request_challenge"))
			return ChallengeRequestActivity.class;
		if(collapse_key.equals("accepted_challenge_turn1_true"))
			return ChallengeQuestionsActivity.class;
		/*if(collapse_key.equals("accepted_challenge_turn1_false"))
			return HappyMeteoActivity.class;*/
		if(collapse_key.equals("accepted_challenge_turn2"))
			return ChallengeQuestionsActivity.class;
		if(collapse_key.equals("accepted_challenge_turn3"))
			return ChallengeScoreActivity.class;
		return null;
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context, Bundle extras) {
		String user_id = extras.getString("user_id");
		
		if(user_id != null && SessionCache.getUser_id(context) != null && user_id.equals(SessionCache.getUser_id(context))) {
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
//			String notificationTag = String.valueOf(UUID.randomUUID());
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			String collapse_key = extras.getString("collapse_key");
			if(collapse_key == null || collapse_key.equals("do_not_collapse")) {
				collapse_key = extras.getString("appy_key");
			}
			
			String message = getMessageFromCollapseKey(context, collapse_key);
			Class<? extends ImpulseActivity> clazz = getActivityFromCollapseKey(collapse_key);
			Intent notificationIntent = null;
			
			if(clazz == null) {
				notificationIntent = new Intent(context, Activity.class);
			} else {
				notificationIntent = new Intent(context, clazz);
			}
			
			Notification notification = new Notification(icon, message, when);
			String title = context.getString(R.string.app_name);
	
			notificationIntent.putExtras(extras);
			
			Log.i(Const.TAG, "extras: "+extras.toString());
			
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, 0);
			notification.setLatestEventInfo(context, title, message, pendingIntent);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
			// Play default notification sound
			notification.defaults |= Notification.DEFAULT_SOUND;
	
			// notification.sound = Uri.parse("android.resource://" +
			// context.getPackageName() + "your_sound_file_name.mp3");
	
			// Vibrate if vibrate is enabled
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notificationManager.notify(message, 0, notification);
		}
	}
}
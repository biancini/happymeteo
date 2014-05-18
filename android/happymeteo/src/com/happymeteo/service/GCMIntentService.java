package com.happymeteo.service;

import org.json.JSONException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.challenge.ChallengeQuestionsActivity;
import com.happymeteo.challenge.ChallengeRequestActivity;
import com.happymeteo.challenge.ChallengeScoreImpulseActivity;
import com.happymeteo.meteo.MeteoActivity;
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
		if(collapse_key.equals("questions")) return QuestionActivity.class;
		if(collapse_key.equals("request_challenge")) return ChallengeRequestActivity.class;
		if(collapse_key.equals("accepted_challenge_turn1_true")) return ChallengeQuestionsActivity.class;
		//if(collapse_key.equals("accepted_challenge_turn1_false")) return HappyMeteoActivity.class;
		if(collapse_key.equals("accepted_challenge_turn2")) return ChallengeQuestionsActivity.class;
		if(collapse_key.equals("accepted_challenge_turn3")) return ChallengeScoreImpulseActivity.class;
		
		return null;
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 * @throws JSONException 
	 */
	public static void generateNotification(Context context, Bundle extras) {
		String user_id = extras.getString("user_id");
		
		if(user_id != null && SessionCache.getUser_id(context) != null && user_id.equals(SessionCache.getUser_id(context))) {
			String collapse_key = extras.getString("collapse_key");
			if (collapse_key == null || collapse_key.equals("do_not_collapse")) {
				collapse_key = extras.getString("appy_key");
			}
			
			String contentTitle = getMessageFromCollapseKey(context, collapse_key);
			
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(context)
				    .setSmallIcon(R.drawable.ic_launcher)
				    .setContentTitle(contentTitle)
				    .setAutoCancel(true);
			
			Class<? extends ImpulseActivity> clazz = getActivityFromCollapseKey(collapse_key);
			Intent resultIntent = (clazz == null) ? new Intent(context, MeteoActivity.class) : new Intent(context, clazz);
			resultIntent.putExtras(extras);
			
			// Because clicking the notification opens a new ("special") activity, there's
			// no need to create an artificial back stack.
			PendingIntent resultPendingIntent =
			    PendingIntent.getActivity(
			    context,
			    0,
			    resultIntent,
			    PendingIntent.FLAG_UPDATE_CURRENT
			);
			
			mBuilder.setContentIntent(resultPendingIntent);
			
			// Gets an instance of the NotificationManager service
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			// Builds the notification and issues it.
			notificationManager.notify(contentTitle, 0, mBuilder.build());
		}
	}
}
package com.happymeteo.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.ImpulseActivity;
import com.happymeteo.R;
import com.happymeteo.challenge.ChallengeQuestionsActivity;
import com.happymeteo.challenge.ChallengeRequestActivity;
import com.happymeteo.challenge.ChallengeScoreActivity;
import com.happymeteo.meteo.MeteoActivity;
import com.happymeteo.models.SessionCache;
import com.happymeteo.question.QuestionActivity;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class PushNotificationsService {
	public static void register(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		
		String registrationId = GCMRegistrar.getRegistrationId(context);
		String userId = SessionCache.getUser_id(context);
		
		Log.d(Const.TAG, "registrationId: " + registrationId);
		if (registrationId.equals("")) {
			Log.d(Const.TAG, "Register now: " + GCMRegistrar.isRegisteredOnServer(context));
			GCMRegistrar.register(context, Const.GOOGLE_ID);
		}
		
		if (!registrationId.equals("") && userId != null && !userId.equals("")) {
			ServerUtilities.registerDevice(context, registrationId, userId);
		}
	}

	public static void terminate(Context context) {
		if (GCMRegistrar.isRegistered(context)) GCMRegistrar.unregister(context);
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
		if(collapse_key.equals("accepted_challenge_turn3")) return ChallengeScoreActivity.class;
		
		return null;
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 * @throws JSONException 
	 */
	@SuppressWarnings("deprecation")
	public static void generateNotification(Context context, JSONObject jsonObject) throws JSONException {
		String user_id = jsonObject.getString("user_id");
		
		if(user_id != null && SessionCache.getUser_id(context) != null && user_id.equals(SessionCache.getUser_id(context))) {
			int icon = R.drawable.ic_launcher;
			long when = System.currentTimeMillis();
			//String notificationTag = String.valueOf(UUID.randomUUID());
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			String collapse_key = jsonObject.getString("collapse_key");
			if (collapse_key == null || collapse_key.equals("do_not_collapse")) {
				collapse_key = jsonObject.getString("appy_key");
			}
			
			String message = getMessageFromCollapseKey(context, collapse_key);
			Class<? extends ImpulseActivity> clazz = getActivityFromCollapseKey(collapse_key);
			Intent notificationIntent = (clazz == null) ? new Intent(context, MeteoActivity.class) : new Intent(context, clazz);
			Notification notification = new Notification(icon, message, when);
			String title = context.getString(R.string.app_name);
			
			Bundle extras = new Bundle();
			Iterator<String> iterator = jsonObject.keys();
			while(iterator.hasNext()) {
				String key = iterator.next();
				extras.putString(key, jsonObject.getString(key));
			}
			notificationIntent.putExtras(extras);
			
			// set intent so it does not start a new activity
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
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

	public static void onPushPostExecute(Context context, int id, String result, Exception exception) {
		if(exception != null) {
			return;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			Log.d(Const.TAG, "jsonObject: " + jsonObject.toString());
			generateNotification(context, jsonObject);
		} catch(Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}
	
	public static void getNotification(Context context, String notification_id) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("notification_id", notification_id));
		new PushPostRequest(Const.GET_NOTIFICATION_ID, context, nvps).execute(Const.GET_NOTIFICATION_URL);
	}
}

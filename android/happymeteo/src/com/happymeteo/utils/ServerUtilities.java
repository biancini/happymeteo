package com.happymeteo.utils;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class ServerUtilities {
	
	public static void createAccount(onPostExecuteListener onPostExecuteListener, Activity activity,
			String user_id, String facebook_id, String first_name,
			String last_name, int gender, String email, int age, int education,
			int work, String location, String cap, String password) {
		Log.i(Const.TAG, "createAccount");
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("facebook_id", facebook_id);
		params.put("first_name", first_name);
		params.put("last_name", last_name);
		params.put("gender", String.valueOf(gender));
		params.put("email", email);
		params.put("age", String.valueOf(age));
		params.put("education", String.valueOf(education));
		params.put("work", String.valueOf(work));
		params.put("location", location);
		params.put("cap", cap);
		params.put("password", password);
		
		new PostRequest(onPostExecuteListener, activity, params).execute(Const.CREATE_ACCOUNT);
	}

	public static void facebookLogin(onPostExecuteListener onPostExecuteListener, Activity activity, String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("accessToken", accessToken);
		new PostRequest(onPostExecuteListener, activity, parameters).execute(Const.FACEBOOK_LOGIN_URL);
	}

	public static void normalLogin(onPostExecuteListener onPostExecuteListener, Activity activity, String email, String password) {
		Log.i(Const.TAG, "normalLogin (email = " + email + ", password = "
				+ password + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("email", email);
		parameters.put("password", password);
		new PostRequest(onPostExecuteListener, activity, parameters).execute(Const.NORMAL_LOGIN_URL);
	}

	public static void registerDevice(Context context, String registrationId, String userId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId
				+ ", userId = " + userId + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("registrationId", registrationId);
		parameters.put("userId", userId);
		new PostRequest(context, parameters).execute(Const.REGISTER_URL);
	}

	public static void unregisterDevice(Context context, String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = "
				+ registrationId + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("registrationId", registrationId);
		new PostRequest(context, parameters).execute(Const.REGISTER_URL);
	}

	public static void getQuestions(int id, onPostExecuteListener onPostExecuteListener, Activity activity) {
		Log.i(Const.TAG, "getQuestions");
		Map<String, String> parameters = new HashMap<String, String>();
		new PostRequest(id, onPostExecuteListener, activity, parameters).execute(Const.GET_QUESTIONS_URL);
	}

	public static void submitQuestions(int id, onPostExecuteListener onPostExecuteListener, Activity activity,
			Map<String, String> parameters) {
		Log.i(Const.TAG, "submit questions (params = " + parameters + ")");
		new PostRequest(id, onPostExecuteListener, activity, parameters).execute(Const.SUBMIT_QUESTIONS_URL);
	}

	public static void requestChallenge(Activity activity, String userId,
			String facebookId, String registrationId) {
		Log.i(Const.TAG, "request challenge (userId = " + userId
				+ ", facebookId = " + facebookId + ", registrationId = "
				+ registrationId + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("userId", userId);
		parameters.put("facebookId", facebookId);
		parameters.put("registrationId", registrationId);
		new PostRequest(activity, parameters).execute(Const.REQUEST_CHALLENGE_URL);
	}

	public static void acceptChallenge(Activity activity, String challengeId,
			Boolean accepted) {
		Log.i(Const.TAG, "accept challenge (challengeId = " + challengeId
				+ ", accepted = " + accepted.toString() + ")");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("challengeId", challengeId);
		parameters.put("accepted", accepted.toString());
		new PostRequest(activity, parameters).execute(Const.ACCEPT_CHALLENGE_URL);
	}

	public static void getChallengeQuestions(int id, onPostExecuteListener onPostExecuteListener, Activity activity) {
		Log.i(Const.TAG, "getChallengeQuestions");
		Map<String, String> parameters = new HashMap<String, String>();
		new PostRequest(id, onPostExecuteListener, activity, parameters).execute(Const.QUESTIONS_CHALLENGE_URL);
	}

	public static void submitChallenge(int id, onPostExecuteListener onPostExecuteListener, Activity activity,
			Map<String, String> parameters) {
		Log.i(Const.TAG, "submit questions (params = " + parameters + ")");
		new PostRequest(onPostExecuteListener, activity, parameters).execute(Const.SUBMIT_CHALLENGE_URL);
	}

	public static void happyMeteo(onPostExecuteListener onPostExecuteListener, Activity activity) {
		Log.i(Const.TAG, "happyMeteo");
		Map<String, String> parameters = new HashMap<String, String>();
		new PostRequest(onPostExecuteListener, activity, parameters).execute(Const.HAPPY_METEO_URL);
	}

	public static void happyContext(onPostExecuteListener onPostExecuteListener, Activity activity) {
		Log.i(Const.TAG, "happyContext");
		Map<String, String> parameters = new HashMap<String, String>();
		new PostRequest(onPostExecuteListener, activity, parameters).execute(Const.HAPPY_CONTEXT_URL);
	}
}

package com.happymeteo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class ServerUtilities {
	
	public static void createAccount(onPostExecuteListener onPostExecuteListener, Activity activity,
			String user_id, String facebook_id, String first_name,
			String last_name, int gender, String email, int age, int education,
			int work, String cap, String password) {
		Log.i(Const.TAG, "createAccount");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", user_id));
		nvps.add(new BasicNameValuePair("facebook_id", facebook_id));
		nvps.add(new BasicNameValuePair("first_name", first_name));
		nvps.add(new BasicNameValuePair("last_name", last_name));
		nvps.add(new BasicNameValuePair("gender", String.valueOf(gender)));
		nvps.add(new BasicNameValuePair("email", email));
		nvps.add(new BasicNameValuePair("age", String.valueOf(age)));
		nvps.add(new BasicNameValuePair("education", String.valueOf(education)));
		nvps.add(new BasicNameValuePair("work", String.valueOf(work)));
		nvps.add(new BasicNameValuePair("cap", cap));
		nvps.add(new BasicNameValuePair("password", password));
		
		new PostRequest(Const.CREATE_ACCOUNT_ID, activity, nvps, onPostExecuteListener).execute(Const.CREATE_ACCOUNT);
	}

	public static void facebookLogin(onPostExecuteListener onPostExecuteListener, Activity activity, String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("accessToken", accessToken));
		new PostRequest(Const.FACEBOOK_LOGIN_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.FACEBOOK_LOGIN_URL);
	}

	public static void normalLogin(onPostExecuteListener onPostExecuteListener, Activity activity, String email, String password) {
		Log.i(Const.TAG, "normalLogin (email = " + email + ", password = "
				+ password + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", email));
		nvps.add(new BasicNameValuePair("password", password));
		new PostRequest(Const.NORMAL_LOGIN_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.NORMAL_LOGIN_URL);
	}

	public static void registerDevice(Context context, String registrationId, String userId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId + ", userId = " + userId + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.REGISTER_URL_ID, context, nvps).execute(Const.REGISTER_URL);
	}

	public static void unregisterDevice(Context context, String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = " + registrationId + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		new PostRequest(Const.UNREGISTER_URL_ID, context, nvps).execute(Const.REGISTER_URL);
	}

	public static void getQuestions(onPostExecuteListener onPostExecuteListener, Activity activity, String userId) {
		Log.i(Const.TAG, "getQuestions");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.GET_QUESTIONS_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.GET_QUESTIONS_URL);
	}

	public static void submitQuestions(onPostExecuteListener onPostExecuteListener, Activity activity,
			Map<String, String> params) {
		Log.i(Const.TAG, "submit questions (params = " + params + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for(String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		new PostRequest(Const.SUBMIT_QUESTIONS_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.SUBMIT_QUESTIONS_URL);
	}

	public static void requestChallenge(Activity activity, String userId,
			String facebookId, String registrationId) {
		Log.i(Const.TAG, "request challenge (userId = " + userId + ", facebookId = " + facebookId + ", registrationId = " + registrationId + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userId", userId));
		nvps.add(new BasicNameValuePair("facebookId", facebookId));
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		new PostRequest(Const.REQUEST_CHALLENGE_URL_ID, activity, nvps).execute(Const.REQUEST_CHALLENGE_URL);
	}

	public static void acceptChallenge(onPostExecuteListener onPostExecuteListener, Activity activity, String challengeId,
			Boolean accepted, String registrationId, String userId) {
		Log.i(Const.TAG, "accept challenge (challengeId = " + challengeId
				+ ", accepted = " + accepted.toString() + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("challengeId", challengeId));
		nvps.add(new BasicNameValuePair("accepted", accepted.toString()));
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.ACCEPT_CHALLENGE_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.ACCEPT_CHALLENGE_URL);
	}

	public static void getChallengeQuestions(onPostExecuteListener onPostExecuteListener, Activity activity, String challengeId, String turn) {
		Log.i(Const.TAG, "getChallengeQuestions");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("challengeId", challengeId));
		nvps.add(new BasicNameValuePair("turn", turn));
		new PostRequest(Const.QUESTIONS_CHALLENGE_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.QUESTIONS_CHALLENGE_URL);
	}

	public static void submitChallenge(onPostExecuteListener onPostExecuteListener, Activity activity,
			Map<String, String> params) {
		Log.i(Const.TAG, "submit questions (params = " + params + ")");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for(String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		new PostRequest(Const.SUBMIT_CHALLENGE_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.SUBMIT_CHALLENGE_URL);
	}
	
	public static void getChallenges(onPostExecuteListener onPostExecuteListener, Activity activity, String userId) {
		Log.i(Const.TAG, "getChallenges");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));
		new PostRequest(Const.GET_CHALENGES_URL_ID, activity, nvps, onPostExecuteListener).execute(Const.GET_CHALLENGES_URL);
	}
}

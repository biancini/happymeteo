package com.happymeteo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.happymeteo.NotLoggedActivity;

public class ServerUtilities {
	// private static final long DELAY = 2000;

	public static void createAccount(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String user_id, String facebook_id, String first_name,
			String last_name, int gender, String email, int age, int education,
			int work, String cap, String password) {
		
		final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
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

		new PostRequest(Const.CREATE_ACCOUNT_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.CREATE_ACCOUNT);
	}

	public static void facebookLogin(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String accessToken) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("accessToken", accessToken));
		new PostRequest(Const.FACEBOOK_LOGIN_URL_ID, appyMeteoNotLoggedActivity,nvps).execute(Const.FACEBOOK_LOGIN_URL);
	}

	public static void normalLogin(NotLoggedActivity appyMeteoNotLoggedActivity,String email, String password) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", email));
		nvps.add(new BasicNameValuePair("password", password));
		new PostRequest(Const.NORMAL_LOGIN_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.NORMAL_LOGIN_URL);
	}

	public static void registerDevice(Context context, String registrationId,
			String userId) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.REGISTER_URL_ID, context, nvps)
				.execute(Const.REGISTER_URL);
	}

	public static void unregisterDevice(Context context, String registrationId) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		new PostRequest(Const.UNREGISTER_URL_ID, context, nvps).execute(Const.REGISTER_URL);
	}

	public static void getQuestions(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String userId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.GET_QUESTIONS_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.GET_QUESTIONS_URL);
	}

	public static void submitQuestions(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			Map<String, String> params) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		new PostRequest(Const.SUBMIT_QUESTIONS_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.SUBMIT_QUESTIONS_URL);
	}

	public static void requestChallenge(NotLoggedActivity appyMeteoNotLoggedActivity, String userId,
			String facebookId, String registrationId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userId", userId));
		nvps.add(new BasicNameValuePair("facebookId", facebookId));
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		new PostRequest(Const.REQUEST_CHALLENGE_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.REQUEST_CHALLENGE_URL);
	}

	public static void acceptChallenge(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String challengeId, Boolean accepted, String registrationId,
			String userId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("challengeId", challengeId));
		nvps.add(new BasicNameValuePair("accepted", accepted.toString()));
		nvps.add(new BasicNameValuePair("registrationId", registrationId));
		nvps.add(new BasicNameValuePair("userId", userId));
		new PostRequest(Const.ACCEPT_CHALLENGE_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.ACCEPT_CHALLENGE_URL);
	}

	public static void getChallengeQuestions(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String challengeId, String turn) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("challengeId", challengeId));
		nvps.add(new BasicNameValuePair("turn", turn));
		new PostRequest(Const.QUESTIONS_CHALLENGE_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.QUESTIONS_CHALLENGE_URL);
	}

	public static void submitChallenge(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			Map<String, String> params) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		new PostRequest(Const.SUBMIT_CHALLENGE_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.SUBMIT_CHALLENGE_URL);
	}

	public static void getChallenges(NotLoggedActivity appyMeteoNotLoggedActivity, String userId) {
		final List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));

		new PostRequest(Const.GET_CHALENGES_URL_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.GET_CHALLENGES_URL);
	}

	public static void getAppynessByDay(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String userId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));
		new PostRequest(Const.GET_APPINESS_BY_DAY_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.GET_APPINESS_BY_DAY_URL);
	}

	/*
	public static void getAppynessByMonth(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String userId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));
		new PostRequest(Const.GET_APPINESS_BY_MONTH_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.GET_APPINESS_BY_MONTH_URL);
	}
	*/

	public static void lostPassword(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String email) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", email));
		new PostRequest(Const.LOST_PASSWORD_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.LOST_PASSWORD_URL);
	}

	public static void changePassword(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String userId, String newPassowrd, String oldPassword) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));
		nvps.add(new BasicNameValuePair("new_password", newPassowrd));
		nvps.add(new BasicNameValuePair("old_password", oldPassword));
		new PostRequest(Const.CHANGE_PASSWORD_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.CHANGE_PASSWORD_URL);
	}

	public static void updateFacebook(
			NotLoggedActivity appyMeteoNotLoggedActivity,
			String userId, String facebookId) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", userId));
		nvps.add(new BasicNameValuePair("facebook_id", facebookId));
		new PostRequest(Const.UPDATE_FACEBOOK_ID, appyMeteoNotLoggedActivity, nvps).execute(Const.UPDATE_FACEBOOK_URL);
	}
	
	public static void showErrorAndRetry(final String error, final int id, final Activity activity, final OnPostExecuteListener onPostExecuteListener, final List<NameValuePair> nvps, final String url) {
		AlertDialogManager.showErrorAndRetry(
			activity, 
			error, 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new PostRequest(id, activity, onPostExecuteListener, nvps).execute(url);
				}
			});
	}
}
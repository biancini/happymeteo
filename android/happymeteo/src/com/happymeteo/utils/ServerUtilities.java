package com.happymeteo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.happymeteo.models.CreateAccountDTO;
import com.happymeteo.models.User;

public final class ServerUtilities {
	/**
	 * Create account
	 */
	public static CreateAccountDTO createAccount(String facebook_id, String first_name, String last_name, int gender, String email, int age, int education, int work, String location, String password) {
		Log.i(Const.TAG, "createAccount");
		Map<String, String> params = new HashMap<String, String>();
		params.put("facebook_id", facebook_id);
		params.put("first_name", first_name);
		params.put("last_name", last_name);
		params.put("gender", String.valueOf(gender));
		params.put("email", email);
		params.put("age", String.valueOf(age));
		params.put("education", String.valueOf(education));
		params.put("work", String.valueOf(work));
		params.put("location", location);
		params.put("password", password);
		String json = ServerUtilities.postRequest(Const.CREATE_ACCOUNT, params);
		Log.i(Const.TAG, json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(!isError(jsonObject)) {
				if(jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) {
					return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.CONFIRMED_OR_FACEBOOK, jsonObject.getString("user_id"));
				} else {
					return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.NOT_CONFIRMED, jsonObject.getString("user_id"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.ERROR, "");
	}
	
	/**
	 * Verify access token and register the user through facebook
	 */
	public static User facebookLogin(String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("accessToken", accessToken);
		String json = ServerUtilities.postRequest(Const.FACEBOOK_LOGIN_URL, params);
		Log.i(Const.TAG, json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(!isError(jsonObject)) {
				return new User(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Login with email and password
	 */
	public static User normalLogin(String email, String password) {
		Log.i(Const.TAG, "normalLogin (email = " + email + ", password = " + password +")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("password", password);
		String json = ServerUtilities.postRequest(Const.NORMAL_LOGIN_URL, params);
		Log.i(Const.TAG, json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(!isError(jsonObject)) {
				return new User(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Register this device within the server
	 */
	public static void registerDevice(String registrationId, String userId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId + ", userId = " + userId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		params.put("userId", userId);
		ServerUtilities.postRequest(Const.REGISTER_URL, params);
	}

	/**
	 * Unregister this device within the server
	 */
	public static void unregisterDevice(String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		ServerUtilities.postRequest(Const.UNREGISTER_URL, params);
	}
	
	/**
	 * Get questions
	 */
	public static JSONArray getQuestions() {
		Log.i(Const.TAG, "getQuestions");
		Map<String, String> params = new HashMap<String, String>();
		String json = ServerUtilities.postRequest(Const.GET_QUESTIONS_URL, params);
		JSONArray jsonArray;
		Log.i(Const.TAG, json);
		try {
			jsonArray = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = null;
		}
		return jsonArray;
	}
	
	/**
	 * Submit question
	 */
	public static boolean submitQuestions(Map<String, String> params) {
		Log.i(Const.TAG, "submit questions (params = " + params + ")");
		ServerUtilities.postRequest(Const.SUBMIT_QUESTIONS_URL, params);
		return true;
	}
	
	/**
	 * Request challenge
	 */
	public static void requestChallenge(String userId, String facebookId, String registrationId) {
		Log.i(Const.TAG, "request challenge (userId = " + userId + ", facebookId = " + facebookId + ", registrationId = "+registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("facebookId", facebookId);
		params.put("registrationId", registrationId);
		String json = ServerUtilities.postRequest(Const.REQUEST_CHALLENGE, params);
		Log.i(Const.TAG, json);
	}
	
	/**
	 * Accept challenge
	 */
	public static void acceptChallenge(String challengeId, Boolean accepted) {
		Log.i(Const.TAG, "accept challenge (challengeId = " + challengeId + ", accepted = "+accepted.toString() + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("challengeId", challengeId);
		params.put("accepted", accepted.toString());
		String json = ServerUtilities.postRequest(Const.ACCEPT_CHALLENGE, params);
		Log.i(Const.TAG, json);
	}
	
	private static boolean isError(JSONObject jsonObject) {
		try {
			String error = jsonObject.getString("error");
			Log.e(Const.TAG, error + ":" + jsonObject.getString("message"));
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	private static String postRequest(String serverUrl, Map<String, String> parameters) {
		URL url;
		try {
			url = new URL(serverUrl);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Wrong URL: " + serverUrl);
		}

		StringBuilder requestBody = new StringBuilder();
		Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			requestBody.append(entry.toString());
			if (it.hasNext()) {
				requestBody.append('&');
			}
		}

		String output = "";
		byte[] data = requestBody.toString().getBytes();
		HttpURLConnection conn = null;
		try {
			Log.i(Const.TAG, "POST: "+serverUrl+" "+url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(data.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			OutputStream out = conn.getOutputStream();
			out.write(data);
			out.close();
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Request failed with status: " + status);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String line;
			while ((line = br.readLine()) != null) {
				output += line;
			}
		} catch(Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return output;
	}
}

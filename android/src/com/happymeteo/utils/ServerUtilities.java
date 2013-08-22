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

import android.content.Context;
import android.util.Log;

import com.happymeteo.models.User;

public final class ServerUtilities {
	
	/**
	 * Get questions
	 */
	public static JSONArray getQuestions(Context context) {
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
	 * Verify access token and register the user through facebook
	 */
	public static User facebookLogin(Context context, String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("accessToken", accessToken);
		String json = ServerUtilities.postRequest(Const.FACEBOOK_LOGIN_URL, params);
		Log.i(Const.TAG, json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			return new User(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Create account
	 */
	public static boolean createAccount(Context context, String facebook_id, String first_name, String last_name, int gender, String email, int age, int education, int work, String location) {
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
		String json = ServerUtilities.postRequest(Const.CREATE_ACCOUNT, params);
		Log.i(Const.TAG, json);
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject.get("message").equals("OK");
		} catch (JSONException e) {
			return false;
		}
	}

	/**
	 * Register this device within the server.
	 * 
	 */
	public static void registerDevice(Context context, String registrationId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		ServerUtilities.postRequest(Const.REGISTER_URL, params);
	}

	/**
	 * Unregister this device within the server.
	 */
	public static void unregisterDevice(Context context, String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		ServerUtilities.postRequest(Const.UNREGISTER_URL, params);
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
			Log.i(Const.TAG, "Output from Server .... \n");
			while ((line = br.readLine()) != null) {
				Log.i(Const.TAG, "r:" + line);
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

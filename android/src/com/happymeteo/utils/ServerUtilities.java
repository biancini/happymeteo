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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.R;
import com.happymeteo.models.User;

public final class ServerUtilities {

	/**
	 * Verify access token and register the user
	 */
	public static User facebookLogin(Context context, String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("accessToken", accessToken);
		String message = "";
		try {
			String json = ServerUtilities.postRequest(Const.FACEBOOK_LOGIN_URL, params);
			
			JSONObject jsonObject = new JSONObject(json);
			
			message = context
					.getString(R.string.server_registered, jsonObject.get("facebook_id"));
			
			return new User(jsonObject);
		} catch (IOException e) {
			message = context.getString(R.string.server_register_error,
					accessToken, e.getMessage());
		} catch (JSONException e) {
			message = context.getString(R.string.server_register_error,
					accessToken, e.getMessage());
		}
		Log.i(Const.TAG, message);
		return null;
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static void register(Context context, String registrationId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		String message = "";
		try {
			ServerUtilities.postRequest(Const.REGISTER_URL, params);
			message = context.getString(R.string.server_registered, registrationId);
		} catch (IOException e) {
			message = context.getString(R.string.server_register_error, registrationId,
					e.getMessage());
		}
		Log.i(Const.TAG, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(Context context, String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		String message = "";
		try {
			ServerUtilities.postRequest(Const.UNREGISTER_URL, params);
			GCMRegistrar.setRegisteredOnServer(context, false);
			message = context.getString(R.string.server_unregistered, registrationId);
		} catch (IOException e) {
			message = context.getString(R.string.server_unregister_error,
					registrationId, e.getMessage());
		}
		Log.i(Const.TAG, message);
	}

	private static String postRequest(String serverUrl,
			Map<String, String> parameters) throws IOException {
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
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return output;
	}
}

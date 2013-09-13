package com.happymeteo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.happymeteo.models.CreateAccountDTO;
import com.happymeteo.models.User;

public final class ServerUtilities {
	/**
	 * Create account
	 */
	public static CreateAccountDTO createAccount(Context context, String user_id, String facebook_id, String first_name, String last_name, int gender, String email, int age, int education, int work, String location, String cap, String password) {
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
		String json = ServerUtilities.postRequest(context, Const.CREATE_ACCOUNT, params);
		try {
			JSONObject jsonObject = new JSONObject(json);
			if(jsonObject.get("message").equals("CONFIRMED_OR_FACEBOOK")) {
				return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.CONFIRMED_OR_FACEBOOK, jsonObject.getString("user_id"));
			} else {
				return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.NOT_CONFIRMED, jsonObject.getString("user_id"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new CreateAccountDTO(Const.CREATE_ACCOUNT_STATUS.ERROR, "");
	}
	
	/**
	 * Verify access token and register the user through facebook
	 */
	public static User facebookLogin(Context context, String accessToken) {
		Log.i(Const.TAG, "facebookLogin (accessToken = " + accessToken + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("accessToken", accessToken);
		String json = ServerUtilities.postRequest(context, Const.FACEBOOK_LOGIN_URL, params);
		try {
			if(json != null) {
				JSONObject jsonObject = new JSONObject(json);
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
	public static User normalLogin(Context context, String email, String password) {
		Log.i(Const.TAG, "normalLogin (email = " + email + ", password = " + password +")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("email", email);
		params.put("password", password);
		String json = ServerUtilities.postRequest(context, Const.NORMAL_LOGIN_URL, params);
		try {
			if(json != null) {
				JSONObject jsonObject = new JSONObject(json);
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
	public static void registerDevice(Context context, String registrationId, String userId) {
		Log.i(Const.TAG, "registering device (regId = " + registrationId + ", userId = " + userId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		params.put("userId", userId);
		ServerUtilities.postRequest(context, Const.REGISTER_URL, params);
	}

	/**
	 * Unregister this device within the server
	 */
	public static void unregisterDevice(Context context, String registrationId) {
		Log.i(Const.TAG, "unregistering device (registrationId = " + registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("registrationId", registrationId);
		ServerUtilities.postRequest(context, Const.UNREGISTER_URL, params);
	}
	
	/**
	 * Get questions
	 */
	public static JSONArray getQuestions(Context context) {
		Log.i(Const.TAG, "getQuestions");
		Map<String, String> params = new HashMap<String, String>();
		String json = ServerUtilities.postRequest(context, Const.GET_QUESTIONS_URL, params);
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = null;
		}
		return jsonArray;
	}
	
	/**
	 * Submit question
	 */
	public static boolean submitQuestions(Context context, Map<String, String> params) {
		Log.i(Const.TAG, "submit questions (params = " + params + ")");
		ServerUtilities.postRequest(context, Const.SUBMIT_QUESTIONS_URL, params);
		return true;
	}
	
	/**
	 * Request challenge
	 */
	public static void requestChallenge(Context context, String userId, String facebookId, String registrationId) {
		Log.i(Const.TAG, "request challenge (userId = " + userId + ", facebookId = " + facebookId + ", registrationId = "+registrationId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("facebookId", facebookId);
		params.put("registrationId", registrationId);
		ServerUtilities.postRequest(context, Const.REQUEST_CHALLENGE_URL, params);
	}
	
	/**
	 * Accept challenge
	 */
	public static void acceptChallenge(Context context, String challengeId, Boolean accepted) {
		Log.i(Const.TAG, "accept challenge (challengeId = " + challengeId + ", accepted = "+accepted.toString() + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("challengeId", challengeId);
		params.put("accepted", accepted.toString());
		ServerUtilities.postRequest(context, Const.ACCEPT_CHALLENGE_URL, params);
	}
	
	/**
	 * Get challenge questions
	 */
	public static JSONArray getChallengeQuestions(Context context) {
		Log.i(Const.TAG, "getChallengeQuestions");
		Map<String, String> params = new HashMap<String, String>();
		String json = ServerUtilities.postRequest(context, Const.QUESTIONS_CHALLENGE_URL, params);
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
			jsonArray = null;
		}
		return jsonArray;
	}
	
	/**
	 * Submit challenge question
	 */
	public static boolean submitChallenge(Context context, Map<String, String> params) {
		Log.i(Const.TAG, "submit challenge (params = " + params + ")");
		ServerUtilities.postRequest(context, Const.SUBMIT_CHALLENGE_URL, params);
		return true;
	}
	
	/**
	 * happy meteo
	 */
	public static JSONObject happyMeteo(Context context) {
		Log.i(Const.TAG, "happyMeteo");
		Map<String, String> params = new HashMap<String, String>();
		String json = ServerUtilities.postRequest(context, Const.HAPPY_METEO_URL, params);
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * happy context
	 */
	public static JSONObject happyContext(Context context) {
		Log.i(Const.TAG, "happyContext");
		Map<String, String> params = new HashMap<String, String>();
		String json = ServerUtilities.postRequest(context, Const.HAPPY_CONTEXT_URL, params);
		try {
			JSONObject jsonObject = new JSONObject(json);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static JSONObject isError(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String error = jsonObject.getString("error");
			Log.e(Const.TAG, error + ":" + jsonObject.getString("message"));
			return jsonObject;
		} catch (JSONException e) {
			return null;
		}
	}

	public static String postRequest(Context context, String serverUrl, Map<String, String> parameters) {
		try {
			StringBuffer output = new StringBuffer();
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(serverUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for(String key: parameters.keySet()) {
				nvps.add(new BasicNameValuePair(key, parameters.get(key)));
			}
			request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(request);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();

			JSONObject jsonObject = isError(output.toString());
			
			if(jsonObject != null) {
				AlertDialogManager alert = new AlertDialogManager();
				alert.showAlertDialog(context, jsonObject.getString("error"),
						jsonObject.getString("message"), false, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						});
				return null;
			}
			
			return output.toString();
		} catch (Exception e) {
			e.printStackTrace();
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(context, "Errore",
					e.getLocalizedMessage(), false, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					}); 
		}
		
		/*HttpURLConnection conn = null;
		
		try {
			URL url = new URL(serverUrl);
			StringBuilder requestBody = new StringBuilder();
			Iterator<Entry<String, String>> it = parameters.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				requestBody.append(entry.toString());
				if (it.hasNext()) {
					requestBody.append('&');
				}
			}
	
			StringBuffer output = new StringBuffer();
			byte[] data = requestBody.toString().getBytes();
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

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();
			
			JSONObject jsonObject = isError(output.toString());
			
			if(jsonObject != null) {
				AlertDialogManager alert = new AlertDialogManager();
				alert.showAlertDialog(context, jsonObject.getString("error"),
						jsonObject.getString("message"), false, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {}
						});
				return null;
			}
			
			return output.toString();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Wrong URL: " + serverUrl);
		} catch(Exception e) {
			e.printStackTrace();
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(context, "Errore",
					e.getStackTrace().toString(), false, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {}
					}); 
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}*/

		return null;
	}
	
	public static String getRequest(String serverUrl) {
		HttpURLConnection conn = null;
		try {
			StringBuffer output = new StringBuffer();
			URL url = new URL(serverUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Request failed with status: " + status);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				output.append(inputLine);
			}
			in.close();
			return output.toString();
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}
}

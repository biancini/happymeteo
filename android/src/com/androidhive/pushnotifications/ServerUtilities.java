package com.androidhive.pushnotifications;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;


public final class ServerUtilities {

    /**
     * Register this account/device pair within the server.
     *
     */
    static void register(final Context context, String name, String email, final String regId) {
    	Log.i(CommonUtilities.TAG, "registering device (regId = " + regId + ")");
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("name", name);
        params.put("email", email);
        String message = "";
        try {
			ServerUtilities.post(CommonUtilities.REGISTER_URL, params);
			message = context.getString(R.string.server_registered, regId);
		} catch (IOException e) {
			message = context.getString(R.string.server_register_error, regId, e.getMessage());
		}
        Log.i(CommonUtilities.TAG, message);
		CommonUtilities.displayMessage(context, message);
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        Log.i(CommonUtilities.TAG, "unregistering device (regId = " + regId + ")");
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        String message = "";
        try {
        	ServerUtilities.post(CommonUtilities.UNREGISTER_URL, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            message = context.getString(R.string.server_unregistered, regId);
        } catch (IOException e) {
        	message = context.getString(R.string.server_unregister_error, regId, e.getMessage());
        }
        Log.i(CommonUtilities.TAG, message);
		CommonUtilities.displayMessage(context, message);
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    public static void post(String endpoint, Map<String, String> params)
            throws IOException {   	
        
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        
        if(params != null) {
	        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
	        // constructs the POST body using the parameters
	        while (iterator.hasNext()) {
	            Entry<String, String> param = iterator.next();
	            bodyBuilder.append(param.getKey()).append('=')
	                    .append(param.getValue());
	            if (iterator.hasNext()) {
	                bodyBuilder.append('&');
	            }
	        }
        }
        
        String body = bodyBuilder.toString();
        Log.v(CommonUtilities.TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
        	Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}

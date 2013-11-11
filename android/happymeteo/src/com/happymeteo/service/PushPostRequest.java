package com.happymeteo.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;

public class PushPostRequest extends AsyncTask<String, Void, String> {
	private int id = 0;
	private Context context = null;
	private List<NameValuePair> nvps = null;
	private Exception exception = null;
	private String url = null;
	
	public PushPostRequest(int id, Context context, List<NameValuePair> nvps) {
		this.id = id;
		this.context = context;
		this.nvps = nvps;
		this.exception = null;
	}

	private void searchError(String json) {
		if (json == null || json.startsWith("[")) return;
		
		try {
			JSONObject jsonObject = new JSONObject(json);

			if (jsonObject != null && jsonObject.has("error") && jsonObject.getString("error") != null) {
				String error = jsonObject.getString("error");
				exception = new Exception(error);
			}
		} catch (JSONException e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... urls) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    
	    if (networkInfo != null && networkInfo.isConnected()) {
	    	StringBuffer output = new StringBuffer();
	    	url = urls[0];
	    	
			try {
				Log.i(Const.TAG, "PostRequest url: " + url);
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				String query_string = "";
				boolean first = true;

				Collections.sort(nvps, new Comparator<NameValuePair>() {
					@Override
					public int compare(NameValuePair lhs, NameValuePair rhs) {
						return lhs.getName().compareTo(rhs.getName());
					}
				});

				for (NameValuePair pair : nvps) {
					String value = "";
					if (pair.getValue() != null) value = pair.getValue();
					if (!first) query_string += "&";

					query_string += pair.getName() + "=" + value;
					first = false;
				}
				Log.d(Const.TAG, "query_string: " + query_string);
				nvps.add(new BasicNameValuePair("hashing", SHA1.hexdigest(Const.CALL_SECRET_KEY, query_string)));

				request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
				HttpResponse response = client.execute(request);

				InputStream inputStream = response.getEntity().getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine;
				while ((inputLine = bufferedReader.readLine()) != null) {
					output.append(inputLine);
				}
				bufferedReader.close();
				inputStream.close();
				
				return output.toString();
			} catch (Exception e) {
				Log.e(Const.TAG, e.getMessage(), e);
				this.exception = e;
			}
	    } else {
	    	this.exception = new Exception("No connection");
	    }
	    return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		searchError(result);

		Log.d(Const.TAG, id + " PostRequest result: " + result);
		Log.w(Const.TAG, id + " PostRequest exception: " + exception);
		
		if (exception != null) {
			/*if (context instanceof Activity) { // for "Unable to add window -- token null is not for an application"
				ServerUtilities.showErrorAndRetry(exception.getMessage(), id, (Activity) context, onPostExecuteListener, nvps, url);
			}
			// TODO
			*/
		} else {
			PushNotificationsService.onPushPostExecute(context, id, result, exception);
		}
	}
}
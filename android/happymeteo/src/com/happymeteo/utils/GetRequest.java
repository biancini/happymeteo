package com.happymeteo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class GetRequest extends AsyncTask<String, Void, String> {
	private onPostExecuteListener onPostExecuteListener;
	private int id;
	
	public GetRequest() {
		this.onPostExecuteListener = null;
		this.id = 0;
	}
	
	public GetRequest(onPostExecuteListener onPostExecuteListener) {
		this();
		this.onPostExecuteListener = onPostExecuteListener;
	}
	
	public GetRequest(int id, onPostExecuteListener onPostExecuteListener) {
		this(onPostExecuteListener);
		this.id = id;
	}
	
	@Override
	protected String doInBackground(String... urls) {
		StringBuffer output = new StringBuffer();
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);
	
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					output.append(inputLine);
				}
				in.close();
			} catch(Exception e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}

		return output.toString();
	}
	
	@Override
    protected void onPostExecute(String result) {
		Log.i(Const.TAG, result);
		
		if(onPostExecuteListener != null && result != null) {
			onPostExecuteListener.onPostExecute(id, result);
		}
    }
}

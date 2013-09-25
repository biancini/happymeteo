package com.happymeteo.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

public class GetRequest extends AsyncTask<String, Void, String> {
	private onPostExecuteListener onPostExecuteListener;
	private int id;
	private ProgressDialog spinner;
	
	public GetRequest(Context context) {
		this.onPostExecuteListener = null;
		this.id = 0;
		if(context instanceof Activity) {
			spinner = new ProgressDialog(context);
			spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
			spinner.setMessage(context.getString(com.happymeteo.R.string.loading));
		}
	}
	
	public GetRequest(Context context, onPostExecuteListener onPostExecuteListener) {
		this(context);
		this.onPostExecuteListener = onPostExecuteListener;
	}
	
	public GetRequest(Context context, int id, onPostExecuteListener onPostExecuteListener) {
		this(context, onPostExecuteListener);
		this.id = id;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(spinner != null) {
			spinner.show();
		}
	}
	
	@Override
	protected String doInBackground(String... urls) {
		StringBuffer output = new StringBuffer();
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);
	
				InputStream inputStream = response.getEntity().getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine;
				while ((inputLine = bufferedReader.readLine()) != null) {
					output.append(inputLine);
				}
				bufferedReader.close();
				inputStream.close();
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
			onPostExecuteListener.onPostExecute(id, result, null);
		}
		if(spinner != null) {
			spinner.dismiss();
		}
    }
}

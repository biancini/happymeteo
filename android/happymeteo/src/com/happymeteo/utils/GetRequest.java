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
	private onGetExecuteListener onGetExecuteListener;
	private ProgressDialog spinner;
	
	public GetRequest(Context context, onGetExecuteListener onGetExecuteListener) {
		this.onGetExecuteListener = onGetExecuteListener;
		if(context instanceof Activity) {
			spinner = new ProgressDialog(context);
			spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
			spinner.setMessage(context.getString(com.happymeteo.R.string.loading));
		}
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
				Log.i(Const.TAG, "GetRequest url: "+url);
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
		
		if(onGetExecuteListener != null && result != null) {
			onGetExecuteListener.onGetExecute(result, null);
		}
		if(spinner != null) {
			spinner.dismiss();
		}
    }
}

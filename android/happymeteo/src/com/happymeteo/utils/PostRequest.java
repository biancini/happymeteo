package com.happymeteo.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

public class PostRequest extends AsyncTask<String, Void, String> {
	private int id;
	private Context context;
	private Map<String, String> parameters;
	private onPostExecuteListener onPostExecuteListener;
	private ProgressDialog spinner;
	
	public PostRequest(int id, onPostExecuteListener onPostExecuteListener, Context context, Map<String, String> parameters) {
		this(onPostExecuteListener, context, parameters);
		this.id = id;
	}

	public PostRequest(onPostExecuteListener onPostExecuteListener, Context context, Map<String, String> parameters) {
		this(context, parameters);
		this.onPostExecuteListener = onPostExecuteListener;
	}
	
	public PostRequest(Context context, Map<String, String> parameters) {
		this.id = 0;
		this.onPostExecuteListener = null;
		this.context = context;
		this.parameters = parameters;
		spinner = new ProgressDialog(context);
		spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		spinner.setMessage(context.getString(com.happymeteo.R.string.loading));
	}
	
	private boolean showError(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String error = jsonObject.getString("error");
			Log.e(Const.TAG, error + ":" + jsonObject.getString("message"));
			
			if (jsonObject != null && jsonObject.getString("error") != null && jsonObject.getString("message") != null) {
				AlertDialogManager alert = new AlertDialogManager();
				alert.showAlertDialog(context, jsonObject.getString("error"),
							jsonObject.getString("message"), false,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
				return true;
				
			}
		} catch (JSONException e) {}
		return false;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		spinner.show();
	}

	@Override
	protected String doInBackground(String... urls) {
		StringBuffer output = new StringBuffer();
		for (String url : urls) {
			try {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (String key : parameters.keySet()) {
					nvps.add(new BasicNameValuePair(key, parameters.get(key)));
				}
				request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
				HttpResponse response = client.execute(request);
	
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					output.append(inputLine);
				}
				in.close();
			} catch(Exception e) {
				e.printStackTrace();
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}

		return output.toString();
	}
	
	@Override
    protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(!showError(result)) {
			Log.i(Const.TAG, result);
			
			if(onPostExecuteListener != null && result != null) {
				onPostExecuteListener.onPostExecute(id, result);
			}
		}
		spinner.dismiss();
    }
}

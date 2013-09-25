package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class HappyContextActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_context);
		super.onCreate(savedInstanceState);
		
		//ServerUtilities.happyContext(this, this);
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			Log.i(Const.TAG, "json: " + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

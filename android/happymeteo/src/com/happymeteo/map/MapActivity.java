package com.happymeteo.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;
import com.happymeteo.utils.Const;

public class MapActivity extends LoggedActivity {

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_map);
		super.onCreate(savedInstanceState);
		
		WebView webmapview = (WebView) findViewById(R.id.webmapview);
		WebSettings webSettings = webmapview.getSettings();
		webSettings.setAppCacheEnabled(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webmapview.loadUrl(Const.BASE_URL+"/weather_map.html");
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
	
	@Override
	public void OnFacebookExecute(Session session, SessionState state) {
		// Do nothing
	}
}

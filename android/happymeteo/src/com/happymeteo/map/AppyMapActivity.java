package com.happymeteo.map;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.happymeteo.AppyMeteoLoggedActivity;
import com.happymeteo.R;
import com.happymeteo.utils.Const;

public class AppyMapActivity extends AppyMeteoLoggedActivity {

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_map);
		super.onCreate(savedInstanceState);

		WebView webmapview = (WebView) findViewById(R.id.webmapview);
		WebSettings webSettings = webmapview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webmapview.loadUrl(Const.BASE_URL+"/weather_map.html");
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}

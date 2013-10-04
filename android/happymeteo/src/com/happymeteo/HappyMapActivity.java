package com.happymeteo;

import com.happymeteo.utils.Const;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HappyMapActivity extends AppyMeteoLoggedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_map);
		super.onCreate(savedInstanceState);

		WebView webmapview = (WebView) findViewById(R.id.webmapview);
		WebSettings webSettings = webmapview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webmapview.loadUrl(Const.BASE_URL+"/weather_map.html");
	}

}

package com.happymeteo;

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

		// Simplest usage: note that an exception will NOT be thrown
		// if there is an error loading this page (see below).
		// webmapview.loadUrl("https://www.google.com/fusiontables/embedviz?q=select+col2%3E%3E0+from+1IBG8pv8_kIM3gl9qAjPsXicEmcvU_RpEO6gKyZc&viz=MAP&h=false&lat=45.48765662836111&lng=-350.76732687343747&t=1&z=9&l=col2%3E%3E0&y=2&tmplt=3");
		// OR, you can also load from an HTML string:
		// String summary =
		// "<html><body>You scored <b>192</b> points.</body></html>";
		// webmapview.loadData(summary, "text/html", null);
		// ... although note that there are restrictions on what this HTML can
		// do.
		// See the JavaDocs for loadData() and loadDataWithBaseURL() for more
		// info.

		webmapview.loadUrl("file:///android_res/raw/weather_map.html");
	}

}

package com.happymeteo.information;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity1 extends LoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page1);
		super.onCreate(savedInstanceState);
		
		final TextView information_page1 = (TextView) findViewById(R.id.information_page1);
		information_page1.setText(Html.fromHtml(getString(R.string.information1)));
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
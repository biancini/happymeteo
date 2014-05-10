package com.happymeteo.information;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity4 extends LoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page4);
		super.onCreate(savedInstanceState);
		
		final TextView information_page4 = (TextView) findViewById(R.id.information_page4);
		information_page4.setText(Html.fromHtml(getString(R.string.information4)));
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
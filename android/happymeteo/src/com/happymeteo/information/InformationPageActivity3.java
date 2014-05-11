package com.happymeteo.information;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.happymeteo.LoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity3 extends LoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page3);
		super.onCreate(savedInstanceState);
		
		final TextView information_page3 = (TextView) findViewById(R.id.information_page3);
		information_page3.setText(Html.fromHtml(getString(R.string.information3)));
	}

}
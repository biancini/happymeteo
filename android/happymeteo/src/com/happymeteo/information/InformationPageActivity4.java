package com.happymeteo.information;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.happymeteo.LoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity4 extends LoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page4);
		super.onCreate(savedInstanceState);
		
		final TextView information_page4 = (TextView) findViewById(R.id.information_page4);
		information_page4.setText(Html.fromHtml(getString(R.string.information4)));
		
		final TextView legendIconaTitle = (TextView) findViewById(R.id.legendIconaTitle);
		legendIconaTitle.setPaintFlags(legendIconaTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		final TextView legendColoreTitle = (TextView) findViewById(R.id.legendColoreTitle);
		legendColoreTitle.setPaintFlags(legendColoreTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		final TextView legendValoreTitle = (TextView) findViewById(R.id.legendValoreTitle);
		legendValoreTitle.setPaintFlags(legendValoreTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
	}

}
package com.happymeteo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class MenuActivity extends Activity {
	
	/* The BroadcastReceiver needs to be in the same class where registerReceiver is called */
	public BroadcastReceiver broadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		/* Initialize PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().initialize(
				getApplicationContext());
		
		if(!HappyMeteoApplication.getPushNotificationsService().getRegistrationId().equals("")) {
			/* Register device on happymeteo backend */
			ServerUtilities.registerDevice(getApplicationContext(), HappyMeteoApplication.getPushNotificationsService().getRegistrationId());
		}
		
		Button btnInformationPage = (Button) findViewById(R.id.btnInformationPage);
		Button btnHappyMeteo = (Button) findViewById(R.id.btnHappyMeteo);
		Button btnHappyContext = (Button) findViewById(R.id.btnHappyContext);
		Button btnHappyMap = (Button) findViewById(R.id.btnHappyMap);
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		
		btnInformationPage.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, InformationPageActivity.class);
				context.startActivity(i);
			}
		});
		
		btnHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, HappyMeteoActivity.class);
				context.startActivity(i);
			}
		});
		
		btnHappyContext.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, HappyContextActivity.class);
				context.startActivity(i);
			}
		});
		
		btnHappyMap.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, HappyMapActivity.class);
				context.startActivity(i);
			}
		});
		
		btnBeginQuestions.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, QuestionBeginActivity.class);
				context.startActivity(i);
			}
		});
		
		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/* Close Facebook Session */
				HappyMeteoApplication.getFacebookSessionService().onClickLogout();
				
				/* Return to index activity */
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		Log.i(Const.TAG, "unregisterReceiver "+broadcastReceiver);
		
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().terminate(
				getApplicationContext());

		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}

package com.happymeteo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.Settings;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.ConnectionDetector;

public class IndexActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);

		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

		/* Check internet */
		if (!cd.isConnectingToInternet()) {
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			return;
		}

		Settings.addLoggingBehavior(LoggingBehavior.CACHE);

		HappyMeteoApplication.getFacebookSessionService().initialize(
				getApplicationContext(), savedInstanceState, this);

		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		Button btnLoginHappyMeteo = (Button) findViewById(R.id.btnLoginHappyMeteo);
		Button btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);
		
		Button btnLogout = (Button) findViewById(R.id.btnLogoutFacebook);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, CreateAccountActivity.class);
				context.startActivity(i);
			}
		});

		btnLoginHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent i = new Intent(context, NormalLoginActivity.class);
				context.startActivity(i);
			}
		});

		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				HappyMeteoApplication.getFacebookSessionService()
						.onClickLogin();
			}
		});
		
		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				/* Close Facebook Session */
				HappyMeteoApplication.getFacebookSessionService().onClickLogout();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// TODO move in FacebookSessionService
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// TODO move in FacebookSessionService
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}
}

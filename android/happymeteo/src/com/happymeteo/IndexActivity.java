package com.happymeteo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.ConnectionDetector;
import com.happymeteo.utils.Const;

public class IndexActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		
		Log.i(Const.TAG, "Create IndexActivity");
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());

		/* Check internet */
		if (!cd.isConnectingToInternet()) {
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(this, "Internet Connection Error",
					"Please connect to working Internet connection", false,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			return;
		}
		
		/* Initialize HappyMeteoApplication */
		HappyMeteoApplication.i().getFacebookSessionService().initialize(this);
		
		Button btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
		Button btnLoginHappyMeteo = (Button) findViewById(R.id.btnLoginHappyMeteo);
		Button btnLoginFacebook = (Button) findViewById(R.id.btnLoginFacebook);

		btnCreateAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, CreateAccountActivity.class);
				context.startActivity(intent);
			}
		});

		btnLoginHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, NormalLoginActivity.class);
				context.startActivity(intent);
			}
		});

		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, LoadingActivity.class);
				intent.putExtra("action", 1);
				context.startActivity(intent);
			}
		});
	}
}

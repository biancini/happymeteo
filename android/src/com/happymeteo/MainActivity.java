package com.happymeteo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class MainActivity extends Activity {
	
	/* The BroadcastReceiver needs to be in the same class where registerReceiver is called */
	public BroadcastReceiver broadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Initialize PushNotificationsService */
		HappyMeteoApplication.getPushNotificationsService().initialize(
				getApplicationContext());
		
		if(!HappyMeteoApplication.getPushNotificationsService().getRegistrationId().equals("")) {
			/* Register device on happymeteo backend */
			ServerUtilities.register(getApplicationContext(), HappyMeteoApplication.getPushNotificationsService().getRegistrationId());
		}
		
		Button btnInformationPage = (Button) findViewById(R.id.btnInformationPage);
		Button btnMeteoDellaFelicita = (Button) findViewById(R.id.btnMeteoDellaFelicita);
		Button btnContestoDellaFelicita = (Button) findViewById(R.id.btnContestoDellaFelicita);
		Button btnMappaDellaFelicita = (Button) findViewById(R.id.btnMappaDellaFelicita);
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		
		btnInformationPage.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				//TODO Information Page
				Toast.makeText(getApplicationContext(), "Information Page",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		btnMeteoDellaFelicita.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				//TODO Meteo della felicita
				Toast.makeText(getApplicationContext(), "Meteo della felicita",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		btnContestoDellaFelicita.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				//TODO Contesto della felicita
				Toast.makeText(getApplicationContext(), "Contesto della felicita",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		btnMappaDellaFelicita.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				
				//TODO Mappa della felicita
				Toast.makeText(getApplicationContext(), "Mappa della felicita",
						Toast.LENGTH_SHORT).show();
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

}

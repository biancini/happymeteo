package com.happymeteo;

import java.util.HashMap;
import java.util.List;

import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class AppyMeteoImpulseActivity extends
		AppyMeteoNotLoggedActivity {

	protected HashMap<String, String> intentParameters;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		intentParameters = new HashMap<String, String>();
		
		initialize(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		initialize(intent);
	}

	public void initialize(Intent intent) {
		List<String> keyIntentParamteres = getKeyIntentParameters();

		if (keyIntentParamteres == null || keyIntentParamteres.isEmpty() || intent.getExtras() == null) {
			AlertDialogManager alert = new AlertDialogManager();
			alert.showAlertDialog(this, "Errore", "C'è stato un errore", false,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			return;
		}
		
		for(String key : keyIntentParamteres) {
			Log.i(Const.TAG, "initialize: "+key+" => "+intent.getExtras().getString(key));
			intentParameters.put(key, intent.getExtras().getString(key));
		}
	}

	public abstract List<String> getKeyIntentParameters();
}

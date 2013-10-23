package com.happymeteo;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;

public abstract class ImpulseActivity extends NotLoggedActivity {

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
			AlertDialogManager.showError(this, this.getString(R.string.error_impulse));
			return;
		}
		
		for(String key : keyIntentParamteres) {
			Log.i(Const.TAG, "initialize: "+key+" => "+intent.getExtras().getString(key));
			intentParameters.put(key, intent.getExtras().getString(key));
		}
	}

	public abstract List<String> getKeyIntentParameters();
}

package com.happymeteo;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;

public abstract class ImpulseActivity extends NotLoggedActivity {
	protected HashMap<String, String> intentParameters = null;

	@Override
	public final void onCreate(Bundle savedInstanceState) {
		setContentView(getContentView());
		super.onCreate(savedInstanceState);

		intentParameters = new HashMap<String, String>();
		Log.i(Const.TAG, "initialize onCreate");
		initialize(getIntent());
		onCreation();
		showActivity();
	}

	@Override
	protected final void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(Const.TAG, "initialize onNewIntent");
		initialize(intent);
		showActivity();
	}

	public void initialize(Intent intent) {
		List<String> keyIntentParamteres = getKeyIntentParameters();

		if (keyIntentParamteres == null || keyIntentParamteres.isEmpty() || intent.getExtras() == null) {
			AlertDialogManager.showError(this, this.getString(R.string.error_impulse));
			return;
		}
		
		for(String key : keyIntentParamteres) {
			Log.i(Const.TAG, "initialize: " + key + " => " + intent.getExtras().getString(key));
			intentParameters.put(key, intent.getExtras().getString(key));
		}
	}

	public abstract List<String> getKeyIntentParameters();
	
	public abstract void showActivity();
	
	public abstract int getContentView();
	
	public abstract void onCreation();
}

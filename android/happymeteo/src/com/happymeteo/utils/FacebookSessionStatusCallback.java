package com.happymeteo.utils;

import android.app.Activity;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookSessionStatusCallback implements Session.StatusCallback {
	private OnFacebookExecuteListener onFacebookExecuteListener;
	private Activity activity;
	
	public FacebookSessionStatusCallback(Activity activity, OnFacebookExecuteListener onFacebookExecuteListener) {
		this.activity = activity;
		this.onFacebookExecuteListener = onFacebookExecuteListener;
	}
	
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		Log.i(Const.TAG, "SessionStatusCallback state: " + state);
		
		if (exception != null) {
			AlertDialogManager.showError(activity, exception.getMessage());
			Log.e(Const.TAG, exception.getMessage(), exception);
			return;
		}
		
		onFacebookExecuteListener.OnFacebookExecute(session, state);
	}
}

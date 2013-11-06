package com.happymeteo.utils;

import com.facebook.Session;
import com.facebook.SessionState;

public interface OnFacebookExecuteListener {
	public void OnFacebookExecute(Session session, SessionState state);
}

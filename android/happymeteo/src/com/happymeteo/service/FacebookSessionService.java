package com.happymeteo.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.FacebookException;
import com.happymeteo.CreateAccountActivity;
import com.happymeteo.HappyMeteoApplication;
import com.happymeteo.MenuActivity;
import com.happymeteo.facebook.FacebookAuthDialog;
import com.happymeteo.facebook.FacebookAuthDialog.OnCompleteListener;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class FacebookSessionService implements OnCompleteListener {
	
	private void openConnession(Activity activity) {
		Log.i(Const.TAG, "openConnession");
		String url = "https://m.facebook.com/dialog/oauth?display=touch"
				+ "&client_id="+Const.FACEBOOK_ID
				+ "&scope="+Const.getFacebookReadPermission()
				+ "&type=user_agent"
				+ "&redirect_uri="+Const.BASE_URL;
		
		Log.i(Const.TAG, "url open facebook connession: "+url);
		
		FacebookAuthDialog facebookAuthDialog = new FacebookAuthDialog(activity, url);
		facebookAuthDialog.setOnCompleteListener(this);
		facebookAuthDialog.show();
	}
	
	private boolean tryLoginUser(Activity activity) {
		SharedPreferences preferences = HappyMeteoApplication.i().getSharedPreferences();
		String accessToken = preferences.getString("accessToken", null);
		Log.i(Const.TAG, "accessToken: "+accessToken);
		
		if(accessToken != null) {
			// Logged
			Log.i(Const.TAG, "Logged");
			User user = ServerUtilities.facebookLogin(accessToken);
			
			if(user != null) {
				HappyMeteoApplication.i().setFacebookSession(true);
				
				if(user.getRegistered() == User.USER_NOT_REGISTERED) {
					// Switch to create account activity if not registered
					Intent intent = new Intent(activity.getApplicationContext(), CreateAccountActivity.class);
					intent.putExtra("facebook_id", user.getFacebook_id());
					intent.putExtra("first_name", user.getFirst_name());
					intent.putExtra("last_name", user.getLast_name());
					intent.putExtra("gender", user.getGender());
					intent.putExtra("email", user.getEmail());
					intent.putExtra("age", user.getAge());
					intent.putExtra("education", user.getEducation());
					intent.putExtra("work", user.getWork());
					intent.putExtra("location", user.getLocation());
					
					Log.i(Const.TAG, "CreateAccountActivity startActivity");
					activity.startActivity(intent);
				} else {
					HappyMeteoApplication.i().setCurrentUser(user);
					
					// Switch to menu activity if registered
					Intent intent = new Intent(activity.getApplicationContext(), MenuActivity.class);
					activity.startActivity(intent);
				}
				return true;
			}
		}
		return false;
	}
	
	public void initialize(Activity activity) {
		tryLoginUser(activity);
	}
	
	public void onClickLogin(Activity activity) {
		if(!tryLoginUser(activity)) {
			openConnession(activity);
		}
	}

	public void onClickLogout(Context context) {
		// setCookie acts differently when trying to expire cookies between builds of Android that are using
        // Chromium HTTP stack and those that are not. Using both of these domains to ensure it works on both.
    	clearCookiesForDomain(context, "facebook");
        clearCookiesForDomain(context, "facebook.com");
        clearCookiesForDomain(context, ".facebook.com");
        clearCookiesForDomain(context, "https://facebook.com");
        clearCookiesForDomain(context, "https://.facebook.com");
	}
	
	private void clearCookiesForDomain(Context context, String domain) {
        // This is to work around a bug where CookieManager may fail to instantiate if CookieSyncManager
        // has never been created.
        CookieSyncManager syncManager = CookieSyncManager.createInstance(context);
        syncManager.sync();

        CookieManager cookieManager = CookieManager.getInstance();

        String cookies = cookieManager.getCookie(domain);
        if (cookies == null) {
            return;
        }

        String[] splitCookies = cookies.split(";");
        for (String cookie : splitCookies) {
            String[] cookieParts = cookie.split("=");
            if (cookieParts.length > 0) {
                String newCookie = cookieParts[0].trim() + "=;expires=Sat, 1 Jan 2000 00:00:01 UTC;";
                cookieManager.setCookie(domain, newCookie);
            }
        }
        cookieManager.removeExpiredCookie();
    }

	@Override
	public void onComplete(Bundle values, FacebookException error, Activity activity) {
		Log.i(Const.TAG, "OnCompleteListener");
		if(values != null) {
			for(String key : values.keySet()) {
				Log.i(Const.TAG, key+": "+values.getString(key));
			}
			
			SharedPreferences preferences = HappyMeteoApplication.i().getSharedPreferences();
			Editor editor = preferences.edit();
			editor.putString("accessToken", values.getString("access_token"));
			editor.commit();
			
			Log.i(Const.TAG, "access_token from v: "+values.getString("access_token"));
			Log.i(Const.TAG, "access_token from p: "+preferences.getString("accessToken", null));
		}
		
		if(error == null) {
			tryLoginUser(activity);
		} else {
			// Not logged
			Log.i(Const.TAG, "Not logged");
			activity.finish();
		}
	}
}
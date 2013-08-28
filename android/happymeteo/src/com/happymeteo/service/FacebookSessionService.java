package com.happymeteo.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

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
	private Context context;
	private String accessToken;
	private String expiresIn;
	private Activity activity;
	
	private void openConnession(Activity activity) {
		this.accessToken = null;
		this.expiresIn = null;
		Log.i(Const.TAG, "openConnession");
		String url = "https://m.facebook.com/dialog/oauth?display=touch"
				+ "&client_id=405414319576008"
				+ "&scope=email%2Cuser_birthday"
				+ "&type=user_agent"
				+ "&redirect_uri="+Const.BASE_URL;
		
		FacebookAuthDialog webDialog = new FacebookAuthDialog(activity, url);
		webDialog.setOnCompleteListener(this);
		webDialog.show();
	}
	
	public boolean initialize(Context context, Bundle savedInstanceState, Activity activity) {
		this.context = context;
		
		/*Settings.addLoggingBehavior(LoggingBehavior.CACHE);
		
		Session session = Session.getActiveSession();
		if (session == null) {
			if (savedInstanceState != null) {
				Log.i(Const.TAG, "restoreSession");
				session = Session.restoreSession(context, null,
						statusCallback, savedInstanceState);
			}
			if (session == null) {
				Log.i(Const.TAG, "new Session");
				session = new Session(context);
			}
			Session.setActiveSession(session);
			Log.i(Const.TAG, "session.getState(): "+session.getState());
			if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
				openConnession(session);
				return true;
			}
		}*/
		return false;
	}
	
	public void onClickLogin(Activity activity) {
		this.activity = activity;
		openConnession(activity);
	}

	public void onClickLogout() {
		clearFacebookCookies(context);
	}
	
	private static void clearCookiesForDomain(Context context, String domain) {
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

    public static void clearFacebookCookies(Context context) {
        // setCookie acts differently when trying to expire cookies between builds of Android that are using
        // Chromium HTTP stack and those that are not. Using both of these domains to ensure it works on both.
    	clearCookiesForDomain(context, "facebook");
        clearCookiesForDomain(context, "facebook.com");
        clearCookiesForDomain(context, ".facebook.com");
        clearCookiesForDomain(context, "https://facebook.com");
        clearCookiesForDomain(context, "https://.facebook.com");
    }

	@Override
	public void onComplete(Bundle values, FacebookException error) {
		Log.i(Const.TAG, "OnCompleteListener");
		if(values != null) {
			for(String key : values.keySet()) {
				Log.i(Const.TAG, key+": "+values.getString(key));
			}
			
			Log.i(Const.TAG, "FacebookException: "+error);
			
			this.accessToken = values.getString("access_token");
			this.expiresIn = values.getString("expires_in");
		}
		
		if(error == null && accessToken != null) {
			// Logged
			Toast.makeText(this.context, "Logged with facebook", Toast.LENGTH_SHORT).show();
			Log.i(Const.TAG, "accessToken: "+accessToken);
			
			// Call CommonUtilities.FACEBOOK_LOGIN_URL
			User user = ServerUtilities.facebookLogin(accessToken);
			
			// Set current user
			HappyMeteoApplication.setCurrentUser(user);
			HappyMeteoApplication.setFacebookSession(true);
			
			if(user != null) {
				if(user.getRegistered() == User.USER_NOT_REGISTERED) {
					// Switch to create account activity if not registered
					Intent intent = new Intent(context, CreateAccountActivity.class);
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
					// Switch to menu activity if registered
					Intent intent = new Intent(context, MenuActivity.class);
					activity.startActivity(intent);
				}
			}
		} else {
			// Not logged
			Toast.makeText(this.context, "Not logged with facebook", Toast.LENGTH_SHORT).show();
		}
	}
}
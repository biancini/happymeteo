package com.happymeteo.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.facebook.FacebookException;
import com.happymeteo.AppyMeteoNotLoggedActivity;
import com.happymeteo.CreateAccountActivity;
import com.happymeteo.HappyMeteoApplication;
import com.happymeteo.MenuActivity;
import com.happymeteo.facebook.AuthDialog;
import com.happymeteo.facebook.WebDialog.OnCompleteListener;
import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class FacebookSessionService implements OnCompleteListener, onPostExecuteListener {
	private AppyMeteoNotLoggedActivity lastActivity;
	
	public void openConnession(OnCompleteListener onCompleteListener) {
		String url = "https://m.facebook.com/dialog/oauth?display=touch"
				+ "&client_id="+Const.FACEBOOK_ID
				+ "&scope="+Const.getFacebookReadPermission()
				+ "&type=user_agent"
				+ "&redirect_uri="+Const.BASE_URL;
		
		AuthDialog facebookAuthDialog = new AuthDialog(lastActivity, url);
		facebookAuthDialog.setOnCompleteListener(onCompleteListener);
		facebookAuthDialog.show();
	}
	
	private void tryLoginUser() {
		String accessToken = HappyMeteoApplication.i().getAccessToken();
		if(accessToken != null) {
			ServerUtilities.facebookLogin(this, lastActivity, accessToken);
		} else {
			openConnession(this);
		}
	}
	
	public void onClickLogin(AppyMeteoNotLoggedActivity activity) {
		lastActivity = activity;
		tryLoginUser();
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
	public void onComplete(Bundle values, FacebookException error, AppyMeteoNotLoggedActivity caller) {
		if(values != null) {
			HappyMeteoApplication.i().setAccessToken(values.getString("access_token"));
		}
		
		if(error == null) {
			tryLoginUser();
		} else {
			caller.finish();
		}
	}

	@Override
	public void onPostExecute(int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			User user = new User(jsonObject);
			
			if(user != null) {
				HappyMeteoApplication.i().setFacebookSession(true);
				HappyMeteoApplication.i().setCurrentUser(user);
				
				if(user.getRegistered() == User.USER_NOT_REGISTERED) {
					lastActivity.invokeActivity(CreateAccountActivity.class);
				} else {
					lastActivity.invokeActivity(MenuActivity.class);
				}
				return;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		openConnession(this);
	}
}
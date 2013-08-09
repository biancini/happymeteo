package com.happymeteo.utils;

public final class Const {
	
	// give your server registration url here
	public static final String BASE_URL = "https://happymeteo.appspot.com";
	public static final String FACEBOOK_LOGIN_URL = BASE_URL + "/facebook_login";
	public static final String NORMAL_LOGIN_URL = BASE_URL + "/normal_login";
	public static final String REGISTER_URL = BASE_URL + "/register"; 
	public static final String UNREGISTER_URL = BASE_URL + "/unregister"; 

    // Google project id
	public static final String SENDER_ID = "347057775979"; 

    /**
     * Tag used on log messages.
     */
	public static final String TAG = "HappyMeteo";

	/* DEVE ESSERE nello stesso category scelto nel manifest, da non modificare */
	public static final String DISPLAY_MESSAGE_ACTION =
            "com.happymeteo.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";
}

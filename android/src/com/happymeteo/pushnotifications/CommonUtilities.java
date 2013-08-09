package com.happymeteo.pushnotifications;

public final class CommonUtilities {
	
	// give your server registration url here
	public static final String BASE_URL = "http://happymeteo.appspot.com";
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

package com.happymeteo.pushnotifications;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
	public static final String BASE_URL = "http://happymeteo.appspot.com";
	public static final String LOGIN_FACEBOOK_URL = BASE_URL + "/auth/facebook"; 
	public static final String REGISTER_URL = BASE_URL + "/register"; 
	public static final String UNREGISTER_URL = BASE_URL + "/unregister"; 

    // Google project id
	public static final String SENDER_ID = "347057775979"; 

    /**
     * Tag used on log messages.
     */
	public static final String TAG = "HappyMeteo";

	public static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}

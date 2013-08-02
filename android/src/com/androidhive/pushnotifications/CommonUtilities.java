package com.androidhive.pushnotifications;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
    static final String BASE_URL = "http://10.0.2.2:8080";
    static final String REGISTER_URL = BASE_URL + "/register"; 
    static final String UNREGISTER_URL = BASE_URL + "/unregister"; 

    // Google project id
    static final String SENDER_ID = "347057775979"; 

    /**
     * Tag used on log messages.
     */
    static final String TAG = "HappyMeteo";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "data";

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

package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	private TextView today_text;
	private TextView yesterday_text;
	private TextView tomorrow_text;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
		/* Initialize PushNotificationsService */
		if(!HappyMeteoApplication.i().getPushNotificationsService().initialize(this)) {
			ServerUtilities.registerDevice(
					this,
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId(), 
					HappyMeteoApplication.i().getCurrentUser().getUser_id());
		}
		
		ServerUtilities.happyMeteo(this, this);
		
		Typeface helveticaneueltstd = Typeface.createFromAsset(getAssets(), "helveticaneueltstd.ttf");
		
		today_text = (TextView) findViewById(R.id.today_text);
		today_text.setTypeface(helveticaneueltstd);
		
		yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setTypeface(helveticaneueltstd);
		
		tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setTypeface(helveticaneueltstd);
		
		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.profile_picture);

		if (HappyMeteoApplication.i().isFacebookSession()) {
			userImage.setProfileId(String.valueOf(HappyMeteoApplication
					.i().getCurrentUser().getFacebook_id()));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}
		
		//JSONObject json = ServerUtilities.happyMeteo(getApplicationContext());
		//Log.i(Const.TAG, "json: " + json);
		
		/*RelativeLayout relativeLayoutMeteoUp = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUp);
        
		
		
		RelativeLayout.LayoutParams userImageLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		//userImageLayout.gravity = Gravity.BOTTOM;
		
		relativeLayoutMeteoUp.addView(userImage, userImageLayout);*/
		
	}

	@Override
	public void onPostExecute(int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			Log.i(Const.TAG, "json: " + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().terminate(getApplicationContext());

		super.onDestroy();
	}

}

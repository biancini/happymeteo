package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.service.PushNotificationsService;
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
		
		/*SharedPreferences properties = getApplicationContext().getSharedPreferences(Const.TAG, MODE_PRIVATE);
		
		Log.i(Const.TAG, "startAppyMeteo: "+properties.getString("startAppyMeteo", null));
		
		Editor edit = properties.edit();
		edit.clear();
		edit.putString("startAppyMeteo", (new Date()).toString());
		edit.commit();*/
		
		/* Initialize PushNotificationsService */
		PushNotificationsService.register(getApplicationContext(), HappyMeteoApplication.i().getCurrentUser().getUser_id());
		
		ServerUtilities.happyMeteo(this, this);
		
		Typeface helveticaneueltstd_ultlt_webfont = Typeface.createFromAsset(getAssets(), "helveticaneueltstd-ultlt-webfont.ttf");
		
		today_text = (TextView) findViewById(R.id.today_text);
		today_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
		yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
		tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
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
	public void onPostExecute(int id, String result, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			Log.i(Const.TAG, "json: " + jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}
}

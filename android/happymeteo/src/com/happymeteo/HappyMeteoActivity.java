package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.onPostExecuteListener;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
		//ServerUtilities.happyMeteo(this, this);
		
		Log.i(Const.TAG, "suca font");
		
		Typeface helveticaneueltstd = Typeface.createFromAsset(getAssets(), "helveticaneueltstd.ttf");
		
		TextView today_text = (TextView) findViewById(R.id.today_text);
		today_text.setTypeface(helveticaneueltstd);
		
		TextView yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setTypeface(helveticaneueltstd);
		
		TextView tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setTypeface(helveticaneueltstd);
		
		Log.i(Const.TAG, "suca font 2");
		
		//JSONObject json = ServerUtilities.happyMeteo(getApplicationContext());
		//Log.i(Const.TAG, "json: " + json);
		
		/*RelativeLayout relativeLayoutMeteoUp = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUp);
        
		ProfilePictureView userImage = new ProfilePictureView(getApplicationContext());

		if (HappyMeteoApplication.i().isFacebookSession()) {
			userImage.setProfileId(String.valueOf(HappyMeteoApplication
					.i().getCurrentUser().getFacebook_id()));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}
		
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

}

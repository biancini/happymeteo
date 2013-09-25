package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.models.User;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	private TextView today_text;
	private TextView yesterday_text;
	private TextView tomorrow_text;
	private LinearLayout linearLayoutMeteoUp;
	
	private int[] getColorByToday(int today) {
		int colors[] = {0, 0};
		switch(today) {
			case 1:
				colors[0] = 0xff7bccff;
				colors[1] = 0xff2ea6ff;
				break;
			case 2:
				colors[0] = 0xff2ea6ff;
				colors[1] = 0xff0071bc;
				break;
			case 3:
				colors[0] = 0xff0071bc;
				colors[1] = 0xff4a4998;
				break;
			case 4:
				colors[0] = 0xff93278f;
				colors[1] = 0xff4a4998;
				break;
			case 5:
				colors[0] = 0xffed1e79;
				colors[1] = 0xffae3771;
				break;
			case 6:
				colors[0] = 0xffff0000;
				colors[1] = 0xfffd3771;
				break;
			case 7:
				colors[0] = 0xff01700d;
				colors[1] = 0xfffd3700;
				break;
			case 8:
				colors[0] = 0xfff7931e;
				colors[1] = 0xfffd6c00;
				break;
			case 9:
				colors[0] = 0xfff9c33d;
				colors[1] = 0xfffd9200;
				break;
			case 10:
				colors[0] = 0xffffe400;
				colors[1] = 0xfffdc800;
				break;
		}
		
		return colors;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
		/* Initialize PushNotificationsService */
		PushNotificationsService.register(getApplicationContext(), User.getUser_id(this));
		
		ServerUtilities.happyMeteo(this, this);
		
		Typeface helveticaneueltstd_ultlt_webfont = Typeface.createFromAsset(getAssets(), "helveticaneueltstd-ultlt-webfont.ttf");
		
		today_text = (TextView) findViewById(R.id.today_text);
		today_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
		yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
		tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		
		linearLayoutMeteoUp = (LinearLayout) findViewById(R.id.linearLayoutMeteoUp);
		
		TextView welcomeToday = (TextView) findViewById(R.id.welcomeToday);
		welcomeToday.setText(User.getFirst_name(this).toLowerCase()+"_OGGI");
		
		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.profile_picture);
		if (User.isFacebookSession(this)) {
			userImage.setProfileId(User.getFacebook_id(this));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}
	}
	
	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			Log.i(Const.TAG, "json: " + jsonObject);
			
			String today = "10";
			String yesterday = "3";
			String tomorrow = "7";
			
			today_text.setText(today);
			yesterday_text.setText(yesterday);
			tomorrow_text.setText(tomorrow);
			
			int today_int = Integer.valueOf(today);
			GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, getColorByToday(today_int));
			linearLayoutMeteoUp.setBackgroundDrawable(gradientDrawable);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

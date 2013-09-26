package com.happymeteo;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.models.User;
import com.happymeteo.service.PushNotificationsService;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity {
	
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
	
	private int getWhiteIcon(int day) {
		switch(day) {
			case 1:
				return R.drawable.white_1happy;
			case 2:
				return R.drawable.white_2happy;
			case 3:
				return R.drawable.white_3happy;
			case 4:
				return R.drawable.white_4happy;
			case 5:
				return R.drawable.white_5happy;
			case 6:
				return R.drawable.white_6happy;
			case 7:
				return R.drawable.white_7happy;
			case 8:
				return R.drawable.white_8happy;
			case 9:
				return R.drawable.white_9happy;
			case 10:
				return R.drawable.white_10happy;
		}
		
		return R.drawable.white_1happy;
	}
	
	private int getGrayIcon(int day) {
		switch(day) {
			case 1:
				return R.drawable.gray_1happy;
			case 2:
				return R.drawable.gray_2happy;
			case 3:
				return R.drawable.gray_3happy;
			case 4:
				return R.drawable.gray_4happy;
			case 5:
				return R.drawable.gray_5happy;
			case 6:
				return R.drawable.gray_6happy;
			case 7:
				return R.drawable.gray_7happy;
			case 8:
				return R.drawable.gray_8happy;
			case 9:
				return R.drawable.gray_9happy;
			case 10:
				return R.drawable.gray_10happy;
		}
		
		return R.drawable.white_1happy;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
		/* Initialize PushNotificationsService */
		PushNotificationsService.register(getApplicationContext(), User.getUser_id(this));
		
		//ServerUtilities.happyMeteo(this, this);
		
		Typeface helveticaneueltstd_ultlt_webfont = Typeface.createFromAsset(getAssets(), "helveticaneueltstd-ultlt-webfont.ttf");
		
		int today_int = User.getToday(this);
		int yesterday_int = User.getYesterday(this);
		int tomorrow_int = User.getTomorrow(this);
		
		TextView today_text = (TextView) findViewById(R.id.today_text);
		today_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		today_text.setText(String.valueOf(today_int));
		
		TextView yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		yesterday_text.setText(String.valueOf(yesterday_int));
		
		TextView tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		tomorrow_text.setText(String.valueOf(tomorrow_int));
		
		ImageView today_pic = (ImageView) findViewById(R.id.today_pic);
		today_pic.setImageResource(getWhiteIcon(today_int));
		ImageView yesterday_pic = (ImageView) findViewById(R.id.yesterday_pic);
		yesterday_pic.setImageResource(getGrayIcon(yesterday_int));
		ImageView tomorrow_pic = (ImageView) findViewById(R.id.tomorrow_pic);
		tomorrow_pic.setImageResource(getGrayIcon(tomorrow_int));
		
		LinearLayout linearLayoutMeteoUp = (LinearLayout) findViewById(R.id.linearLayoutMeteoUp);
		GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, getColorByToday(today_int));
		linearLayoutMeteoUp.setBackgroundDrawable(gradientDrawable);
		
		TextView welcomeToday = (TextView) findViewById(R.id.welcomeToday);
		welcomeToday.setText(User.getFirst_name(this).toLowerCase()+"_OGGI");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.profile_picture);
		if (User.isFacebookSession(this)) {
			userImage.setProfileId(User.getFacebook_id(this));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}
	}
}

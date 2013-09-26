package com.happymeteo;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.models.User;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.Const;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity {
	private ViewFlipper viewFlipper;
	private float lastX;

	private int[] getColorByToday(int today) {
		int colors[] = { 0, 0 };
		switch (today) {
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
		switch (day) {
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
		switch (day) {
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
		PushNotificationsService.register(getApplicationContext(),
				User.getUser_id(this));

		// ServerUtilities.happyMeteo(this, this);

		Typeface helveticaneueltstd_ultlt_webfont = Typeface.createFromAsset(
				getAssets(), "helveticaneueltstd-ultlt-webfont.ttf");

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

		TextView welcomeToday = (TextView) findViewById(R.id.welcomeToday);
		welcomeToday.setText(User.getFirst_name(this).toLowerCase() + "_OGGI");

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperUp);
		
		RelativeLayout relativeLayoutMeteoUp1 = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUp1);
		GradientDrawable gradientDrawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				getColorByToday(today_int));
		relativeLayoutMeteoUp1.setBackgroundDrawable(gradientDrawable);

		RelativeLayout relativeLayoutMeteoUp2 = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUp2);
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
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		if(touchevent.getY() > viewFlipper.getHeight()) {
			return super.onTouchEvent(touchevent);
		}
		
		switch (touchevent.getAction()) {
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN:
			Log.i(Const.TAG, "ACTION_DOWN");
			lastX = touchevent.getX();
			break;
		case MotionEvent.ACTION_UP:
			Log.i(Const.TAG, "ACTION_UP");
			float currentX = touchevent.getX();

			// if left to right swipe on screen
			if (lastX < currentX) {
				// If no more View/Child to flip
				if (viewFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen
				// will go OUT from Right
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				// Show the next Screen
				viewFlipper.showNext();
			}

			// if right to left swipe on screen
			if (lastX > currentX) {
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen
				// will go OUT from Left
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				// Show The Previous Screen
				viewFlipper.showPrevious();
			}
			break;
		}
		return super.onTouchEvent(touchevent);
	}
}

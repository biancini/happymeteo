package com.happymeteo;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.widget.ProfilePictureView;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
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

}

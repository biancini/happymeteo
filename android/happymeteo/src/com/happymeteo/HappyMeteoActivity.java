package com.happymeteo;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class HappyMeteoActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_happy_meteo);
		
		LinearLayout linearLayoutMeteo = (LinearLayout) findViewById(R.id.linearLayoutMeteo);
        //RelativeLayout relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		//RelativeLayout relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        
        Log.i(Const.TAG, "linearLayoutMeteo.getHeight(): "+linearLayoutMeteo.getHeight());
        Log.i(Const.TAG, "linearLayoutMeteo.getLayoutParams().height: "+linearLayoutMeteo.getLayoutParams().height);
        
        /*RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, linearLayoutMeteo.getHeight()/2);
        
        relativeLayout1.setLayoutParams(layoutParams);
        relativeLayout2.setLayoutParams(layoutParams);
		
		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.userImage);

		if (HappyMeteoApplication.i().isFacebookSession()) {
			
			userImage.setProfileId(String.valueOf(HappyMeteoApplication
					.i().getCurrentUser().getFacebook_id()));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}*/
		
		JSONObject json = ServerUtilities.happyMeteo(getApplicationContext());
		Log.i(Const.TAG, "json: " + json);
	}

}

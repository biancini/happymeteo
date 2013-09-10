package com.happymeteo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class MenuActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		Log.i(Const.TAG, "Create MenuActivity");

		/* Initialize PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().initialize(
				getApplicationContext());

		if (!HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId().equals("") 
				&& HappyMeteoApplication.i().getCurrentUser() != null) {
			/* Register device on happymeteo backend */
			ServerUtilities.registerDevice(
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId(),
					HappyMeteoApplication.i().getCurrentUser().getUser_id());
		}

		if (HappyMeteoApplication.i().isFacebookSession()) {
			ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.userImage);
			userImage.setProfileId(String.valueOf(HappyMeteoApplication
					.i().getCurrentUser().getFacebook_id()));
			userImage.setCropped(true);
		}

		Button btnInformationPage = (Button) findViewById(R.id.btnInformationPage);
		Button btnHappyMeteo = (Button) findViewById(R.id.btnHappyMeteo);
		Button btnHappyContext = (Button) findViewById(R.id.btnHappyContext);
		Button btnHappyMap = (Button) findViewById(R.id.btnHappyMap);
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		Button btnChallenge = (Button) findViewById(R.id.btnChallenge);
		Button btnChallengeTry = (Button) findViewById(R.id.btnChallengeTry);
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		Button btnLogout2 = (Button) findViewById(R.id.btnLogout2);

		btnInformationPage.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context,
						InformationPageActivity.class);
				context.startActivity(intent);
			}
		});

		btnHappyMeteo.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, HappyMeteoActivity.class);
				context.startActivity(intent);
			}
		});

		btnHappyContext.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, HappyContextActivity.class);
				context.startActivity(intent);
			}
		});

		btnHappyMap.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, HappyMapActivity.class);
				context.startActivity(intent);
			}
		});

		btnBeginQuestions.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, QuestionActivity.class);
				context.startActivity(intent);
			}
		});
		
		btnChallenge.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, ChallengeActivity.class);
				context.startActivity(intent);
			}
		});
		
		btnChallengeTry.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.requestChallenge(
					HappyMeteoApplication.i().getCurrentUser().getUser_id(),
					HappyMeteoApplication.i().getCurrentUser().getFacebook_id(),
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId());
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				
				/* Terminate PushNotificationsService */
				HappyMeteoApplication.i().getPushNotificationsService().terminate(
						getApplicationContext());
				
				/* Close Facebook Session */
				HappyMeteoApplication.i().getFacebookSessionService()
						.onClickLogout(context);

				/* Return to index activity */
				Intent intent = new Intent(context, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
		
		btnLogout2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				
				HappyMeteoApplication.i().setAccessToken("");
				
				/* Terminate PushNotificationsService */
				HappyMeteoApplication.i().getPushNotificationsService().terminate(
						getApplicationContext());
				
				/* Close Facebook Session */
				HappyMeteoApplication.i().getFacebookSessionService()
						.onClickLogout(context);

				/* Return to index activity */
				Intent intent = new Intent(context, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		/* Terminate PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().terminate(
				getApplicationContext());

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

}

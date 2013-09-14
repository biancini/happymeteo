package com.happymeteo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.happymeteo.utils.ServerUtilities;

public class MenuActivity extends AppyMeteoLoggedActivity {
	private AppyMeteoNotLoggedActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_menu);
		super.onCreate(savedInstanceState);
		
		this.activity = this;
		
		/* Initialize PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().initialize(
				getApplicationContext());

		if (!HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId().equals("") 
				&& HappyMeteoApplication.i().getCurrentUser() != null) {
			/* Register device on happymeteo backend */
			ServerUtilities.registerDevice(
					getApplicationContext(), 
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId(),
					HappyMeteoApplication.i().getCurrentUser().getUser_id());
		}
		
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		Button btnChallengeTry = (Button) findViewById(R.id.btnChallengeTry);
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		Button btnLogout2 = (Button) findViewById(R.id.btnLogout2);
		
		btnBeginQuestions.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, QuestionActivity.class);
				context.startActivity(intent);
			}
		});
		
		btnChallengeTry.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.requestChallenge(
					activity,
					HappyMeteoApplication.i().getCurrentUser().getUser_id(),
					HappyMeteoApplication.i().getCurrentUser().getFacebook_id(),
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId());
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				
				HappyMeteoApplication.i().logout(context);

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
				
				HappyMeteoApplication.i().logout(context);

				/* Return to index activity */
				Intent intent = new Intent(context, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		HappyMeteoApplication.i().logout(getApplicationContext());

		super.onDestroy();
	}
}

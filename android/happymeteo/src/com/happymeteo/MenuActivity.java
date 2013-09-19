package com.happymeteo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.utils.ServerUtilities;

public class MenuActivity extends AppyMeteoLoggedActivity {
	private AppyMeteoNotLoggedActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_menu);
		super.onCreate(savedInstanceState);
		
		this.activity = this;
		
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		Button btnChallengeTry = (Button) findViewById(R.id.btnChallengeTry);

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
					GCMRegistrar.getRegistrationId(getApplicationContext()));
			}
		});
	}

	
}

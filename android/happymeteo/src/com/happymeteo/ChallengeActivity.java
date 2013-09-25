package com.happymeteo;

import com.happymeteo.models.User;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChallengeActivity extends AppyMeteoLoggedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge);
		super.onCreate(savedInstanceState);

		Button btnChallengeFacebook = (Button) findViewById(R.id.btnChallengeFacebook);
		Button btnChallengeRandom = (Button) findViewById(R.id.btnChallengeRandom);
		
		if(!User.isFacebookSession(this)) {
			btnChallengeFacebook.setVisibility(View.GONE);
		}

		btnChallengeFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context,
						FriendsFacebookActivity.class);
				context.startActivity(intent);
			}
		});
		
		btnChallengeRandom.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
			}
		});
	}
}

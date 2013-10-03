package com.happymeteo;

import com.happymeteo.models.SessionCache;

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

		Button btnChallengeNew = (Button) findViewById(R.id.btnChallengeNew);
		Button btnChallengeDone = (Button) findViewById(R.id.btnChallengeDone);
		
		if(!SessionCache.isFacebookSession(this)) {
			btnChallengeNew.setEnabled(false);
		}

		btnChallengeNew.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context,
						FriendsFacebookActivity.class);
				context.startActivity(intent);
			}
		});
	}
}

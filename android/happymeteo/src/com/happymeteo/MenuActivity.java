package com.happymeteo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_menu);
		super.onCreate(savedInstanceState);
		
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		btnBeginQuestions.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, QuestionActivity.class);
				context.startActivity(intent);
			}
		});
		
		Button btnChallengeTry_request = (Button) findViewById(R.id.btnChallengeTry_request);
		btnChallengeTry_request.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("collapse_key", "request_challenge");
				extras.putString("challenge", "{'challenge_id': '5383844584751104', 'user_id_a': '6221964502892544', 'user_id_b': '4827508880965632'}");
				GCMIntentService.generateNotification(view.getContext(), extras);
			}
		});
		
		Button btnChallengeTry_turn1_true = (Button) findViewById(R.id.btnChallengeTry_turn1_true);
		btnChallengeTry_turn1_true.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("collapse_key", "do_not_collapse");
				extras.putString("appy_key", "accepted_challenge_turn1_true");
				extras.putString("challenge", "{'challenge_id': '4669728962379776', 'user_id_a': '6221964502892544', 'user_id_b': '4827508880965632'}");
				extras.putString("turn", "1");
				GCMIntentService.generateNotification(view.getContext(), extras);
			}
		});
		
		Button btnChallengeTry_turn1_false = (Button) findViewById(R.id.btnChallengeTry_turn1_false);
		btnChallengeTry_turn1_false.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("collapse_key", "do_not_collapse");
				extras.putString("appy_key", "accepted_challenge_turn1_false");
				GCMIntentService.generateNotification(view.getContext(), extras);
			}
		});
		
		Button btnChallengeTry_turn2 = (Button) findViewById(R.id.btnChallengeTry_turn2);
		btnChallengeTry_turn2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("challenge", "{'challenge_id': '6361714148769792', 'user_id_a': '5288891447771136', 'user_id_b': '5904755398279168'}");
				extras.putString("collapse_key", "do_not_collapse");
				extras.putString("appy_key", "accepted_challenge_turn2");
				extras.putString("score", "10");
				extras.putString("turn", "2");
				GCMIntentService.generateNotification(view.getContext(), extras);
			}
		});
		
		Button btnChallengeTry_turn3 = (Button) findViewById(R.id.btnChallengeTry_turn3);
		btnChallengeTry_turn3.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Bundle extras = new Bundle();
				extras.putString("collapse_key", "do_not_collapse");
				extras.putString("appy_key", "accepted_challenge_turn3");
				extras.putString("score", "10");
				GCMIntentService.generateNotification(view.getContext(), extras);
			}
		});
	}
}

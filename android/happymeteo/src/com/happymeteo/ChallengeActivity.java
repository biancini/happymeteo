package com.happymeteo;

import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChallengeActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_challenge);
		super.onCreate(savedInstanceState);

		Button btnChallengeNew = (Button) findViewById(R.id.btnChallengeNew);
		
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
		
		ServerUtilities.getChallenges(this, this, SessionCache.getUser_id(this));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) {
			return;
		}
		
		Log.i(Const.TAG, "result: "+result);
	}
}

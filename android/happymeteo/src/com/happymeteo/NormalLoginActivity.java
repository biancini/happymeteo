package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.happymeteo.models.User;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class NormalLoginActivity extends AppyMeteoNotLoggedActivity implements onPostExecuteListener {
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_normal_login);
		super.onCreate(savedInstanceState);

		final EditText normal_login_email = (EditText) findViewById(R.id.normal_login_email);
		final EditText normal_login_password = (EditText) findViewById(R.id.normal_login_password);
		
		this.activity = this;
		this.onPostExecuteListener = this;

		Button btnGoNormalLogin = (Button) findViewById(R.id.btnGoNormalLogin);
		btnGoNormalLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String email = normal_login_email.getText().toString();
				String password = "";
				try {
					password = SHA1.hexdigest(normal_login_password.getText().toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Log.i(Const.TAG, "email: " + email);
				Log.i(Const.TAG, "password: " + password);
				
				ServerUtilities.normalLogin(onPostExecuteListener, activity, email, password);
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			User user = new User(jsonObject);
			
			if(user != null) {
				HappyMeteoApplication.i().setCurrentUser(user);
				invokeActivity(HappyMeteoActivity.class);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

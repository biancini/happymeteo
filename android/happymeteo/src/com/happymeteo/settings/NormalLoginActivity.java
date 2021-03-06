package com.happymeteo.settings;

import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.happymeteo.NotLoggedActivity;
import com.happymeteo.R;
import com.happymeteo.meteo.MeteoActivity;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;

public class NormalLoginActivity extends NotLoggedActivity implements OnPostExecuteListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_normal_login);
		super.onCreate(savedInstanceState);

		final EditText normal_login_email = (EditText) findViewById(R.id.normal_login_email);
		final EditText normal_login_password = (EditText) findViewById(R.id.normal_login_password);
		Button btnLostPassword = (Button) findViewById(R.id.btnLostPassword);

		final Form mForm = new Form();
		mForm.addField(Field.using(normal_login_email).validate(NotEmpty.build(this)));
		mForm.addField(Field.using(normal_login_password).validate(NotEmpty.build(this)));

		Button btnGoNormalLogin = (Button) findViewById(R.id.btnGoNormalLogin);
		btnGoNormalLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mForm.isValid()) {
					String email = normal_login_email.getText().toString();
					String password = "";
					
					try {
						password = SHA1.hexdigest(Const.PASSWORD_SECRET_KEY, normal_login_password.getText().toString());
					} catch (Exception e) {
						Log.e(Const.TAG, e.getMessage(), e);
					}

					ServerUtilities.normalLogin(NormalLoginActivity.this, email, password);
				}
			}
		});

		btnLostPassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final EditText input = new EditText(v.getContext());

				new AlertDialog.Builder(NormalLoginActivity.this)
					.setTitle(getApplicationContext().getString(com.happymeteo.R.string.empty))
					.setMessage(getApplicationContext().getString(com.happymeteo.R.string.lost_password))
					.setView(input)
					.setPositiveButton(R.string.next,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									String email = input.getText().toString();
									ServerUtilities.lostPassword(NormalLoginActivity.this, email);
									Toast.makeText(getApplicationContext(), "Mail di recupero password inviata.", Toast.LENGTH_SHORT).show();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// Do nothing
								}
							}).show();
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			SessionCache.initialize(this, jsonObject);
			invokeActivity(MeteoActivity.class);
			return;
		} catch (JSONException e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

}

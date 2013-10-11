package com.happymeteo;

import org.json.JSONException;
import org.json.JSONObject;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class NormalLoginActivity extends AppyMeteoNotLoggedActivity implements
		onPostExecuteListener {
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_normal_login);
		super.onCreate(savedInstanceState);

		final EditText normal_login_email = (EditText) findViewById(R.id.normal_login_email);
		final EditText normal_login_password = (EditText) findViewById(R.id.normal_login_password);
		Button btnLostPassword = (Button) findViewById(R.id.btnLostPassword);

		final Form mForm = new Form();
		mForm.addField(Field.using(normal_login_email).validate(
				NotEmpty.build(this)));
		mForm.addField(Field.using(normal_login_password).validate(
				NotEmpty.build(this)));

		this.activity = this;
		this.onPostExecuteListener = this;

		Button btnGoNormalLogin = (Button) findViewById(R.id.btnGoNormalLogin);
		btnGoNormalLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mForm.isValid()) {
					String email = normal_login_email.getText().toString();
					String password = "";
					try {
						password = SHA1.hexdigest(Const.PASSWORD_SECRET_KEY,
								normal_login_password.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}

					ServerUtilities.normalLogin(onPostExecuteListener,
							activity, email, password);
				}
			}
		});

		btnLostPassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(activity)
						.setTitle(
								getApplicationContext().getString(
										com.happymeteo.R.string.empty))
						.setMessage(
								getApplicationContext().getString(
										com.happymeteo.R.string.are_you_sure))
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										ServerUtilities.lostPassword(
												onPostExecuteListener,
												activity, SessionCache
														.getUser_id(activity));
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			SessionCache.initialize(this, jsonObject);
			invokeActivity(HappyMeteoActivity.class);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}

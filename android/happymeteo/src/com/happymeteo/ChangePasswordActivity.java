package com.happymeteo;

import ua.org.zasadnyy.zvalidations.Field;
import ua.org.zasadnyy.zvalidations.Form;
import ua.org.zasadnyy.zvalidations.validations.IsPassword;
import ua.org.zasadnyy.zvalidations.validations.NotEmpty;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.happymeteo.captcha.Captcha;
import com.happymeteo.captcha.TextCaptcha;
import com.happymeteo.captcha.TextCaptcha.TextOptions;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.SHA1;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class ChangePasswordActivity extends AppyMeteoLoggedActivity implements onPostExecuteListener {
	
	private ImageView changePassword_imageCaptcha;
	private Captcha captcha;
	private int captchaWidth;
	private int captchaHeight;
	private AppyMeteoNotLoggedActivity activity;
	private onPostExecuteListener onPostExecuteListener;
	
	private void newCapthca() {
		
		captcha = new TextCaptcha(captchaWidth, captchaHeight, 5, TextOptions.NUMBERS_AND_LETTERS);
        changePassword_imageCaptcha.setImageBitmap(captcha.getImage());
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_change_password);
        super.onCreate(savedInstanceState);
        
        this.activity = this;
		this.onPostExecuteListener = this;
        
        changePassword_imageCaptcha = (ImageView) findViewById(R.id.changePassword_imageCaptcha);
        final EditText changePassword_textCaptcha = (EditText) findViewById(R.id.changePassword_textCaptcha);
        final EditText changePassword_old_password = (EditText) findViewById(R.id.changePassword_old_password);
        final EditText changePassword_password = (EditText) findViewById(R.id.changePassword_password);
        final EditText changePassword_confirm_password = (EditText) findViewById(R.id.changePassword_confirm_password);
        Button btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        Button btnRenewCaptcha = (Button) findViewById(R.id.btnRenewCaptcha);
        
        captchaWidth = changePassword_textCaptcha.getWidth();
        captchaHeight = changePassword_textCaptcha.getHeight();
        newCapthca();
        
        final Form mForm = new Form();
        mForm.addField(Field.using(changePassword_textCaptcha).validate(NotEmpty.build(this)));
        mForm.addField(Field.using(changePassword_old_password).validate(NotEmpty.build(this)));
	    mForm.addField(Field.using(changePassword_password).validate(NotEmpty.build(this)).validate(IsPassword.build(this)));
	    mForm.addField(Field.using(changePassword_confirm_password).validate(NotEmpty.build(this)));
        
        btnChangePassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Log.i(Const.TAG, "captcha.checkAnswer: "+captcha.checkAnswer(changePassword_textCaptcha.getText().toString()));
				
				if(mForm.isValid()) {
					if(captcha.checkAnswer(changePassword_textCaptcha.getText().toString())) {
						if(changePassword_password.getText().toString().equals(changePassword_confirm_password.getText().toString())) {
							String newPassword = "";
							
							try {
								newPassword = SHA1.hexdigest(Const.PASSWORD_SECRET_KEY, changePassword_password.getText().toString().toString());
							} catch (Exception e) {
								e.printStackTrace();
								newPassword = "";
							}	
							
							String oldPassword = "";
							
							try {
								oldPassword = SHA1.hexdigest(Const.PASSWORD_SECRET_KEY, changePassword_old_password.getText().toString());
							} catch (Exception e) {
								e.printStackTrace();
								oldPassword = "";
							}
							
							ServerUtilities.changePassword(onPostExecuteListener, activity, SessionCache.getUser_id(activity),
									newPassword, oldPassword);
						} else {
							changePassword_confirm_password.setError(getApplicationContext().getString(R.string.error_password));
						}
					} else {
						changePassword_textCaptcha.setError(getApplicationContext().getString(R.string.error_captcha));
					}
					
				}
			}
		});
        
        btnRenewCaptcha.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				newCapthca();
			}
		});
    }

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) {
			return;
		}
		
		finish();
	}
}
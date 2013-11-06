package com.happymeteo;

import org.jraf.android.backport.switchwidget.Switch;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.happymeteo.models.SessionCache;
import com.happymeteo.models.User;
import com.happymeteo.utils.AlertDialogManager;
import com.happymeteo.utils.Const;
import com.happymeteo.widget.AppyMeteoSeekBar;
import com.happymeteo.widget.OnAppyMeteoSeekBarChangeListener;

public abstract class QuestionImpulseActivity extends ImpulseActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	protected LinearLayout linearLayout = null;
	protected JSONObject questions = null;
	
	protected LocationClient mLocationClient;
	protected Location mCurrentLocation;
	
	/*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLocationClient = new LocationClient(this, this, this);
	}
	
	@Override
	public void onStart() {
        super.onStart();
        
        mLocationClient.connect();
    }
	
	@Override
	public void onStop() {
        super.onStop();
        
        mLocationClient.disconnect();
    }

	protected void writeQuestionText(String questionText) {
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(10, 10, 10, 0);
		llp.gravity = Gravity.CENTER;

		int gender = SessionCache.getGender(getApplicationContext());
		if (gender == User.GENDER_MALE)
			questionText = questionText.replaceAll("\\[o/a\\]", "o")
					.replaceAll("\\[a/o\\]", "o");
		else
			questionText = questionText.replaceAll("\\[o/a\\]", "a")
					.replaceAll("\\[a/o\\]", "a");

		TextView textView = new TextView(getApplicationContext());
		textView.setLayoutParams(llp);
		textView.setPadding(10, 10, 10, 0);
		textView.setText(questionText);
		textView.setBackgroundColor(getResources().getColor(R.color.white));
		textView.setTextColor(getResources().getColor(R.color.black));
		// textView.setBackgroundResource(R.drawable.fascia);
		textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		textView.setTextSize(23.0f);

		try {
			Typeface billabong = Typeface.createFromAsset(getAssets(),
					"billabong.ttf");
			textView.setTypeface(billabong);
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
		linearLayout.addView(textView);

		LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// llp2.setMargins(10, 0, 10, 10);
		llp2.gravity = Gravity.CENTER;
		ImageView fascetta = new ImageView(getApplicationContext());
		fascetta.setLayoutParams(llp2);
		fascetta.setPadding(10, 0, 10, 10);
		fascetta.setImageDrawable(getResources().getDrawable(R.drawable.fascia));
		linearLayout.addView(fascetta);
	}

	protected void writeQuestionAnswerArea(final int type,
			final String id_question, final String textYes, final String textNo)
			throws JSONException {
		if (type == 1) {
			drawOneToTenAnswer(id_question);
			questions.put(id_question, "1");
		} else {
			drawYesNoAnswer(id_question, textYes, textNo);
			questions.put(id_question, "0");
		}
	}

	protected void drawYesNoAnswer(final String id_question,
			final String textYes, final String textNo) {
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// llp.setMargins(10, 0, 10, 30);

		LinearLayout linearLayout1 = new LinearLayout(getApplicationContext());
		linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout1.setGravity(Gravity.CENTER);

		TextView textYesView = new TextView(this);
		textYesView.setLayoutParams(llp);
		textYesView.setPadding(10, 0, 10, 30);
		textYesView.setText(textYes);
		linearLayout1.addView(textYesView);

		final Switch switchButton = new Switch(this);
		switchButton.setLayoutParams(llp);
		switchButton.setPadding(10, 0, 10, 30);
		switchButton.setChecked(true);
		switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				try {
					questions.put(id_question, isChecked ? "0" : "1");
				} catch (JSONException e) {
					Log.e(Const.TAG, e.getMessage(), e);
				}
			}
		});

		linearLayout1.addView(switchButton);

		TextView textNoView = new TextView(this);
		textNoView.setLayoutParams(llp);
		textNoView.setPadding(10, 0, 10, 30);
		textNoView.setText(textNo);
		linearLayout1.addView(textNoView);

		linearLayout.addView(linearLayout1);
	}

	protected void drawOneToTenAnswer(final String id_question) {
		LinearLayout.LayoutParams llpImg = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		llpImg.weight = 10;
		llpImg.gravity = Gravity.CENTER_VERTICAL;

		LinearLayout linearLayout1 = new LinearLayout(this);
		linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout1.setPadding(10, 0, 10, 10);

		ImageView imageView1 = new ImageView(this);
		imageView1.setImageResource(R.drawable.triste);
		imageView1.setLayoutParams(llpImg);
		linearLayout1.addView(imageView1);

		LinearLayout.LayoutParams llp_seekBar = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		llp_seekBar.weight = 80;

		final AppyMeteoSeekBar appyMeteoSeekBar = new AppyMeteoSeekBar(this);
		appyMeteoSeekBar.setMax(90);
		appyMeteoSeekBar.setProgress(0);
		appyMeteoSeekBar.setLayoutParams(llp_seekBar);

		final TextView tvText = new TextView(this);
		tvText.setText("1\u00B0");
		tvText.setBackgroundResource(R.drawable.baloon);
		tvText.setGravity(Gravity.CENTER);
		tvText.setTextColor(getResources().getColor(R.color.white));
		tvText.setTextSize(15.0f);
		LinearLayout.LayoutParams llpBaloon = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llpBaloon.leftMargin = appyMeteoSeekBar.getProgressPosX();
		tvText.setLayoutParams(llpBaloon);
		linearLayout.addView(tvText);

		appyMeteoSeekBar
				.setOnAppyMeteoSeekBarChangeListener(new OnAppyMeteoSeekBarChangeListener() {
					@Override
					public void onProgressPosXChanged(AppyMeteoSeekBar seekBar,
							int progress, int progressPosX) {
						String value = String.valueOf((progress / 10) + 1);
						try {
							questions.put(id_question, value);
						} catch (JSONException e) {
							Log.e(Const.TAG, e.getMessage(), e);
						}
						tvText.setText(value + "\u00B0");

						LinearLayout.LayoutParams llpBaloon = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						llpBaloon.leftMargin = progressPosX;
						tvText.setLayoutParams(llpBaloon);
					}
				});

		linearLayout1.addView(appyMeteoSeekBar);

		ImageView imageView2 = new ImageView(this);
		imageView2.setImageResource(R.drawable.felice);
		imageView2.setLayoutParams(llpImg);
		linearLayout1.addView(imageView2);

		linearLayout.addView(linearLayout1);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            AlertDialogManager.showError(this, connectionResult.toString());
        }
	}

	@Override
	public void onConnected(Bundle arg0) {
		// Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
	}

}

package com.happymeteo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class QuestionBeginActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_begin);

		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutBeginQuestion);
		final Button btnBeginQuestions = (Button) findViewById(R.id.btnBeginQuestions);
		btnBeginQuestions.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				JSONArray jsonArray = ServerUtilities.getQuestions();

				Log.i(Const.TAG, "jsonArray: " + jsonArray);

				if (jsonArray != null) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject;
						try {
							jsonObject = jsonArray.getJSONObject(i);
							Log.i(Const.TAG, jsonObject.toString());

							LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							llp.setMargins(10, 10, 10, 10);

							TextView tv = new TextView(getApplicationContext());
							tv.setText(jsonObject.getString("question"));
							tv.setLayoutParams(llp);
							linearLayout.addView(tv);

							if (jsonObject.getInt("type") == 1) {
								LinearLayout.LayoutParams llp_seekBar = new LinearLayout.LayoutParams(
										LayoutParams.FILL_PARENT,
										LayoutParams.WRAP_CONTENT);
								llp_seekBar.setMargins(10, 10, 10, 10);
								
								SeekBar seekBar = new SeekBar(getApplicationContext());
								seekBar.setThumb(getResources().getDrawable(R.drawable.scrubber_control_selector_holo_light));
								seekBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_horizontal_holo_light));
								seekBar.setMax(100);
								seekBar.setProgress(10);
								seekBar.setLayoutParams(llp_seekBar);
								seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
									
									@Override
									public void onStopTrackingTouch(SeekBar seekBar) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onStartTrackingTouch(SeekBar seekBar) {
										// TODO Auto-generated method stub
										
									}
									
									@Override
									public void onProgressChanged(SeekBar seekBar, int progress,
											boolean fromUser) {
										Log.i(Const.TAG, "progress: " + Integer.toString(progress)+"%");
										Log.i(Const.TAG, "number: " + Integer.toString(progress/10));
									}
								});
								
								linearLayout.addView(seekBar);
							} else {
								ToggleButton toggleButton = new ToggleButton(getApplicationContext());
								toggleButton.setLayoutParams(llp);
								toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
								    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								        if (isChecked) {
								            // The toggle is enabled
								        	Log.i(Const.TAG, "Si!");
								        } else {
								            // The toggle is disabled
								        	Log.i(Const.TAG, "No!");
								        }
								    }
								});
								
								linearLayout.addView(toggleButton);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
				
				btnBeginQuestions.setEnabled(false);
			}
		});
	}

}

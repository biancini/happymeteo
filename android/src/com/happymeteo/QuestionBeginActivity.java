package com.happymeteo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class QuestionBeginActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_begin);
		
		Button btnBeginQuestions = (Button) findViewById(R.id.btnBeginQuestions);
		btnBeginQuestions.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Toast.makeText(view.getContext(), "Begin questions", Toast.LENGTH_LONG).show();
			}
		});
	}

}

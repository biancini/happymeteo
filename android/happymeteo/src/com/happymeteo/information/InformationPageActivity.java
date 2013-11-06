package com.happymeteo.information;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity extends LoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page);
		super.onCreate(savedInstanceState);
		
		final TextView information_page1 = (TextView) findViewById(R.id.information_page1);
		information_page1.setText(Html.fromHtml(getString(R.string.information1)));
		
		final TextView information_page2 = (TextView) findViewById(R.id.information_page2);
		information_page2.setText(Html.fromHtml(getString(R.string.information2)));
		
		final TextView information_page3 = (TextView) findViewById(R.id.information_page3);
		information_page3.setText(Html.fromHtml(getString(R.string.information3)));
		
		TextView information_pagesub1 = (TextView) findViewById(R.id.information_pagesub1);
		information_pagesub1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				//view.setVisibility(View.GONE);
				if (((TextView) view).getText().equals(getResources().getText(R.string.continue_to_read))) {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page1, "maxLines", 50);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.close_read));
				} else {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page1, "maxLines", 4);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.continue_to_read));
				}
			}
		});
		
		TextView information_pagesub2 = (TextView) findViewById(R.id.information_pagesub2);
		information_pagesub2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//view.setVisibility(View.GONE);
				if (((TextView) view).getText().equals(getResources().getText(R.string.continue_to_read))) {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page2, "maxLines", 50);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.close_read));
				} else {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page2, "maxLines", 4);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.continue_to_read));
				}
			}
		});
		
		TextView information_pagesub3 = (TextView) findViewById(R.id.information_pagesub3);
		information_pagesub3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//view.setVisibility(View.GONE);
				if (((TextView) view).getText().equals(getResources().getText(R.string.continue_to_read))) {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page3, "maxLines", 50);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.close_read));
				} else {
					ObjectAnimator animation = ObjectAnimator.ofInt(information_page3, "maxLines", 4);
					animation.setDuration(1000);
					animation.start();
					
					((TextView) view).setText(getResources().getText(R.string.continue_to_read));
				}
			}
		});
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
	
	@Override
	public void OnFacebookExecute(Session session, SessionState state) {
		// Do nothing
	}
}
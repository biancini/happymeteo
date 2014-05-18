package com.happymeteo.information;

import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
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
		
		final TextView information_page4 = (TextView) findViewById(R.id.information_page4);
		information_page4.setText(Html.fromHtml(getString(R.string.information4)));
		
		final LinearLayout information_page4legend = (LinearLayout) findViewById(R.id.legendLayout); 
		information_page4legend.setVisibility(LinearLayout.GONE);
		
		final TextView legendIconaTitle = (TextView) findViewById(R.id.legendIconaTitle);
		legendIconaTitle.setPaintFlags(legendIconaTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		final TextView legendColoreTitle = (TextView) findViewById(R.id.legendColoreTitle);
		legendColoreTitle.setPaintFlags(legendColoreTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		final TextView legendValoreTitle = (TextView) findViewById(R.id.legendValoreTitle);
		legendValoreTitle.setPaintFlags(legendValoreTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		
		TextView information_pagesub1 = (TextView) findViewById(R.id.information_pagesub1);
		information_pagesub1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
				} else {
					setPersistentActivity(true);
					invokeActivity(InformationPageActivity1.class);
					setPersistentActivity(false);
				}
			}
		});
		
		TextView information_pagesub2 = (TextView) findViewById(R.id.information_pagesub2);
		information_pagesub2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
				} else {
					setPersistentActivity(true);
					invokeActivity(InformationPageActivity2.class);
					setPersistentActivity(false);
				}
			}
		});
		
		TextView information_pagesub3 = (TextView) findViewById(R.id.information_pagesub3);
		information_pagesub3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
				} else {
					setPersistentActivity(true);
					invokeActivity(InformationPageActivity3.class);
					setPersistentActivity(false);
				}
			}
		});
		
		TextView information_pagesub4 = (TextView) findViewById(R.id.information_pagesub4);
		information_pagesub4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					if (((TextView) view).getText().equals(getResources().getText(R.string.continue_to_read))) {
						Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
						animation.setDuration(1000);
						animation.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
								information_page4legend.setVisibility(LinearLayout.VISIBLE);
							}
							@Override
							public void onAnimationRepeat(Animation animation) { }
							@Override
							public void onAnimationEnd(Animation animation) { }
						});
						information_page4legend.startAnimation(animation);
						
						((TextView) view).setText(getResources().getText(R.string.close_read));
					} else {
						Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
						animation.setDuration(1000);
						animation.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) { }
							@Override
							public void onAnimationRepeat(Animation animation) { }
							@Override
							public void onAnimationEnd(Animation animation) {
								information_page4legend.setVisibility(LinearLayout.GONE);
							}
						});
						information_page4legend.startAnimation(animation);
						
						((TextView) view).setText(getResources().getText(R.string.continue_to_read));
					}
				} else {
					setPersistentActivity(true);
					invokeActivity(InformationPageActivity4.class);
					setPersistentActivity(false);
				}
			}
		});
	}
}
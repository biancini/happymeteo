package com.happymeteo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.FeedDialogBuilder;
import com.happymeteo.graph.DayBarGraphView;
import com.happymeteo.graph.GraphView.GraphViewData;
import com.happymeteo.graph.GraphViewSeries;
import com.happymeteo.graph.GraphViewSeries.GraphViewSeriesStyle;
import com.happymeteo.graph.GraphViewStyle;
import com.happymeteo.graph.MonthBarGraphView;
import com.happymeteo.models.SessionCache;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.onPostExecuteListener;

public class HappyMeteoActivity extends AppyMeteoLoggedActivity implements
		onPostExecuteListener {
	
	private TextView welcomeToday;
	private GestureDetector gestureDetector;

	class MyGestureDetector extends SimpleOnGestureListener {
		private ViewFlipper flipper;
		private Context context;

		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		public MyGestureDetector(Context context, ViewFlipper flipper) {
			this.flipper = flipper;
			this.context = context;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;
			
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& flipper.getDisplayedChild() < flipper.getChildCount() - 1) {
				flipper.setInAnimation(context, R.anim.in_from_right);
				flipper.setOutAnimation(context, R.anim.out_to_left);
				welcomeToday.setText(SessionCache.getFirst_name(context).toLowerCase()
						+ "_DIARIO");
				flipper.showNext();
				return true;
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
					&& flipper.getDisplayedChild() > 0) {
				flipper.setInAnimation(context, R.anim.in_from_left);
				flipper.setOutAnimation(context, R.anim.out_to_right);
				flipper.showPrevious();
				if(flipper.getDisplayedChild() == 0) {
					welcomeToday.setText(SessionCache.getFirst_name(context).toLowerCase()
							+ "_OGGI");
				}
				return true;
			}
			return false;
		}
	}

	private int[] getColorByToday(int today) {
		int colors[] = { 0, 0 };
		switch (today) {
		case 1:
			colors[0] = 0xff7bccff;
			colors[1] = 0xff2ea6ff;
			break;
		case 2:
			colors[0] = 0xff2ea6ff;
			colors[1] = 0xff0071bc;
			break;
		case 3:
			colors[0] = 0xff0071bc;
			colors[1] = 0xff4a4998;
			break;
		case 4:
			colors[0] = 0xff93278f;
			colors[1] = 0xff4a4998;
			break;
		case 5:
			colors[0] = 0xffed1e79;
			colors[1] = 0xffae3771;
			break;
		case 6:
			colors[0] = 0xffff0000;
			colors[1] = 0xfffd3771;
			break;
		case 7:
			colors[0] = 0xfff1700d;
			colors[1] = 0xfffd3700;
			break;
		case 8:
			colors[0] = 0xfff7931e;
			colors[1] = 0xfffd6c00;
			break;
		case 9:
			colors[0] = 0xfff9c33d;
			colors[1] = 0xfffd9200;
			break;
		case 10:
			colors[0] = 0xffffe400;
			colors[1] = 0xfffdc800;
			break;
		}

		return colors;
	}

	private int getWhiteIcon(int day) {
		switch (day) {
		case 1:
			return R.drawable.white_1happy;
		case 2:
			return R.drawable.white_2happy;
		case 3:
			return R.drawable.white_3happy;
		case 4:
			return R.drawable.white_4happy;
		case 5:
			return R.drawable.white_5happy;
		case 6:
			return R.drawable.white_6happy;
		case 7:
			return R.drawable.white_7happy;
		case 8:
			return R.drawable.white_8happy;
		case 9:
			return R.drawable.white_9happy;
		case 10:
			return R.drawable.white_10happy;
		}

		return R.drawable.white_1happy;
	}

	private int getGrayIcon(int day) {
		switch (day) {
		case 1:
			return R.drawable.gray_1happy;
		case 2:
			return R.drawable.gray_2happy;
		case 3:
			return R.drawable.gray_3happy;
		case 4:
			return R.drawable.gray_4happy;
		case 5:
			return R.drawable.gray_5happy;
		case 6:
			return R.drawable.gray_6happy;
		case 7:
			return R.drawable.gray_7happy;
		case 8:
			return R.drawable.gray_8happy;
		case 9:
			return R.drawable.gray_9happy;
		case 10:
			return R.drawable.gray_10happy;
		}

		return R.drawable.white_1happy;
	}

	private void setupView() {
		int today_int = SessionCache.getToday(this);
		int yesterday_int = SessionCache.getYesterday(this);
		int tomorrow_int = SessionCache.getTomorrow(this);

		TextView today_text = (TextView) findViewById(R.id.today_text);
		today_text.setText(String.valueOf(today_int));

		TextView yesterday_text = (TextView) findViewById(R.id.yesterday_text);
		yesterday_text.setText(String.valueOf(yesterday_int));

		TextView tomorrow_text = (TextView) findViewById(R.id.tomorrow_text);
		tomorrow_text.setText(String.valueOf(tomorrow_int));

		ImageView today_pic = (ImageView) findViewById(R.id.today_pic);
		today_pic.setImageResource(getWhiteIcon(today_int));
		ImageView yesterday_pic = (ImageView) findViewById(R.id.yesterday_pic);
		yesterday_pic.setImageResource(getGrayIcon(yesterday_int));
		ImageView tomorrow_pic = (ImageView) findViewById(R.id.tomorrow_pic);
		tomorrow_pic.setImageResource(getGrayIcon(tomorrow_int));

		RelativeLayout relativeLayoutMeteoUpToday = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUpToday);
		GradientDrawable gradientDrawable = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM,
				getColorByToday(today_int));
		relativeLayoutMeteoUpToday.setBackgroundDrawable(gradientDrawable);

		try {
			Typeface helveticaneueltstd_ultlt_webfont = Typeface
					.createFromAsset(getAssets(),
							"helveticaneueltstd-ultlt-webfont.ttf");
			today_text.setTypeface(helveticaneueltstd_ultlt_webfont);
			yesterday_text.setTypeface(helveticaneueltstd_ultlt_webfont);
			tomorrow_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		} catch (Exception e) {
		}

		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.profile_picture);
		ImageView facebook = (ImageView) findViewById(R.id.facebook);

		if (SessionCache.isFacebookSession(this)) {
			userImage.setProfileId(SessionCache.getFacebook_id(this));
			userImage.setCropped(true);
			facebook.setVisibility(View.VISIBLE);
		} else {
			userImage.setProfileId(null);
			facebook.setVisibility(View.GONE);
		} 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);

		/* Initialize PushNotificationsService */
		PushNotificationsService.register(getApplicationContext());

		welcomeToday = (TextView) findViewById(R.id.welcomeToday);
		welcomeToday.setText(SessionCache.getFirst_name(this).toLowerCase()
				+ "_OGGI");

		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperUp);
		gestureDetector = new GestureDetector(new MyGestureDetector(this,
				viewFlipper));

		ImageView mail = (ImageView) findViewById(R.id.mail);
		mail.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				i.putExtra(Intent.EXTRA_SUBJECT, "appymeteo");
				i.putExtra(Intent.EXTRA_TEXT, "Ciao, io mi sto divertendo con appymeteo, vuoi farlo anche tu?");
				try {
					startActivity(Intent.createChooser(i, view.getContext()
							.getString(R.string.mail)));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(HappyMeteoActivity.this,
							"Non ci sono mail client installati.",
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});

		ImageView facebook = (ImageView) findViewById(R.id.facebook);
		facebook.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				FeedDialogBuilder feedDialogBuilder = new FeedDialogBuilder(view.getContext(), Session.getActiveSession());
				feedDialogBuilder.setDescription("Ho appena misurato la mia felicit� con appymeteo. LINK");
				feedDialogBuilder.setPicture(Const.BASE_URL + "/img/facebook_invita.png");
				WebDialog webDialog = feedDialogBuilder.build();
				webDialog.show();
				return false;
			}
		});

		setupView();
		
		ServerUtilities.getAppynessByDay(this, this,
				SessionCache.getUser_id(this));
		
		ServerUtilities.getAppynessByMonth(this, this,
				SessionCache.getUser_id(this));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent.getExtras() != null) {
			boolean challengeScoreActivity = intent.getExtras().getBoolean(
					"ChallengeScoreActivity", false);

			if (challengeScoreActivity)
				invokeActivity(ChallengeScoreActivity.class, intent.getExtras());
		}

		setupView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setupView();
	}

	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		if (gestureDetector.onTouchEvent(touchevent)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if(exception != null) {
			return;
		}
		
		
		
		try
		{
			JSONObject jsonObject = new JSONObject(result);
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int month = cal.get(Calendar.MONTH);
			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			
			String[] months = new String[] {
					getApplicationContext().getString(R.string.january),
					getApplicationContext().getString(R.string.february),
					getApplicationContext().getString(R.string.march),
					getApplicationContext().getString(R.string.april),
					getApplicationContext().getString(R.string.may),
					getApplicationContext().getString(R.string.june),
					getApplicationContext().getString(R.string.july),
					getApplicationContext().getString(R.string.august),
					getApplicationContext().getString(R.string.september),
					getApplicationContext().getString(R.string.october),
					getApplicationContext().getString(R.string.november),
					getApplicationContext().getString(R.string.december)};
			
			GraphViewStyle graphViewStyle = new GraphViewStyle(0xff000000, 0xffffffff, 0xffffffff);
			
			switch(id) {
			case Const.GET_APPINESS_BY_DAY_ID:
				RelativeLayout relativeLayoutMeteoUpGraphByDay = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUpGraphByDay);
				GraphViewData[] viewDayData = new  GraphViewData[daysInMonth];
				boolean future = false;
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				for(int i=0; i<daysInMonth; i++) {
					String date = dateFormat.format(cal.getTime());
					double y = 1;
					if(future) {
						y = 0;
					} else {
						if(!jsonObject.isNull(date))
							y = jsonObject.getInt(date);
					}
					viewDayData[i] = new GraphViewData(i+1, y);
					if(cal.getTime().equals(today)) {
						future = true;
					}
					cal.add(Calendar.DATE, +1);
				}
				
			    // init example series data  
			    GraphViewSeries dayDataSeries = new GraphViewSeries(
			    	  new GraphViewSeriesStyle(getResources().getColor(R.color.yellow), 1),
			    	  viewDayData
			    );
			    
			    DayBarGraphView dayBarGraphView = new DayBarGraphView(this);
			    dayBarGraphView.setManualYAxisBounds(10, 0);
			    dayBarGraphView.addSeries(dayDataSeries); // data
			    dayBarGraphView.setHorizontalLabels(new String[] {months[month]});
				dayBarGraphView.setGraphViewStyle(graphViewStyle);
				relativeLayoutMeteoUpGraphByDay.addView(dayBarGraphView);
				break;
			case Const.GET_APPINESS_BY_MONTH_ID:
				RelativeLayout relativeLayoutMeteoUpGraphByMonth = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUpGraphByMonth);
				MonthBarGraphView monthBarGraphView = new MonthBarGraphView(this);
				monthBarGraphView.setManualYAxisBounds(10, 0);
				monthBarGraphView.setGraphViewStyle(graphViewStyle);
				monthBarGraphView.setHorizontalLabels(new String[] {months[month-1]});
				
				int len_months = 5;
				String[] horLabels = new String[len_months];
				
				GraphViewData[] viewMonthData = new  GraphViewData[len_months];
				for(int i=0; i<len_months; i++) {
					int id_month = (month-len_months+i)%12;
					double y = 10;
					if(!jsonObject.isNull(String.valueOf(id_month+1)))
						y = jsonObject.getInt(String.valueOf(id_month+1));
					viewMonthData[i] = new GraphViewData(i+1, y);
					horLabels[i] = months[id_month];
				}
				GraphViewSeries monthDataSeries = new GraphViewSeries(
				    	  new GraphViewSeriesStyle(getResources().getColor(R.color.yellow), 1),
				    	  viewMonthData
				    );
				monthBarGraphView.addSeries(monthDataSeries); // data
				monthBarGraphView.setHorizontalLabels(horLabels);
				relativeLayoutMeteoUpGraphByMonth.addView(monthBarGraphView);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

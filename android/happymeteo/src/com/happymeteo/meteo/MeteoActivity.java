package com.happymeteo.meteo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.FeedDialogBuilder;
import com.happymeteo.LoggedActivity;
import com.happymeteo.R;
import com.happymeteo.challenge.ChallengeScoreActivity;
import com.happymeteo.meteo.GraphView.GraphViewData;
import com.happymeteo.meteo.GraphViewSeries.GraphViewSeriesStyle;
import com.happymeteo.models.SessionCache;
import com.happymeteo.service.PushNotificationsService;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.OnPostExecuteListener;
import com.happymeteo.utils.OnSwipeExecuteListener;
import com.happymeteo.utils.ServerUtilities;
import com.happymeteo.utils.SwipeGestureDetector;

public class MeteoActivity extends LoggedActivity implements OnPostExecuteListener, OnSwipeExecuteListener {

	private TextView welcomeToday = null;
	private GestureDetector gestureDetector = null;

	private int[] getColorByToday(int today) {
		int[] colors_0 = {
				0xff7bccff, 0xff2ea6ff, 0xff0071bc, 0xff93278f, 0xffed1e79,
				0xffff0000, 0xfff1700d, 0xfff7931e, 0xfff9c33d, 0xffffe400 };
		int[] colors_1 = {
				0xff2ea6ff, 0xff0071bc, 0xff4a4998, 0xff4a4998, 0xffae3771,
				0xfffd3771, 0xfffd3700, 0xfffd6c00, 0xfffd9200, 0xfffdc800 };

		if (today < 1 || today > 10)
			return new int[] { colors_0[0], colors_1[0] };

		return new int[] { colors_0[today - 1], colors_1[today - 1] };
	}

	private int getWhiteIcon(int day) {
		if (day < 1 || day > 10) return R.drawable.white_1happy;

		int[] icons = { R.drawable.white_1happy, R.drawable.white_2happy,
				R.drawable.white_3happy, R.drawable.white_4happy,
				R.drawable.white_5happy, R.drawable.white_6happy,
				R.drawable.white_7happy, R.drawable.white_8happy,
				R.drawable.white_9happy, R.drawable.white_10happy };

		return icons[day - 1];
	}

	private int getGrayIcon(int day) {
		if (day < 1 || day > 10) return R.drawable.gray_1happy;

		int[] icons = { R.drawable.gray_1happy, R.drawable.gray_2happy,
				R.drawable.gray_3happy, R.drawable.gray_4happy,
				R.drawable.gray_5happy, R.drawable.gray_6happy,
				R.drawable.gray_7happy, R.drawable.gray_8happy,
				R.drawable.gray_9happy, R.drawable.gray_10happy };

		return icons[day - 1];
	}

	@SuppressWarnings("deprecation")
	protected void showViewBasedOnFacebookSession() {
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
			Typeface helveticaneueltstd_ultlt_webfont = Typeface.createFromAsset(getAssets(), "helveticaneueltstd-ultlt-webfont.ttf");
			today_text.setTypeface(helveticaneueltstd_ultlt_webfont);
			yesterday_text.setTypeface(helveticaneueltstd_ultlt_webfont);
			tomorrow_text.setTypeface(helveticaneueltstd_ultlt_webfont);
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_happy_meteo);
		super.onCreate(savedInstanceState);
		
		setPersistentActivity(true);

		/* Initialize PushNotificationsService */
		PushNotificationsService.register(getApplicationContext());

		welcomeToday = (TextView) findViewById(R.id.welcomeToday);
		welcomeToday.setText(SessionCache.getFirst_name(this).toLowerCase(Locale.getDefault()) + "_OGGI");

		ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperUp);
		gestureDetector = new GestureDetector(this, new SwipeGestureDetector(this, viewFlipper, this));

		ImageView mail = (ImageView) findViewById(R.id.mail);
		mail.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
				i.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text));

				try {
					startActivity(Intent.createChooser(i, getString(R.string.mail)));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(MeteoActivity.this, getString(R.string.missing_mailclient), Toast.LENGTH_SHORT).show();
				}

				return false;
			}
		});

		ImageView facebook = (ImageView) findViewById(R.id.facebook);
		facebook.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				FeedDialogBuilder feedDialogBuilder = new FeedDialogBuilder(view.getContext(), Session.getActiveSession());
				feedDialogBuilder.setDescription(getString(R.string.facebook_text));
				feedDialogBuilder.setPicture(Const.BASE_URL + "/img/facebook_invita.png");
				WebDialog webDialog = feedDialogBuilder.build();
				webDialog.show();
				return false;
			}
		});

		ServerUtilities.getAppynessByDay(this, SessionCache.getUser_id(this));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		if (intent.getExtras() != null) {
			boolean challengeScoreActivity = intent.getExtras().getBoolean("ChallengeScoreActivity", false);

			if (challengeScoreActivity) {
				invokeActivity(ChallengeScoreActivity.class, intent.getExtras());
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		
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
	public boolean onTouchEvent(MotionEvent touchevent) {
		return !(gestureDetector.onTouchEvent(touchevent));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		if (exception != null) return;

		try {
			JSONObject jsonObject = new JSONObject(result);
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			int month = cal.get(Calendar.MONTH);
			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			int[] months = new int[] { R.string.january, R.string.february,
					R.string.march, R.string.april, R.string.may,
					R.string.june, R.string.july, R.string.august,
					R.string.september, R.string.october, R.string.november,
					R.string.december };

			GraphViewStyle graphViewStyle = new GraphViewStyle(0xff000000, 0xffffffff, 0xffffffff);

			switch (id) {
			case Const.GET_APPINESS_BY_DAY_ID:
				RelativeLayout wait = (RelativeLayout) findViewById(R.id.waitGetAppinessByDay);
				wait.setVisibility(View.GONE);

				RelativeLayout relativeLayoutMeteoUpGraphByDay = (RelativeLayout) findViewById(R.id.relativeLayoutMeteoUpGraphByDay);

				int groupBy = 7;
				int groups = daysInMonth / groupBy
						+ (((daysInMonth % groupBy) == 0) ? 0 : 1);
				GraphViewData[] viewDayData = new GraphViewData[groups];
				for (int i = 0; i < groups; ++i) {
					viewDayData[i] = new GraphViewData(i + 1, 0.0f);
				}

				boolean future = false;
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

				int arrayIndex = 0;
				float curValue = 0.0f;
				int curElems = 0;
				for (int i = 0; i < daysInMonth; i++) {
					String date = dateFormat.format(cal.getTime());
					double y = (jsonObject.isNull(date)) ? 0.0 : jsonObject.getInt(date);

					if (!future) {
						curElems += 1;
						curValue += y;
					}

					if (i % groupBy == 0) {
						float val = (curElems > 0) ? curValue / curElems : 0.0f;
						viewDayData[arrayIndex].valueY = val;
						arrayIndex++;
						curValue = 0.0f;
						curElems = 0;
					}

					if (cal.getTime().equals(today)) future = true;
					cal.add(Calendar.DATE, 1);
				}

				// init example series data
				GraphViewSeries dayDataSeries = new GraphViewSeries(
						new GraphViewSeriesStyle(getResources().getColor(R.color.yellow), 1), viewDayData);

				GraphView dayBarGraphView = new GraphView(this);
				dayBarGraphView.setManualYAxisBounds(10, 0);
				dayBarGraphView.addSeries(dayDataSeries); // data
				dayBarGraphView.setHorizontalLabels(new String[] { getApplicationContext().getString(months[month]) });
				dayBarGraphView.setGraphViewStyle(graphViewStyle);
				relativeLayoutMeteoUpGraphByDay.addView(dayBarGraphView);
				break;
			}
		} catch (Exception e) {
			Log.e(Const.TAG, e.getMessage(), e);
		}
	}

	@Override
	public void OnSwipeExecute(int child) {
		if(child == 0) {
			welcomeToday.setText(SessionCache.getFirst_name(this).toLowerCase(Locale.getDefault()) + "_DIARIO");
		} else {
			welcomeToday.setText(SessionCache.getFirst_name(this).toLowerCase(Locale.getDefault()) + "_OGGI");
		}
	}
	
	@Override
	public void OnFacebookExecute(Session session, SessionState state) {
		// Do nothing
	}
}

package com.happymeteo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.facebook.Session;
import com.facebook.SessionState;
import com.happymeteo.models.SessionCache;
import com.happymeteo.utils.Const;

public class AppyMeteoLoggedActivity extends AppyMeteoNotLoggedActivity implements
		ISideNavigationCallback {
	
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private SideNavigationView sideNavigationView;
	
	private void inShow() {
		Log.i(Const.TAG, "inShow: "+SessionCache.isFacebookSession(this));
		if(!SessionCache.isFacebookSession(this)) {
			Log.i(Const.TAG, "changeIcon");
			sideNavigationView.changeIcon(R.id.side_navigation_menu_item4, R.drawable.icona_sfidagrigio);
		} else {
			sideNavigationView.changeIcon(R.id.side_navigation_menu_item4, R.drawable.icona_sfida);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Action bar */
		getSupportActionBar().setIcon(R.drawable.icona_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		/* Home icon */
		ImageView icon = (ImageView) findViewById(android.R.id.home);
		if (icon != null) {
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon
					.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = 0;
			iconLp.leftMargin = iconLp.rightMargin = 20;
			icon.setLayoutParams(iconLp);
		}

		/* Side Navigation Menu */
		sideNavigationView = new SideNavigationView(getApplicationContext());
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.removeItem(R.id.side_navigation_menu_item5);
		sideNavigationView.setMenuClickCallback(this);
		sideNavigationView.setMode(Mode.LEFT);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		addContentView(sideNavigationView, layoutParams);
		
		if(SessionCache.isFacebookSession(this) && !Session.getActiveSession().isOpened()) {
			onFacebookConnect(statusCallback, false);
		}
		
		inShow();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		inShow();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		inShow();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.settings).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sideNavigationView.toggleMenu();
			break;
		case R.id.settings:
			invokeActivity(SettingsActivity.class);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onSideNavigationItemClick(int itemId) {
		switch (itemId) {
			case R.id.side_navigation_menu_item1:
				invokeActivity(InformationPageActivity.class);
				break;
	
			case R.id.side_navigation_menu_item2:
				invokeActivity(HappyMeteoActivity.class);
				break;
	
			case R.id.side_navigation_menu_item3:
				invokeActivity(HappyMapActivity.class);
				break;
	
			case R.id.side_navigation_menu_item4:
				if(SessionCache.isFacebookSession(this)) {
					invokeActivity(ChallengeActivity.class);
				}
				break;
	
			case R.id.side_navigation_menu_item5:
//				Bundle extras = new Bundle();
//				extras.putString("timestamp", "test");
//				invokeActivity(QuestionActivity.class, extras);
				Bundle extras = new Bundle();
				extras.putString("ioChallenge", "1.0");
				extras.putString("ioFacebookId", "757833642");
				extras.putString("ioName", "Simon");
				extras.putString("tuChallenge", "1.0");
				extras.putString("tuFacebookId", "500674896");
				extras.putString("tuName", "Andrea");
				invokeActivity(ChallengeScoreActivity.class, extras);
				break;
				
			case R.id.side_navigation_menu_item6:
				onClickLogout();
				break;
	
			default:
				return;
		}
	}

	@Override
	public void onBackPressed() {
		// hide menu if it shown
		if (sideNavigationView.isShown()) {
			sideNavigationView.hideMenu();
		} else {
			Log.i(Const.TAG, "invokeActivity: "+this.getClass()+" "+HappyMeteoActivity.class);
			if(!this.getClass().equals(HappyMeteoActivity.class))
				super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			sideNavigationView.toggleMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			Log.i(Const.TAG, "SessionStatusCallback state: " + state);

			// If there is an exception...
			if (exception != null) {
				spinner.setMessage("Eccezione facebook: "+exception.getMessage());
				return;
			}

			if (session.isOpened()) {
				spinner.setMessage("Connessione a facebook completata");
				spinner.dismiss();
			} else {
				spinner.setMessage("not opened: " + session.getState());
			}
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Session session = Session.getActiveSession();
		session.addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session session = Session.getActiveSession();
		session.removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		session.onActivityResult(this, requestCode,
					resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}
}

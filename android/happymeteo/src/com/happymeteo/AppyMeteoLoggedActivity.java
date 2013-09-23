package com.happymeteo;

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
import com.happymeteo.utils.Const;

public class AppyMeteoLoggedActivity extends AppyMeteoNotLoggedActivity implements
		ISideNavigationCallback {

	private SideNavigationView sideNavigationView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setIcon(R.drawable.icona_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		ImageView icon = (ImageView) findViewById(android.R.id.home);
		if (icon != null) {
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon
					.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = 0;
			iconLp.leftMargin = iconLp.rightMargin = 20;
			icon.setLayoutParams(iconLp);
		}

		sideNavigationView = new SideNavigationView(getApplicationContext());
		sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		sideNavigationView.setMenuClickCallback(this);
		sideNavigationView.setMode(Mode.LEFT);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		addContentView(sideNavigationView, layoutParams);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		menu.findItem(R.id.settings).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.findItem(R.id.menuActivity).setShowAsAction(
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
			invokeActivity(CreateAccountActivity.class);
			break;
		case R.id.menuActivity:
			invokeActivity(MenuActivity.class);
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
				invokeActivity(ChallengeActivity.class);
				break;
	
			case R.id.side_navigation_menu_item5:
				invokeActivity(HappyContextActivity.class);
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
}

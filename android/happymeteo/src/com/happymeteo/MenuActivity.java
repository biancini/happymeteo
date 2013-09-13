package com.happymeteo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.facebook.widget.ProfilePictureView;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class MenuActivity extends SherlockActivity implements ISideNavigationCallback {
	
	private SideNavigationView sideNavigationView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		Log.i(Const.TAG, "Create MenuActivity");

		/* Initialize PushNotificationsService */
		HappyMeteoApplication.i().getPushNotificationsService().initialize(
				getApplicationContext());

		if (!HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId().equals("") 
				&& HappyMeteoApplication.i().getCurrentUser() != null) {
			/* Register device on happymeteo backend */
			ServerUtilities.registerDevice(
					getApplicationContext(), 
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId(),
					HappyMeteoApplication.i().getCurrentUser().getUser_id());
		}
		
		ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.userImage);

		if (HappyMeteoApplication.i().isFacebookSession()) {
			
			userImage.setProfileId(String.valueOf(HappyMeteoApplication
					.i().getCurrentUser().getFacebook_id()));
			userImage.setCropped(true);
		} else {
			userImage.setProfileId(null);
		}

		Button btnModifyAccount = (Button) findViewById(R.id.btnModifyAccount);
		Button btnBeginQuestions = (Button) findViewById(R.id.btnQuestionBegin);
		Button btnChallengeTry = (Button) findViewById(R.id.btnChallengeTry);
		Button btnLogout = (Button) findViewById(R.id.btnLogout);
		Button btnLogout2 = (Button) findViewById(R.id.btnLogout2);
		
		btnModifyAccount.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, CreateAccountActivity.class);
				context.startActivity(intent);
			}
		});

		btnBeginQuestions.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				Intent intent = new Intent(context, QuestionActivity.class);
				context.startActivity(intent);
			}
		});
		
		btnChallengeTry.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				ServerUtilities.requestChallenge(
					view.getContext(),
					HappyMeteoApplication.i().getCurrentUser().getUser_id(),
					HappyMeteoApplication.i().getCurrentUser().getFacebook_id(),
					HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId());
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				
				HappyMeteoApplication.i().logout(context);

				/* Return to index activity */
				Intent intent = new Intent(context, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
		
		btnLogout2.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Context context = view.getContext();
				
				HappyMeteoApplication.i().setAccessToken("");
				
				HappyMeteoApplication.i().logout(context);

				/* Return to index activity */
				Intent intent = new Intent(context, IndexActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
		
		sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);
        sideNavigationView.setMode(Mode.LEFT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onDestroy() {
		HappyMeteoApplication.i().logout(getApplicationContext());

		super.onDestroy();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu, menu);
        if (sideNavigationView.getMode() == Mode.RIGHT) {
            menu.findItem(R.id.mode_right).setChecked(true);
        } else {
            menu.findItem(R.id.mode_left).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideNavigationView.toggleMenu();
                break;
            case R.id.mode_left:
                item.setChecked(true);
                sideNavigationView.setMode(Mode.LEFT);
                break;
            case R.id.mode_right:
                item.setChecked(true);
                sideNavigationView.setMode(Mode.RIGHT);
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
            	invokeActivity(HappyContextActivity.class);
                break;

            case R.id.side_navigation_menu_item4:
            	invokeActivity(HappyMapActivity.class);
                break;

            case R.id.side_navigation_menu_item5:
            	invokeActivity(ChallengeActivity.class);
                break;

            default:
                return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        // hide menu if it shown
        if (sideNavigationView.isShown()) {
            sideNavigationView.hideMenu();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Start activity from SideNavigation.
     * 
     * @param title title of Activity
     * @param resId resource if of background image
     */
    private void invokeActivity(Class<? extends Activity> clazz) {
    	Intent intent = new Intent(this, clazz);
		startActivity(intent);
    }
}

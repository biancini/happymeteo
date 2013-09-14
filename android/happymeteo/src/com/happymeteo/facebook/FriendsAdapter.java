package com.happymeteo.facebook;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.AppyMeteoNotLoggedActivity;
import com.happymeteo.HappyMeteoApplication;
import com.happymeteo.R;
import com.happymeteo.models.Friend;
import com.happymeteo.utils.Const;
import com.happymeteo.utils.ServerUtilities;

public class FriendsAdapter extends ArrayAdapter<Friend> {
	private AppyMeteoNotLoggedActivity activity;
	
	public FriendsAdapter(AppyMeteoNotLoggedActivity activity, List<Friend> values) {
		super(activity.getApplicationContext(), R.layout.activity_friends_facebook_list_row, values);
		
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(
				R.layout.activity_friends_facebook_list_row, parent, false);

		final Friend friend = getItem(position);

		ProfilePictureView profilePictureView = (ProfilePictureView) rowView
				.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(friend.getId());

		/* Set Profile name */
		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		picker_title.setText(friend.getName());

		/* Set Button text */
		Button picker_button = (Button) rowView.findViewById(R.id.picker_button);

		if (getItem(position).isInstalled()) {
			picker_button.setText(R.string.challenge_friend_with_app);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					ServerUtilities.requestChallenge(
						activity, 
						HappyMeteoApplication.i().getCurrentUser().getUser_id(),
						friend.getId(),
						HappyMeteoApplication.i().getPushNotificationsService().getRegistrationId());
				}
			});
		} else {
			/* Feed Dialog: https://developers.facebook.com/docs/reference/dialogs/feed/ */
			picker_button.setText(R.string.challenge_friend_no_app);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					String url = "https://www.facebook.com/dialog/feed?"
							+ "&client_id="+Const.FACEBOOK_ID
							+ "&redirect_uri="+Const.BASE_URL
							+ "&to="+friend.getId();
							
					WebDialog webDialog = new WebDialog(activity, url);
					webDialog.show();
				}
			});
		}

		return rowView;
	}
}
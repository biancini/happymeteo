package com.happymeteo.utils;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.FeedDialogBuilder;
import com.google.android.gcm.GCMRegistrar;
import com.happymeteo.FriendsFacebookActivity;
import com.happymeteo.R;
import com.happymeteo.models.Friend;
import com.happymeteo.models.SessionCache;

public class ListFriend extends AsyncTask<String, Void, Void> {
	private FriendsFacebookActivity activity;
	private List<Friend> friends;
	private LinearLayout pickerLayout;
	private RelativeLayout waitLayout;
	private final int MAX_SIZE = 50;
	private int type;
	
	public ListFriend(int type, FriendsFacebookActivity activity, List<Friend> friends, LinearLayout pickerLayout, RelativeLayout waitLayout) {
		this.type = type;
		this.activity = activity;
		this.friends = friends;
		this.pickerLayout = pickerLayout;
		this.waitLayout = waitLayout;
	}
	
	private void attachFriendToView(View rowView, Friend friend) {
		ProfilePictureView profilePictureView = (ProfilePictureView) rowView
				.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(friend.getId());

		/* Set Profile name */
		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		picker_title.setText(friend.getName());

		/* Set Button text */
		Button picker_button = (Button) rowView.findViewById(R.id.picker_button);
		
		final String friendId = friend.getId();
		
		if (friend.isInstalled()) {
			picker_title.setBackgroundColor(activity.getResources().getColor(R.color.black));
			picker_title.setTextColor(activity.getResources().getColor(R.color.white));
			picker_button.setBackgroundResource(R.drawable.pulsante_gioca);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					ServerUtilities.requestChallenge(
						activity, 
						SessionCache.getUser_id(view.getContext()),
						friendId,
						GCMRegistrar.getRegistrationId(view.getContext()));
				}
			});
		} else {
			/* Feed Dialog: https://developers.facebook.com/docs/reference/dialogs/feed/ */
			picker_button.setBackgroundResource(R.drawable.pulsante_invita);
			picker_button.setOnClickListener(new OnClickListener() {
				
				public void onClick(View view) {
					FeedDialogBuilder feedDialogBuilder = new FeedDialogBuilder(activity, Session.getActiveSession());
					feedDialogBuilder.setDescription("Vieni in appymeteo!");
					feedDialogBuilder.setPicture(Const.BASE_URL + "/img/facebook_invita.png");
					feedDialogBuilder.setTo(friendId);
					WebDialog webDialog = feedDialogBuilder.build();
					webDialog.show();
				}
			});
		}
	}

	@Override
	protected Void doInBackground(String... params) {
		String id = activity.clear(type, pickerLayout, waitLayout);
		List<View> views = new ArrayList<View>();
		
		for (int i = 0; i < friends.size() && i < MAX_SIZE; i++) {
			Friend friend = friends.get(i);
			final View view = activity.getLayoutInflater().inflate(
					R.layout.activity_friends_facebook_list_row, null);
			attachFriendToView(view, friend);
			views.add(view);
		}
		
		activity.populate(type, id, pickerLayout, waitLayout, views);
		return null;
	}
}
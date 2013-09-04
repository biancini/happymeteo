package com.happymeteo.facebook;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.R;
import com.happymeteo.models.Friend;
import com.happymeteo.utils.Const;

public class FriendsAdapter extends ArrayAdapter<Friend> {
	private Activity activity;
	
	public FriendsAdapter(Activity activity, List<Friend> values) {
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
					// TODO Manda richiesta a happymeteo
					// TODO Happymeteo manda notifica push a b
					// TODO b risponde ok a happymeteo
					// TODO HappyMeteo manda notifiche ad a e b
					// TODO Ognuno risponde ad HappyMeteo
					// TODO HappyMeteo manda il risultato tuo con l'avversario
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
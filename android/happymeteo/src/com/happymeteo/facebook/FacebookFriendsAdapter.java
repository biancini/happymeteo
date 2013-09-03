package com.happymeteo.facebook;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.happymeteo.R;
import com.happymeteo.models.Friend;

public class FacebookFriendsAdapter extends ArrayAdapter<Friend> {

	public FacebookFriendsAdapter(Context context, List<Friend> values) {
		super(context, R.layout.activity_friends_facebook_list_row, values);
		//cachedPictures = new HashMap<String, Bitmap>();
	}
	
	/*
	private static final String PROFILEPIC_URL_FORMAT =
            "https://graph.facebook.com/%s/picture";
	private static final String HEIGHT_PARAM = "height";
    private static final String WIDTH_PARAM = "width";
    private static final String MIGRATION_PARAM = "migration_overrides";
    private static final String MIGRATION_VALUE = "{october_2012:true}";
    private HashMap<String, Bitmap> cachedPictures;
	private Bitmap getProfilePicture(String profileId, int width, int height) {
		if(cachedPictures.get(profileId) == null) {
			try {
	        	Uri.Builder builder = new Uri.Builder().encodedPath(String.format(PROFILEPIC_URL_FORMAT, profileId));
	    		builder.appendQueryParameter(HEIGHT_PARAM, String.valueOf(height));
	            builder.appendQueryParameter(WIDTH_PARAM, String.valueOf(width));
	            builder.appendQueryParameter(MIGRATION_PARAM, MIGRATION_VALUE);
				URL url = new URL(builder.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				InputStream stream = connection.getInputStream();
	            Bitmap bitmap = BitmapFactory.decodeStream(stream);
	            cachedPictures.put(profileId, bitmap);
			} catch (Exception e) {
				Log.e(Const.TAG, e.getMessage(), e);
			}
		}
        return cachedPictures.get(profileId);
    }
    */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.activity_friends_facebook_list_row, parent, false);
		
		/* Set Profile picture
		ImageView profilePictureView = (ImageView) rowView.findViewById(R.id.picker_profile_pic_stub);
		Bitmap profilePicture = getProfilePicture(getItem(position).getId(), profilePictureView.getWidth(), profilePictureView.getHeight());
		if(profilePicture != null) {
			profilePictureView.setImageBitmap(profilePicture);
		} */
		
		ProfilePictureView profilePictureView = (ProfilePictureView) rowView.findViewById(R.id.picker_profile_pic_stub);
		profilePictureView.setProfileId(getItem(position).getId());

		/* Set Profile name */
		TextView picker_title = (TextView) rowView.findViewById(R.id.picker_title);
		picker_title.setText(getItem(position).getName());
		
		/* Set Button text */
		Button picker_button = (Button) rowView.findViewById(R.id.picker_button);
		
		if(getItem(position).isInstalled()) {
			picker_button.setText(R.string.challenge_friend_with_app);
		} else {
			picker_button.setText(R.string.challenge_friend_no_app);
		}
		
		return rowView;
	}
}

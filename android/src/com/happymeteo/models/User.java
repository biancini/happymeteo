package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private int facebook_id;
	private String first_name;
	private String last_name;
	private int registered;
	
	public User(JSONObject jsonObject) throws JSONException {
		this.facebook_id = jsonObject.getInt("facebook_id");
		this.first_name = jsonObject.getString("first_name");
		this.last_name = jsonObject.getString("last_name");
		this.registered = jsonObject.getInt("registered");
	}

	public int getFacebook_id() {
		return facebook_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public int getRegistered() {
		return registered;
	}
}

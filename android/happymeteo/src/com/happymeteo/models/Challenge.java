package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Challenge {
	private String registration_id_a;
	private String registration_id_b;
	private String challenge_id;
	private String user_id_a;
	private String user_id_b;
	
	public Challenge(JSONObject jsonObject) throws JSONException {
		this.registration_id_a = jsonObject.getString("registration_id_a");
		this.registration_id_b = jsonObject.getString("registration_id_b");
		this.challenge_id = jsonObject.getString("challenge_id");
		this.user_id_a = jsonObject.getString("user_id_a");
		this.user_id_b = jsonObject.getString("user_id_b");
	}
	
	public String getRegistration_id_a() {
		return registration_id_a;
	}

	public String getRegistration_id_b() {
		return registration_id_b;
	}

	public String getChallenge_id() {
		return challenge_id;
	}

	public String getUser_id_a() {
		return user_id_a;
	}

	public String getUser_id_b() {
		return user_id_b;
	}
}

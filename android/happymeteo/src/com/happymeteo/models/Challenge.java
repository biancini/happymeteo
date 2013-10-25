package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Challenge {
	private String challenge_id = null;
	private String user_id_a = null;
	private String user_id_b = null;
	private String score_a = null;
	private String score_b = null;
	private int turn = 0;
	private User adversary = null;
	private String created = null;
	
	public Challenge(JSONObject jsonObject) throws JSONException {
		this.challenge_id = jsonObject.getString("challenge_id");
		this.user_id_a = jsonObject.getString("user_id_a");
		this.user_id_b = jsonObject.getString("user_id_b");
		this.score_a = jsonObject.getString("score_a");
		this.score_b = jsonObject.getString("score_b");
		this.turn = jsonObject.getInt("turn");
		this.adversary = new User(jsonObject.getJSONObject("adversary"));
		this.created = jsonObject.getString("created");
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

	public String getScore_a() {
		return score_a;
	}

	public String getScore_b() {
		return score_b;
	}

	public int getTurn() {
		return turn;
	}
	
	public User getAdversary() {
		return adversary;
	}
	
	public String getCreated() {
		return created;
	}
}

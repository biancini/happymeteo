package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String user_id;
	private String facebook_id;
	private String first_name;
	private String last_name;
	private int gender;
	private String email;
	private int age;
	private int education;
	private int work;
	private String cap;
	private int registered;
	
	public User(JSONObject jsonObject) throws JSONException {
		user_id = jsonObject.getString("user_id");
		facebook_id = jsonObject.getString("facebook_id");
		first_name = jsonObject.getString("first_name");
		last_name = jsonObject.getString("last_name");
		gender = jsonObject.getInt("gender");
		email = jsonObject.getString("email");
		age = jsonObject.getInt("age");
		education = jsonObject.getInt("education");
		work = jsonObject.getInt("work");
		cap = jsonObject.getString("cap");
		registered = jsonObject.getInt("registered");
	}
	
	public String getUser_id() {
		return user_id;
	}

	public String getFacebook_id() {
		return facebook_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public int getGender() {
		return gender;
	}

	public String getEmail() {
		return email;
	}

	public int getAge() {
		return age;
	}

	public int getEducation() {
		return education;
	}

	public int getWork() {
		return work;
	}

	public String getCap() {
		return cap;
	}

	public int getRegistered() {
		return registered;
	}
}

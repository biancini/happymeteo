package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	public static final int GENDER_MALE = 0;
	public static final int GENDER_FEMALE = 1;
	
	private String user_id = null;
	private String facebook_id = null;
	private String first_name = null;
	private String last_name = null;
	private int gender = GENDER_MALE;
	private String email = null;
	private int age = -1;
	private int education = -1;
	private int work = -1;
	private String cap = null;
	private int registered = -1;
	
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

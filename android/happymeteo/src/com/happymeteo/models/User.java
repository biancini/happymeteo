package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	public static int USER_NOT_REGISTERED = 0;
	public static int USER_REGISTERED = 1;
	
	private String facebook_id;
	private String first_name;
	private String last_name;
	private int gender;
	private String email;
	private int age;
	private int education;
	private int work;
	private String location;
	private int registered;
	
	public User(JSONObject jsonObject) throws JSONException {
		this.facebook_id = jsonObject.getString("facebook_id");
		this.first_name = jsonObject.getString("first_name");
		this.last_name = jsonObject.getString("last_name");
		this.gender = jsonObject.getInt("gender");
		this.email = jsonObject.getString("email");
		this.age = jsonObject.getInt("age");
		this.education = jsonObject.getInt("education");
		this.work = jsonObject.getInt("work");
		this.location = jsonObject.getString("location");
		this.registered = jsonObject.getInt("registered");
	}
	
	public User(String facebook_id, String first_name, String last_name, int gender, String email, int age, int education, int work, String location, int registered) {
		this.facebook_id = facebook_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.email = email;
		this.age = age;
		this.education = education;
		this.work = work;
		this.location = location;
		this.registered = registered;
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

	public String getLocation() {
		return location;
	}

	public int getRegistered() {
		return registered;
	}
}

package com.happymeteo.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.happymeteo.utils.Const;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class User {
	public static int USER_NOT_REGISTERED = 0;
	public static int USER_REGISTERED = 1;
	
	public static void initialize(Context context, JSONObject jsonObject) throws JSONException {
		String user_id = jsonObject.getString("user_id");
		String facebook_id = jsonObject.getString("facebook_id");
		String first_name = jsonObject.getString("first_name");
		String last_name = jsonObject.getString("last_name");
		int gender = jsonObject.getInt("gender");
		String email = jsonObject.getString("email");
		int age = jsonObject.getInt("age");
		int education = jsonObject.getInt("education");
		int work = jsonObject.getInt("work");
		String cap = jsonObject.getString("cap");
		int registered = jsonObject.getInt("registered");
		
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("user_id", user_id);
		editor.putString("facebook_id", facebook_id);
		editor.putString("first_name", first_name);
		editor.putString("last_name", last_name);
		editor.putInt("gender", gender);
		editor.putString("email", email);
		editor.putInt("age", age);
		editor.putInt("education", education);
		editor.putInt("work", work);
		editor.putString("cap", cap);
		editor.putInt("registered", registered);
		
		int today = 1;
		int yesterday = 1;
		int tomorrow = 1;
		
		if(registered == USER_REGISTERED) {
			today = jsonObject.getInt("today");
			yesterday = jsonObject.getInt("yesterday");
			tomorrow = jsonObject.getInt("tomorrow");
		}

		editor.putInt("today", today);
		editor.putInt("yesterday", yesterday);
		editor.putInt("tomorrow", tomorrow);
		
		editor.commit();
	}
	
	public static void initialize(Context context, String user_id, String facebook_id, String first_name, String last_name, int gender, String email, int age, int education, int work, String cap, int registered, int today, int yesterday, int tomorrow) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("user_id", user_id);
		editor.putString("facebook_id", facebook_id);
		editor.putString("first_name", first_name);
		editor.putString("last_name", last_name);
		editor.putInt("gender", gender);
		editor.putString("email", email);
		editor.putInt("age", age);
		editor.putInt("education", education);
		editor.putInt("work", work);
		editor.putString("cap", cap);
		editor.putInt("registered", registered);
		editor.putInt("today", today);
		editor.putInt("yesterday", yesterday);
		editor.putInt("tomorrow", tomorrow);
		editor.commit();
	}
	
	public static boolean isFacebookSession(Context context) {
		String facebook_id = getFacebook_id(context);
		return facebook_id != null && !facebook_id.equals("");
	}
	
	public static String getFacebook_id(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("facebook_id", null);
	}
	
	public static String getUser_id(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("user_id", null);
	}

	public static String getFirst_name(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("first_name", null);
	}

	public static String getLast_name(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("last_name", null);
	}

	public static int getGender(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("gender", 0);
	}

	public static String getEmail(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("email", null);
	}

	public static int getAge(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("age", 0);
	}

	public static int getEducation(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("education", 0);
	}

	public static int getWork(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("work", 0);
	}

	public static int getRegistered(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("registered", User.USER_NOT_REGISTERED);
	}

	public static String getCap(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getString("cap", null);
	}
	
	public static int getToday(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("today", 1);
	}
	
	public static int getYesterday(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("yesterday", 1);
	}
	
	public static int getTomorrow(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(Const.TAG, Context.MODE_PRIVATE);
		return preferences.getInt("tomorrow", 1);
	}
}

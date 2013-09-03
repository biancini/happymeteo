package com.happymeteo.utils;

public final class Const {
	/* Url */
	public static final String BASE_URL = "https://happymeteo.appspot.com";
	
	public static final String CREATE_ACCOUNT = BASE_URL + "/create_account";
	public static final String FACEBOOK_LOGIN_URL = BASE_URL + "/facebook_login";
	public static final String NORMAL_LOGIN_URL = BASE_URL + "/normal_login";
	
	
	
	public static final String REGISTER_URL = BASE_URL + "/register"; 
	public static final String UNREGISTER_URL = BASE_URL + "/unregister";
	
	public static final String GET_QUESTIONS_URL = BASE_URL + "/get_questions";
	public static final String SUBMIT_QUESTIONS_URL = BASE_URL + "/submit_questions";

    /* Google */
	public static final String GOOGLE_ID = "347057775979";
	
	/* Facebook */
	public static final String FACEBOOK_ID = "405414319576008";
	public static final String[] FACEBOOK_PERMISSION_ARRAY_READ = {"email","user_birthday"};
	
	public static String getFacebookReadPermission() {
		String r = "";
		boolean and = false;
		for(String permission : FACEBOOK_PERMISSION_ARRAY_READ) {
			if(and) r+= ",";
			r += permission;
			and = true;
		}
		return r;
	}
	
	/* Password secret key */
	public static final String PASSWORD_SECRET_KEY = "f01a1a0bd409957b9305d2dc21c6b066859f51447c04adfb0b21c9fb5cff9eb0a6573c69e62fcb97321d878bfecb7d2cc8e6650ac6db1a1ef3125373d3e81b85";

    public static final String TAG = "HappyMeteo";

	public static final String EXTRA_MESSAGE = "message";
	
	public enum CREATE_ACCOUNT_STATUS {
		CONFIRMED_OR_FACEBOOK, NOT_CONFIRMED, ERROR
	}
}
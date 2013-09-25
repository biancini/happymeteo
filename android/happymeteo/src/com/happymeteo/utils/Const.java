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
	
	public static final String REQUEST_CHALLENGE_URL = BASE_URL + "/request_challenge"; 
	public static final String ACCEPT_CHALLENGE_URL = BASE_URL + "/accept_challenge";
	public static final String QUESTIONS_CHALLENGE_URL = BASE_URL + "/questions_challenge";
	public static final String SUBMIT_CHALLENGE_URL = BASE_URL + "/submit_challenge";
	
	/* Id Post */
	public static final int CREATE_ACCOUNT_ID = 1;
	public static final int FACEBOOK_LOGIN_URL_ID = 2;
	public static final int NORMAL_LOGIN_URL_ID = 3;
	
	public static final int REGISTER_URL_ID = 4;
	public static final int UNREGISTER_URL_ID = 5;
	
	public static final int GET_QUESTIONS_URL_ID = 6;
	public static final int SUBMIT_QUESTIONS_URL_ID = 7;
	
	public static final int REQUEST_CHALLENGE_URL_ID = 8; 
	public static final int ACCEPT_CHALLENGE_URL_ID = 9;
	public static final int QUESTIONS_CHALLENGE_URL_ID = 10;
	public static final int SUBMIT_CHALLENGE_URL_ID = 11;
	
	public static final int HAPPY_METEO_URL_ID = 12;
	public static final int HAPPY_CONTEXT_URL_ID = 13;
	
	/* Google */
	public static final String GOOGLE_ID = "347057775979";
	
	/* Facebook */
	public static final String FACEBOOK_ID = "405414319576008";
	public static final String[] FACEBOOK_PERMISSIONS = {"email", "user_birthday", "publish_actions"};
	
	/* Password secret key */
	public static final String PASSWORD_SECRET_KEY = "f01a1a0bd409957b9305d2dc21c6b066859f51447c04adfb0b21c9fb5cff9eb0a6573c69e62fcb97321d878bfecb7d2cc8e6650ac6db1a1ef3125373d3e81b85";

    public static final String TAG = "AppyMeteo";
}

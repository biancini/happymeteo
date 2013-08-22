package com.happymeteo.utils;

public final class Const {
	public static final String BASE_URL = "https://happymeteo.appspot.com";
	
	public static final String CREATE_ACCOUNT = BASE_URL + "/create_account";
	public static final String FACEBOOK_LOGIN_URL = BASE_URL + "/facebook_login";
	public static final String NORMAL_LOGIN_URL = BASE_URL + "/normal_login";
	
	public static final String GET_QUESTIONS_URL = BASE_URL + "/get_questions";
	
	public static final String REGISTER_URL = BASE_URL + "/register"; 
	public static final String UNREGISTER_URL = BASE_URL + "/unregister";

    /* Google project id */
	public static final String SENDER_ID = "347057775979";
	
	/* Facebook read permissions */
	public static final String[] FACEBOOK_PERMISSION_ARRAY_READ = {"email","user_birthday"};

    public static final String TAG = "HappyMeteo";

	public static final String EXTRA_MESSAGE = "message";
}

package com.happymeteo.service;

import java.util.HashMap;

public class SessionService {
	private HashMap<String, Object> session;
	
	public SessionService() {
		session = new HashMap<String, Object>();
	}
	
	public Object put(String key, Object value) {
		return session.put(key, value);
	}
	
	public Object get(Object value) {
		return session.get(value);
	}
}

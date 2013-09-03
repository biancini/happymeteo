package com.happymeteo.models;

public class Friend {
	private String name;
	private boolean installed;
	private String id;
	private boolean loaded;
	
	public Friend() {
		id = "";
		name = "";
		installed = false;
		loaded = false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isInstalled() {
		return installed;
	}
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
}

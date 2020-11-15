package com.javaws.threads.repository;

public class KeyItem {

	private String path;
	
	private String key;

	
	public KeyItem() {
		super();
	}

	public KeyItem(String path, String key) {
		super();
		this.path = path;
		this.key = key;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}

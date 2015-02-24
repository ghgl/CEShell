package com.ibm.bao.ceshell.security;

public class Level {
	
	private int mask;
	private String key;
	private String description;
	
	public Level(int mask, String key, String description) {
		super();
		this.mask = mask;
		this.key = key;
		this.description = description;
	}

	public int getMask() {
		return mask;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

package com.ibm.bao.ceshell.security;

import com.filenet.api.constants.AccessRight;

public class ARInfo {
	private AccessRight accessRight;
	private String arName;
	private int value;
	
	public ARInfo(AccessRight accessRight, 
			String arName) {
		this.accessRight = accessRight;
		this.arName = arName;
		this.value = this.accessRight.getValue();
	}
		
	public AccessRight getAccessRight() {
		return accessRight;
	}

	public void setAccessRight(AccessRight accessRight) {
		this.accessRight = accessRight;
	}

	public String getArName() {
		return arName;
	}

	public void setArName(String arName) {
		this.arName = arName;
	}

	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	public boolean isSet(int accessMask) {
		int andValue = accessMask & this.value;
		return (andValue == this.value);
	}
}

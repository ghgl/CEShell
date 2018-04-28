package com.ibm.bao.ceshell.cm;

import java.util.ArrayList;
import java.util.List;

public class LockVO {

	private int type;
	private String resource;
	private String displayName;
	private String caseType;
	private String lockedBy;
	private String timeStamp;

	
	
	public LockVO() {
		super();
	}

	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}



	public String getResource() {
		return resource;
	}



	public void setResource(String resource) {
		this.resource = resource;
	}



	public String getDisplayName() {
		return displayName;
	}



	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	public String getCaseType() {
		return caseType;
	}



	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}



	public String getLockedBy() {
		return lockedBy;
	}



	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}



	public String getTimeStamp() {
		return timeStamp;
	}



	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

}

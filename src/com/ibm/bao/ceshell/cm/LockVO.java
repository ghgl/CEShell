package com.ibm.bao.ceshell.cm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * type: Represents the object type, such as 
 * 		0 SDF, 
 *      1 PE config
 * 		2  Cse Page page, 
 * 		? rule, 
 * 		3 view, 
 * 		5 and task.
 * TODO: include something for resource type in results
 * 
 * @author gregier
 * 
 *
 */
public class LockVO {
	
	public static final String  
		RAW_FMT = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'",
		LOCAL_FMT = "yyyy-MM-dd hh:mm:ss a";

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
	
	public String getLocalTimestamp() throws Exception {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(RAW_FMT);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date d =  dateFormatter.parse(this.getTimeStamp());
		
		SimpleDateFormat localFormatter = new SimpleDateFormat(LOCAL_FMT);
		localFormatter.setTimeZone(TimeZone.getDefault());
		String localTime = localFormatter.format(d);
		return localTime;
	}

}

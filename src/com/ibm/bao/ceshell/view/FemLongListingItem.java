/**
 * 
 */
package com.ibm.bao.ceshell.view;

import java.util.Date;

/**
 *  FemLongDocItem
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class FemLongListingItem  {
	
	public static final String
		FOLDER = "d",
		CUSTOM_OBJECT = "c",
		DOCUMNET = "-";
			
	private String itemTypeIndicator;
	private Boolean isReserved; 
	private String containmentName; 
	private Double contentSize;
	private Date dateCreated;
	private String creator;
	private String classDescriptionName;
	private Integer majorVersionNumber;
	private Integer minorVersionNumber;
	private String versionStatus;
	
	public FemLongListingItem() {
		
	}
	
	public FemLongListingItem(String type) {
		this.itemTypeIndicator = type;
	}
	
	public String getItemTypeIndicator() {
		return itemTypeIndicator;
	}
	public void setItemTypeIndicator(String itemTypeIndicator) {
		this.itemTypeIndicator = itemTypeIndicator;
	}
	public Boolean getIsReserved() {
		return isReserved;
	}
	public void setIsReserved(Boolean isReserved) {
		this.isReserved = isReserved;
	}
	public String getContainmentName() {
		return containmentName;
	}
	public void setContainmentName(String containmentName) {
		this.containmentName = containmentName;
	}
	public Double getContentSize() {
		return contentSize;
	}
	public void setContentSize(Double contentSize) {
		this.contentSize = contentSize;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getClassDescriptionName() {
		return classDescriptionName;
	}
	public void setClassDescriptionName(String classDescriptionName) {
		this.classDescriptionName = classDescriptionName;
	}

	public Integer getMajorVersionNumber() {
		return majorVersionNumber;
	}
	public void setMajorVersionNumber(Integer majorVersionNumber) {
		this.majorVersionNumber = majorVersionNumber;
	}
	public Integer getMinorVersionNumber() {
		return minorVersionNumber;
	}
	public void setMinorVersionNumber(Integer minorVersionNumber) {
		this.minorVersionNumber = minorVersionNumber;
	}
	public String getVersionStatus() {
		return versionStatus;
	}
	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}
}

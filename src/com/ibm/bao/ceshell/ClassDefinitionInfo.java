/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *  ClassDescriptionInfo
 *
 * @author GaryRegier
 * @date   Jun 4, 2011
 */
class ClassDefinitionInfo implements Comparable<ClassDefinitionInfo> {
	public static final String ROOT_NAME = "root";
	
	private String parentName;
	private String id;
	private String symbolicName;
	private int level;
	private String creator;
	private Date dateCreated;
	private Boolean hidden;
	private Boolean systemOwned;
	
	private SortedSet<ClassDefinitionInfo> children;
	
	public ClassDefinitionInfo(
			String id,
			String symbolicName, 
			int level) {
		this();
		this.parentName = null;
		this.id = id;
		this.symbolicName = symbolicName;
		this.level = level;
		this.creator = "";
		this.dateCreated = new Date();
		this.hidden = false;
		this.systemOwned = true;
	}

	public ClassDefinitionInfo() {
		children = new TreeSet<ClassDefinitionInfo>(new Comparator<ClassDefinitionInfo>() {

			public int compare(ClassDefinitionInfo lhs, ClassDefinitionInfo rhs) {
				
				return (lhs.symbolicName.compareTo(rhs.symbolicName));
			}
		});
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Boolean getSystemOwned() {
		return systemOwned;
	}

	public void setSystemOwned(Boolean systemOwned) {
		this.systemOwned = systemOwned;
	}

	public String getSymbolicName() {
		return symbolicName;
	} 
	
	public void setSymbolicName(String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public SortedSet<ClassDefinitionInfo> getChildren() {
		return children;
	}

	public void setChildren(SortedSet<ClassDefinitionInfo> children) {
		this.children = children;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		if (parentName == null) {
			this.parentName = ROOT_NAME; 
		} else {
			this.parentName = parentName;
		}
	}
	
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void addChild(ClassDefinitionInfo child) {
		children.add(child);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ClassDefinitionInfo o) {
		return (this.getSymbolicName().compareTo(o.getSymbolicName()));
	}
	
}

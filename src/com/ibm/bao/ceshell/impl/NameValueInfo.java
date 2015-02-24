/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import com.filenet.api.property.Property;

/**
 *  NameValueInfo
 *
 * @author regier
 * @date   Oct 13, 2011
 */
public class NameValueInfo implements Comparable<NameValueInfo> {
	private String name;
	private String value;
	
	public NameValueInfo() {
		
	}
	
	public NameValueInfo(String propName, Object propValue) { 
		this.name = propName;
		this.value = (propValue == null) ? "" : propValue.toString();
	}
	
	public NameValueInfo(Property fnProp) {
		this.name = fnProp.getPropertyName();
		Object propValue = fnProp.getObjectValue();
		if (propValue != null) {
			this.value = propValue.toString();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(NameValueInfo o) {
		return this.getName().compareTo(o.getName());
	}

}

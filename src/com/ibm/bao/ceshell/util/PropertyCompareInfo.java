/**
 * 
 */
package com.ibm.bao.ceshell.util;

/**
 *  PropertyCompareInfo
 *
 * @author regier
 * @date   Nov 6, 2011
 */
public class PropertyCompareInfo {
	
	private Boolean matched;
	private String propName;
	private String expectedValue;
	private String actualValue;
	
	
	public PropertyCompareInfo(String propName, String expectedValue,
			String actualValue) {
		super();
		this.propName = propName;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.matched = (this.expectedValue.equals(this.actualValue));
	}
	
	public Boolean isMatched() {
		return matched;
	}
	
	public String getPropName() {
		return propName;
	}
	
	public String getExpectedValue() {
		return expectedValue;
	}
	
	public String getActualValue() {
		return actualValue;
	}
	

	@Override
	public String toString() {
		return "PropertyCompareInfo [matched=" + matched + ", propName="
				+ propName + ", expectedValue=" + expectedValue
				+ ", actualValue=" + actualValue + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((propName == null) ? 0 : propName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyCompareInfo other = (PropertyCompareInfo) obj;
		if (propName == null) {
			if (other.propName != null)
				return false;
		} else if (!propName.equals(other.propName))
			return false;
		return true;
	}
}

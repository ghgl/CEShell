/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *  PropertyCompareResults
 *
 * @author regier
 * @date   Nov 6, 2011
 */
public class PropertyComparer {
		
	private Set<PropertyCompareInfo> mismatched = new HashSet<PropertyCompareInfo>();
	private Set<PropertyCompareInfo> matched = new HashSet<PropertyCompareInfo>();
	
	
	public PropertyComparer(Properties expectedValues, Properties actualValues) {
		this.compareProperties(expectedValues, actualValues);
	}
	
	public boolean isAllMatched() {
		return mismatched.size() == 0;
	}
	
	public int getMatchedCount() {
		return matched.size();
	}

	public String getErrorResults() {
		StringBuffer buf = new StringBuffer();
		buf.append("Properties that did not match: " + mismatched.size() + "\n");
		for (PropertyCompareInfo mismatchedProperty : mismatched) {
			buf.append(mismatchedProperty.toString()).append("\n");
		}
		return buf.toString();
	}
	
	private void compareProperties(Properties expectedValues, Properties actualValues) {
		for (Iterator iter = expectedValues.keySet().iterator(); iter.hasNext();) {
			String propName = (String) iter.next();
			String expectedValue = expectedValues.getProperty(propName);
			String actualValue = actualValues.getProperty(propName);
			PropertyCompareInfo pci = 
					new PropertyCompareInfo(propName, expectedValue, actualValue);
			addResult(pci);
		}
	}
	
	private void addResult(PropertyCompareInfo result) {
		if (result.isMatched()) {
			matched.add(result);
		} else {
			mismatched.add(result);
		}
	}
	
}

/**
 * 
 */
package com.ibm.bao.ceshell.security;

import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  AceCompareResults
 *
 * @author GaryRegier
 * @date   Jul 12, 2011
 */
public class AceFieldCompare {
	
	static ColDef[] AceColDefs = new ColDef[] {
		new ColDef("fieldName", 32, StringUtil.ALIGN_LEFT),
		new ColDef("equal", 10, StringUtil.ALIGN_LEFT),
		new ColDef("equivalent", 10, StringUtil.ALIGN_LEFT),
		new ColDef("Values", 50, StringUtil.ALIGN_LEFT)
	};
	
	String fieldName;
	Boolean equal;
	Boolean equivalent;
	String lhsValue;
	String rhsValue;
	public AceFieldCompare(String fieldName, boolean equal, boolean equivalent,
			String lhsValue, String rhsValue) {
		super();
		this.fieldName = fieldName;
		this.equal = equal;
		this.equivalent = equivalent;
		this.lhsValue = lhsValue;
		this.rhsValue = rhsValue;
	}
	
	@Override
	public String toString() {
		String values = null;
		if (equal) {
			values = lhsValue;
		} else {
			values = lhsValue + " <=> " + rhsValue;
		}
		String[] row = new String[] {
				fieldName,
				equal.toString(), 
				equivalent.toString(),
				values
		};
		return StringUtil.formatRow(AceFieldCompare.AceColDefs, row, " ");
		
	}
}
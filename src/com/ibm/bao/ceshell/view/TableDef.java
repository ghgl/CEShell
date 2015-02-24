/**
 * 
 */
package com.ibm.bao.ceshell.view;

import java.util.List;


/**
 *  TableDef
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class TableDef {
	
	private List<FieldFormatter> colDefs;

	public List<FieldFormatter> getColDefs() {
		return colDefs;
	}

	public void setColDefs(List<FieldFormatter> colDefs) {
		this.colDefs = colDefs;
	}
	
	public String formatRow(Object[] rowData) {
		StringBuffer buf = new StringBuffer();
//		formatRow(buf, rowData);
		return buf.toString();
	}
	
//	public void formatRow(StringBuffer buf, Object[] rowData) 
//	throws IllegalArgumentException{
//		if (rowData.length != colDefs.size()) {
//			throw new IllegalArgumentException("different sizes for row data");
//		}
//		for (int i = 0; i < colDefs.size(); i++) {	
//			FieldFormatter def = colDefs.get(i);
//			
//		}
//	}
}

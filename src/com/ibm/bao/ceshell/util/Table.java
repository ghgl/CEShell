/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.bao.ceshell.BaseResponse;

/**
 *  Table
 *
 * @author GaryRegier
 * @date   Jun 30, 2011
 */
public class Table {
	
	private static final String DEFAULT_PAD = " ";
	private ColDef[] colDefs;
	private List<String[]> rows = new ArrayList<String[]>();
	
	private String padChar = DEFAULT_PAD;
	
	public Table(ColDef[] colDefs) {
		this.colDefs = colDefs;
	}
	
	public void addRow(String[] rowData) {
		rows.add(rowData);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(StringUtil.formatHeader(colDefs, padChar));
		for (Iterator<String[]> iterator = rows.iterator(); iterator.hasNext();) {
			String[] nextRow = iterator.next();
			buf.append(StringUtil.formatRow(colDefs, nextRow, padChar));
		}	
		return buf.toString();
	}
}

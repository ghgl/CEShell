/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import com.ibm.bao.ceshell.util.CSV;

/**
 *  BulkLoadConfigInfo
 *
 * @author regier
 * @date   Sep 9, 2011
 */
public class BulkLoadConfigInfo {
	
	
	private String contentProperty;
	private String docClass;
	private char separatorChar;
	
	public BulkLoadConfigInfo() {
		 separatorChar = CSV.DEFAULT_SEP;;
	}
	
	public String getContentProperty() {
		return contentProperty;
	}
	public void setContentProperty(String contentProperty) {
		this.contentProperty = contentProperty;
	}
	public String getDocClass() {
		return docClass;
	}
	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}
	public char getSeparatorChar() {
		return separatorChar;
	}
	public void setSeparatorChar(char separatorChar) {
		this.separatorChar = separatorChar;
	}
	
	

}

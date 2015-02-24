/**
 * 
 */
package com.ibm.bao.ceshell.util;

/**
 *  ColDef
 *
 * @author GaryRegier
 * @date   Oct 13, 2010
 */
public class ColDef {
	private String label;
	private int length;
	private String align;
	
	
	
	public ColDef() {
		super();
	}
	
	public ColDef(String label, int length, String align) {
		super();
		this.label = label;
		this.length = length;
		this.align = align;
	}




	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	
	

}

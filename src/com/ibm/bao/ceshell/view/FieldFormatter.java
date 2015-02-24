/**
 * 
 */
package com.ibm.bao.ceshell.view;

import com.ibm.bao.ceshell.util.StringUtil;

/**
 *  ColDef
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class FieldFormatter {
		
	protected static final String 
		DEFAULT_NAME = "",
		DEFAULT_ALIGN = StringUtil.ALIGN_LEFT,
		DEFAULT_PAD_CHAR = " ";
	
	private static final int DEFAULT_WIDTH = 10;
	
	private String name;
	private int width;
	private String align;
	private String padChar;
	
	public FieldFormatter() {
		this.name = DEFAULT_NAME;
		this.width = DEFAULT_WIDTH;
		this.padChar = DEFAULT_PAD_CHAR;
	}

	public FieldFormatter(String name, int width, String align, String padChar) {
		super();
		this.name = name;
		this.width = width;
		this.align = align;
		this.padChar = padChar;
	}
	
	public void init(String name, int width) {
		this.name = name;
		this.width = width;
	}
	
	public FieldFormatter init(String name, int width, String align, String padChar) {
		this.setName(name);
		this.setWidth(width);
		this.setPadChar(padChar);
		return this;
	}

	public String getAlign() {
		return align;
	}
	public FieldFormatter setAlign(String align) {
		this.align = align;
		return this;
	}
	public String getName() {
		return name;
	}
	public FieldFormatter setName(String name) {
		this.name = name;
		return this;
	}
	public int getWidth() {
		return width;
	}
	public FieldFormatter setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public String getPadChar() {
		return padChar;
	}

	public FieldFormatter setPadChar(String padChar) {
		this.padChar = padChar;
		return this;
	}

	public String format(Object value) {
		String strValue = (value == null) ? "" : value.toString();
		return StringUtil.pad(strValue, getPadChar(), this.getAlign(), this.getWidth());
	}
}

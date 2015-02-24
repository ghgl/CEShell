/**
 * 
 */
package com.ibm.bao.ceshell.view;


/**
 *  BooleanFieldFormatter
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class BooleanFieldFormatter extends FieldFormatter {
	      
	
	private String trueFmt;
	private String falseFmt;
	
	public BooleanFieldFormatter() {
		this(FieldFormatter.DEFAULT_NAME, 25);
	}
	
	public BooleanFieldFormatter(String name, int width) {
		super();
		this.init(name, width);
	}
	
	public String getTrueFmt() {
		return trueFmt;
	}

	public void setTrueFmt(String trueFmt) {
		this.trueFmt = trueFmt;
	}

	public String getFalseFmt() {
		return falseFmt;
	}

	public void setFalseFmt(String falseFmt) {
		this.falseFmt = falseFmt;
	}
	
	public void setFormats(String trueFmt, String falseFmt) {
		this.trueFmt = trueFmt;
		this.falseFmt = falseFmt;
	}

	@Override
	public String format(Object value) {
		
		try {
			Boolean boolVal = (Boolean) value;
			String boolStr;
			if (Boolean.TRUE.equals(boolVal)) {
				boolStr = getTrueBoolStr(); 
			} else {
				boolStr = getFalseBoolStr();
			}
			return super.format(boolStr); 
		} catch (Exception e) {
			return super.format("");
		}
	}
	
	private String getTrueBoolStr() {
		return (trueFmt != null) ? trueFmt: Boolean.TRUE.toString() ;
	}
	
	private String getFalseBoolStr() {
		return (falseFmt != null) ? falseFmt : Boolean.FALSE.toString() ;
	}

}

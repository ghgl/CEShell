/**
 * 
 */
package com.ibm.bao.ceshell.view;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  DateFmtCol
 *
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class DateFieldFormatter extends FieldFormatter {
	
	public static final String
		FEM_DATE_FORMAT = "MM/dd/yyyy hh:mm:ss a",
		UNIX_DATE_FORMAT = "yyyy-MM-dd HH:mm",
		DEFAULT_DATE_FORMAT = FEM_DATE_FORMAT;
	
	private String dateFormat = DateFieldFormatter.DEFAULT_DATE_FORMAT;
	private SimpleDateFormat formatter;
	
	public DateFieldFormatter() {
		this(FieldFormatter.DEFAULT_NAME, 25);
	}
	
	public DateFieldFormatter(String name, int width) {
		super();
		this.init(name, width);
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		formatter = new SimpleDateFormat(dateFormat);
	}

	@Override
	public String format(Object value) {
		
		try {
			Date date = (Date) value;
			String strValue = getFormatter().format(date);
			return super.format(strValue); 
		} catch (Exception e) {
			return super.format("");
		}
	}
	
	protected SimpleDateFormat getFormatter() {
		if (formatter == null) {
			formatter = new SimpleDateFormat(getDateFormat());
		}
		return formatter;
	}
}

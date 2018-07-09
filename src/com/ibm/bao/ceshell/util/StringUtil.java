package com.ibm.bao.ceshell.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import filenet.vw.api.VWFieldType;

public class StringUtil {
	
	public static final String 
			DATE_FMT_FEM = "MM/dd/yyyy hh:mm:ss a",
			DATE_FMT_SHORT = "MM/dd/yyyy",
			DATE_FMT_PE = VWFieldType.TIME_MASK; // i.e. MM/dd/yyyy hh:mm:ss"
				
	
	
	public static final String 
		ALIGN_LEFT = "L",
		ALIGN_RIGHT = "R",
		ALIGN_DEFAULT = ALIGN_LEFT;
	
	public static final String DEFAULT_PAD_CHAR = " ";
	
	public static String decode(String rawString) {
		@SuppressWarnings("deprecation")
		String decoded = java.net.URLDecoder.decode(rawString);
		return decoded;
	}
	
	public static String padRight(String input, int len) {
		int inputLen = input.length();
		int padLen = len - inputLen;
		String result = input;
		if (padLen > 0) {
			for (int i = 0; i < padLen; i++) {
				result = result + DEFAULT_PAD_CHAR;
			}
		}
		return result;
	}
	
	public static String listToString(@SuppressWarnings("rawtypes") List list, String separatorChar) {
		StringBuffer buf = null;
		if (list.isEmpty()) {
			return "";
		}
		
		if (list.size() == 1) {
			return list.get(0).toString();
		}
		
		// more than 1 elem
		buf = new StringBuffer();
		buf.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			buf.append(separatorChar);
			buf.append(list.get(i).toString());
		}
		return buf.toString();
	}
	
	public static String pad(String input, String padChar, String align, int len)  {
		if (ALIGN_RIGHT.equals(align)) {
			return StringUtil.padRight(input, padChar, len);
		} else {
			return padLeft(input, padChar, len);
		}
	}
	
	public static String padLeft(String input, String padChar, int len) {
		int inputLen = 0; 
		if (input != null) {
			inputLen = input.length();
		}
		int padLen = len - inputLen;
		
		
		StringBuffer buf = new StringBuffer();
		buf.append(input);
		if (padLen > 0) {
			for (int i = 0; i < padLen; i++) {
				buf.append(padChar);
			}
		}
		return buf.toString();
	}
	
	/**
	 * 
	 * @param base
	 * @param tring
	 * @return
	 */
	public static String appendArrayToString(String base,
			String delim, 
			String[] itemsToAppend) {
		StringBuffer buf = new StringBuffer();
		
		if (itemsToAppend == null || itemsToAppend.length <= 0) {
			return base;
		}
		if (base != null) {
			buf.append(base);
		}
		
		for (int i = 0; i < itemsToAppend.length - 1; i++) {
			buf.append(itemsToAppend[i]);
			buf.append(delim);
		}
		buf.append(itemsToAppend[itemsToAppend.length - 1]);
		
		return buf.toString();
	}
	
	public static String padRight(String input, String padChar, int len) {
		int inputLen = input.length();
		if (input != null) {
			inputLen = input.length();
		}
		int padLen = len - inputLen;
		StringBuffer buf = new StringBuffer();
		if (padLen > 0) {
			for (int i = 0; i < padLen; i++) {
				buf.append(padChar);
			}
		}
		buf.append(input);
		return buf.toString();
	}
	
	public static String[] toArray(String rawString) {
		StringTokenizer tokenizer; 
		String[] result = null;
		
		if (rawString == null) {
			return result;
		}
		
		tokenizer = new StringTokenizer(rawString);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}
		result = new String[tokens.size()];
		result =(String[]) tokens.toArray(result);
		
		return result;
	}	
	
	public static String fmtDate(Date d) {
		return StringUtil.fmtDate(d, StringUtil.DATE_FMT_FEM);
	}
	
	public static String fmtDate(Date d, String fmt) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(fmt);
		return dateFormatter.format(d);
	}
	
	public static Boolean parseBoolean(String boolStr) {
		char firstChar = 'f';
		
		if (boolStr == null || boolStr.length() == 0) {
			return Boolean.FALSE;
		}
		firstChar = boolStr.trim().toLowerCase().charAt(0);
		if ('t' == firstChar) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Date parseDate(String dateStr) throws Exception {
		if (dateStr.length() == DATE_FMT_SHORT.length()) {
			return parseDate(dateStr, DATE_FMT_SHORT);
		} else if(dateStr.length() == DATE_FMT_PE.length()) {
			return parseDate(dateStr, DATE_FMT_PE);
		} else if (dateStr.length() == DATE_FMT_FEM.length()){
			return parseDate(dateStr, DATE_FMT_FEM);
		} 
		throw new IllegalArgumentException("The date is of an unknown format");
	}
	
	public static Date parseDate(String dateStr, String dateFmt) throws Exception {
		SimpleDateFormat dateFormatter = null;
		dateFormatter = new SimpleDateFormat(dateFmt);
		return dateFormatter.parse(dateStr);
	}
	
	
	public static String formatHeader(ColDef[] cols, String padChar) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cols.length; i++) {
			buf.append(StringUtil.pad(cols[i].getLabel(), 
					padChar,
					cols[i].getAlign(), 
					cols[i].getLength()));
			buf.append(" ");
		}
		// underline
		buf.append("\n");
		for (int i = 0; i < cols.length; i++) {
			buf.append(StringUtil.pad("", "-", 
					StringUtil.ALIGN_LEFT, cols[i].getLength()));
			buf.append(" ");
		}
		return buf.toString();
	}
	
	public static String formatRow(ColDef[] cols, String[] rowData, String padChar) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cols.length; i++) {
			String nextValue = StringUtil.nullCheck(rowData[i]);
			if (i < cols.length - 1) {
				buf.append(StringUtil.pad(nextValue, padChar, cols[i].getAlign(), cols[i].getLength()));
				buf.append(" ");
			} else {
				buf.append(nextValue);
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * @param get_RequiresUniqueElements
	 * @return
	 */
	private static String nullCheck(String value) {
		if (value == null) {
			return "null";
		} else {
			return value;
		}
	}
	
	/**
	 * Format a properties type output where there is label in the left column
	 * and a value in the right column
	 * @param label
	 * @param value
	 * @param labelLen
	 * @param padChar
	 * @return
	 */
	public static String formatTwoCols(String label, 
			String value, 
			int labelColLen, 
			String padChar) {
		return StringUtil.padLeft(label, padChar, labelColLen) + value; 
	}
}

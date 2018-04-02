/**
 * 
 */
package com;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  IOUtils
 *
 * @author gregier
 * @date   Nov 4, 2015
 */
public class IOUtils {
	
	public static String ExceptionToStackTraceMsg(Exception ex) {
		StringBuffer msg = new StringBuffer();
		msg.append(ex.getMessage());
		
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		msg.append(errors.toString());
		
		return msg.toString();
	}

	/**
	 * @param inputStream
	 * @return
	 */
	public static String toString(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

}

/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.util.Properties;

import com.ibm.bao.ceshell.CEShell;

/**
 * MimeTypesUtil
 * 
 * @author regier
 * @date Sep 7, 2011
 */
public class MimeTypesUtil {


	protected Properties mimeTypes = null;

	/**
		 * 
		 */
	public CEShell ceShell;

	/**
		 * 
		 */
	public MimeTypesUtil() throws Exception {
		init();
	}
	
	public void init() throws Exception {
		PropertyUtil util = new PropertyUtil();
		mimeTypes = util.loadProperties("mimetypes.properties");
		
	}
	
	/**
	 * @param doc
	 * @param srcFiles
	 */
	public String getMimeType(String fileName) {
		String mimeType = null;
		String ext = "";
		if (fileName.contains(".")) {
			int pos = fileName.lastIndexOf(".");
			ext = fileName.substring(pos + 1).toLowerCase();
		}
		if (mimeTypes.containsKey(ext)) {
			mimeType = mimeTypes.getProperty(ext);
			
		}
		
		return mimeType;
	}
}
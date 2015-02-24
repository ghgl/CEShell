/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *  AttachmentInfo
 *
 * @author regier
 * @date   Jan 20, 2013
 */
public class AttachmentInfo {
	
	public static final String UNDEFINED_ATTACHMENT = "||0|0||";
	
	private String attachmentClass;
	private String objectStoreName;
	private String attachemntGuid;
	private String docGuid = "";
	private String rawAttachmentString;
	
	/**
	 * An attachment has a string that looks like this:
	 * 	BHDC_ECC_Billing||3|3|BHOSECC|{597C5DC3-7C85-4C2C-88F2-2688948152EE}
	 * The GUID maps to the version series GUID. Unfortunately, this is not searchable.
	 * In order to find the doc, you have to fetch the VersionSeries,
	 * and then fetch the currentVersionId;
	 * 
	 * Uuggh!
	 * 
	 * @param rawAttachmentString should look like this:  BHDC_ECC_Billing||3|3|BHOSECC|{597C5DC3-7C85-4C2C-88F2-2688948152EE}. <br>
	 *          For empty attachment it looks like &quot;||0|0||&quot;
	 *        
	 * @return
	 */
	public AttachmentInfo(String rawAttachmentString) {
		ArrayList<String> results = null;
		
		boolean not_assigned = true;
		String os = null;
		String attachmentClass = null;
		String attachmentGuid = null;
		
		if (UNDEFINED_ATTACHMENT.equals(rawAttachmentString)) {
			// attachment not defined
			
		} else {
			results = parseRawAttachmentString(rawAttachmentString);
			int num_tokens = results.size();
			if (num_tokens < 5) {
				throw new IllegalArgumentException("Unexpected number of tokens on attachment");
			}
			attachmentClass = results.get(0);
			os = results.get(3);
			attachmentGuid = results.get(4);
			not_assigned = false;
		}
		
		this.rawAttachmentString = rawAttachmentString;
		this.setObjectStoreName(os);
		this.setAttachmentClass(attachmentClass);
		this.setAttachemntGuid(attachmentGuid);
	}
	
	public String getAttachmentValue() {
		return rawAttachmentString + " (docId: " + docGuid + ")";
	}
	
	public String getRawAttachmentString() {
		return rawAttachmentString;
	}



	public String getAttachmentClass() {
		return attachmentClass;
	}


	public void setAttachmentClass(String attachmentClass) {
		this.attachmentClass = attachmentClass;
	}


	public String getObjectStoreName() {
		return objectStoreName;
	}


	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}


	public String getAttachemntGuid() {
		return attachemntGuid;
	}


	public void setAttachemntGuid(String attachemntGuid) {
		this.attachemntGuid = attachemntGuid;
	}


	public String getDocGuid() {
		return docGuid;
	}


	public void setDocGuid(String docGuid) {
		this.docGuid = docGuid;
	}
	
	
	/**
	 * @param rawAttachmentString
	 * @return
	 */
	private ArrayList<String> parseRawAttachmentString(String rawAttachmentString) {
		StringTokenizer tok = new StringTokenizer(rawAttachmentString, "|");
		int count = tok.countTokens();
		ArrayList<String> results  = new ArrayList<String>();
		
		while (tok.hasMoreTokens()) {
			String next = tok.nextToken();
			results.add(next);
		}
		return results;
	}



}

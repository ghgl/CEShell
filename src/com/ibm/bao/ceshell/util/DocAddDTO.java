/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.util.List;

/**
 *  DocAddDTO
 *
 * @author regier
 * @date   Mar 22, 2015
 */
public class DocAddDTO extends DocUtilDTO {
	
	public static final String DEFAULT_DOCCLASS_NAME = "Document";
	
	private String docClass = null;
	private String documentTitle = null;
	private String parentFolderUri = null;
	
	
	public DocAddDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public DocAddDTO(String docClass,
			List<File> srcFiles, 
			String documentTitle,
			File propertiesFile,
			String parentFolderUri) throws Exception {
		super(srcFiles, propertiesFile);
		
		this.docClass = docClass;
		this.documentTitle = documentTitle;
		this.parentFolderUri = parentFolderUri;
	}
	
	/**
	 * Typical ctor for unfiled document, with properties
	 * 
	 * @param docClass
	 * @param srcFiles
	 * @param documentTitle
	 * @param props
	 * @throws Exception
	 */
	public DocAddDTO(String docClass,
			List<File> srcFiles, 
			String documentTitle,
			java.util.Properties props) throws Exception {
		super(srcFiles, props);

		this.docClass = docClass;
		this.documentTitle = documentTitle;
	}
	
	
	/**
	 * Typical ctor for unfiled document, with properties as properties file
	 * 
	 * @param docClass
	 * @param srcFiles
	 * @param documentTitle
	 * @param props
	 * @throws Exception
	 */
	public DocAddDTO(String docClass,
			List<File> srcFiles, 
			String documentTitle,
			File propsFile) throws Exception {
		super(srcFiles, propsFile);
		
		this.docClass = docClass;
		this.documentTitle = documentTitle;
	}


	public String getDocClass() {
		if (docClass == null) {
			return DEFAULT_DOCCLASS_NAME;
		} else {
			return docClass;
		}
	}
	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}
	public String getDocumentTitle() {
		return documentTitle;
	}
	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}
	public String getParentFolderUri() {
		return parentFolderUri;
	}
	public void setParentFolderUri(String parentFolderUri) {
		this.parentFolderUri = parentFolderUri;
	}

	
	 public boolean isLinkedToFolder() {
		 return (parentFolderUri == null) ? false : true;
	 }
	
}

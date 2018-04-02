/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  DocUtilDTO
 *
 * @author regier
 * @date   Mar 22, 2015
 */
public abstract class DocUtilDTO {

	protected List<File> srcFiles = new ArrayList<File>();
	protected java.util.Properties docProps = null;

	/**
	 *  No-Opt ctor for bean
	 */
	public DocUtilDTO() {
		super();
	}
	
	/**
	 * Common ctor for new document with one or more src files
	 */
	public DocUtilDTO(List<File>  srcFiles) throws Exception {
		//this.setSrcFiles(srcFiles);
	}
	
	/**
	 * Common ctor for new document with one or more src files and and properties
	 * @param srcFiles
	 * @param propsFile
	 * @throws Exception
	 */
	public DocUtilDTO(List<File> srcFiles, File propsFile) throws Exception {
		this.setSrcFiles(srcFiles);
		this.setDocProps(propsFile);
	}
	
	public DocUtilDTO(List<File> srcFiles, java.util.Properties props) throws Exception {
		this.setSrcFiles(srcFiles);
		this.setDocProps(props);
	}
	

	public List<File> getSrcFiles() {
		return srcFiles;
	}

	public void setSrcFiles(List<File> srcFiles) {
		if (srcFiles == null) {
			return;
		}
		
		this.srcFiles = srcFiles;
	}

	public java.util.Properties getDocProps() {
		return docProps;
	}

	public void setDocProps(java.util.Properties docProps) {
		this.docProps = docProps;
	}
	
	public void setDocProps(File propsFile) throws Exception, IOException {
		if(propsFile == null) {
			return;
		}
		java.util.Properties props = new PropertyUtil().loadPropertiesFromFile(propsFile);
		this.docProps = props;
	}
	
	public boolean hasProperties() {
		if (docProps == null) {
			return false;
		}
		if (docProps.keySet().size() == 0) {
			return false;
		}
		return true;
	}
	
	public boolean hasSrcFiles() {
		if (srcFiles == null) {
			return false;
		}
		if (srcFiles.size() == 0) {
			return false;
		}
		
		return true;
	}

}
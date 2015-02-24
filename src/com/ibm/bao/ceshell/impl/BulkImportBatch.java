/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * BulkImportInfo
 * @author  regier
 * @date    Sep 8, 2011
 */
public class BulkImportBatch {
	
	private Map<String, Integer> fieldsIdx = new HashMap<String, Integer>();
	private int contentFilesFieldIdx;
	private List<String> header;
	private List<List<String>> data;
	private File srcDataFile;
	private BulkLoadConfigInfo configInfo;
	private BulkLoadLogger logger;
	
	public BulkImportBatch(BulkLoadConfigInfo configInfo, File srcDataFile) 
			throws Exception{
		this.configInfo = configInfo;
		this.srcDataFile = srcDataFile;
		logger = new BulkLoadLogger(srcDataFile.getParentFile(), srcDataFile.getName());
	}
	
	
	public BulkLoadConfigInfo getConfigInfo() {
		return configInfo;
	}

	public BulkLoadLogger getLogger() {
		return logger;
	}

	public void setConfigInfo(BulkLoadConfigInfo configInfo) {
		this.configInfo = configInfo;
	}


	public Map<String, Integer> getFieldsIdx() {
		return fieldsIdx;
	}

	public void setFieldsIdx(Map<String, Integer> fieldsIdx) {
		this.fieldsIdx = fieldsIdx;
	}

	public File getSrcDataFile() {
		return srcDataFile;
	}
	
	public void setSrcDataFile(File srcDataFile) {
		this.srcDataFile = srcDataFile;
	}

	public int getContentFilesFieldIdx() {
		return contentFilesFieldIdx;
	}

	public void setContentFilesFieldIdx(int contentFilesFieldIdx) {
		this.contentFilesFieldIdx = contentFilesFieldIdx;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
		initializeFieldsIdx();
	}
	
	public int getHeaderSize() {
		return header.size();
	}

	/**
	 * 
	 */
	private void initializeFieldsIdx() {
		for (int i = 0; i < header.size(); i++) {
			String fieldName = header.get(i);
			String contentProperty = this.configInfo.getContentProperty();
			if (contentProperty.equals(fieldName)) {
				contentFilesFieldIdx = i;
			} else {
				fieldsIdx.put(fieldName, new Integer(i));
			}
		
		}
	}

	public List<List<String>> getData() {
		return data;
	}
	public void setData(List<List<String>> data) {
		this.data = data;
	}
}
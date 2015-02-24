/**
 * 
 */
package com.ibm.bao.ceshell.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.EngineObject;
import com.filenet.api.core.Factory;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.ibm.bao.ceshell.CEShell;
import com.ibm.bao.ceshell.util.MimeTypesUtil;

/**
 * EditInfo
 * 
 * @author regier
 * @date Sep 9, 2011
 */
public class DocEditInfo extends EditInfoImpl implements EditInfo {
	private static MimeTypesUtil mimeTypes = null;
	public DocEditInfo(CEShell ceShell, Document doc, String className) {
		this.ceShell = ceShell;
		this.ceObj = doc;
		this.className = className;
		ClassDescription cd = fetchClassDescription(ceShell, className);
		
		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();

		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDescription pd = (PropertyDescription) iterator.next();
			pd.get_DataType();
			String pdName = pd.get_SymbolicName();
			pdMap.put(pdName, pd);
		}
	}

	@SuppressWarnings("unchecked")
	public void addContentElements(Document doc, List<File> srcFiles)
			throws Exception {
		ContentElementList contentList = Factory.ContentTransfer.createList();

		for (Iterator<File> iter = srcFiles.iterator(); iter.hasNext();) {
			File nextFile = iter.next();
			String name = nextFile.getName();
			String mimeType = this.getMimeTypes().getMimeType(name);
			// First, add a ContentTransfer object.
			ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
			FileInputStream fileIS = new FileInputStream(
					nextFile.getAbsolutePath());
			ctObject.setCaptureSource(fileIS);
			// Add ContentTransfer object to list
			if (mimeType != null) {
				ctObject.set_ContentType(mimeType);
			}
			ctObject.set_RetrievalName(nextFile.getName());
			contentList.add(ctObject);
		}

		doc.set_ContentElements(contentList);
	}
	
	protected MimeTypesUtil getMimeTypes() throws Exception {
		if (mimeTypes == null) {
			mimeTypes = new MimeTypesUtil();
		}
		return mimeTypes;
	}
}
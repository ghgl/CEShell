/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.CEShell;

/**
 *  DocUtil
 *
 * @author regier
 * @date   Mar 31, 2014
 */
public class DocUtil {
	
	private static MimeTypesUtil MimeTypes = null;
	
	CEShell ceShell;

	public DocUtil() {
		super();
	}

	public void setCEShell(CEShell shell) {
		this.ceShell = shell;
	}
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			java.util.Properties props) throws Exception {
				
		Document doc = doCreateDocument(docClass, srcFiles, null, props, null);
		return doc;
	}
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			String documentTitle,
			File propertiesFile) throws Exception {
		
		Document doc = null;
		java.util.Properties props = null;
		
		if (propertiesFile != null) {
			PropertyUtil propUtil = new PropertyUtil();
			props = propUtil.loadPropertiesFromFile(propertiesFile);
		}
		
		doc = doCreateDocument(docClass, srcFiles, documentTitle, props, null);
		
		return doc;
	}
	
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			String documentTitle,
			java.util.Properties props) throws Exception {
		Document doc = null;
		doc = doCreateDocument(docClass, srcFiles, documentTitle, props, null);
		
		return doc;
	}
	
	public Document createDocumentAndFile(
			String docClass,
			List<File> srcFiles, 
			String documentTitle,
			File propertiesFile,
			String parentFolderUri) throws Exception {
		Document doc = null;
		java.util.Properties props = null;
		if (propertiesFile != null) {
			PropertyUtil propUtil = new PropertyUtil();
			props = propUtil.loadPropertiesFromFile(propertiesFile);
		}
		doc = doCreateDocument(docClass, srcFiles, documentTitle, props, parentFolderUri);
		
		return doc;
	}
	
/* --------------------------------------------------------------
 *  implementation
 */
	
	/*
	 * Execute the command to create the document and file if required
	 */
	protected Document doCreateDocument(
			String docClass,
			List<File> srcFiles, 
			String documentTitle,
			java.util.Properties docProps,
			String parentFolderUri) throws Exception {
		
		Document doc = null;
		boolean linkDocToFolder = (parentFolderUri == null) ? false : true;
		String docName = null;
		
		if (docClass == null) {
			docClass = ClassNames.DOCUMENT;
		}
		
		doc = Factory.Document.createInstance(getShell().getObjectStore(), docClass);
		
		
		if (documentTitle != null) {
			doc.getProperties().putValue("DocumentTitle", documentTitle);
		}
		// Set document properties
		if (docProps != null) {
			
			doSetProps(docClass, doc, docProps);
		}
		
		if (srcFiles != null) {
			addContentElements(doc, srcFiles);
		}

		// Check in the document
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.save(RefreshMode.REFRESH);
		docName = doc.get_Name();
		if (linkDocToFolder) {
			fileInFolder(doc, parentFolderUri, docName);
		}
		
		return doc;
	}
	
	private void doSetProps(
			String className, 
			Document doc, 
			java.util.Properties props) throws Exception {
		Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
		Properties docProps = doc.getProperties();
		ClassDescription cd = fetchClassDescription(className);
		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();
		
		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDescription pd = (PropertyDescription) iterator.next();
			pd.get_DataType();
			String pdName = pd.get_SymbolicName();
			pdMap.put(pdName, pd);
		}
		for (Object propName : props.keySet()) {
			String propValue = props.getProperty(propName.toString());
			if (! pdMap.containsKey(propName))  {
				throw new IllegalArgumentException("property with name " + propName + " was not found.");
			}
			PropertyDescription pd = pdMap.get(propName);
			if (pd.get_IsReadOnly()) {
				throw new Exception(String.format("Property %s is read-only", propName));
			}
			applyProperty(className, docProps, pd, propName.toString(), propValue);
		}	
	}
	
	/**
	 * @return
	 */
	private CEShell getShell() {
		return ceShell;
	}

	@SuppressWarnings("unchecked")
	protected void addContentElements(Document doc, List<File> srcFiles) 
				throws Exception {
		ContentElementList contentList = Factory.ContentTransfer.createList();

		for (Iterator<File> iter = srcFiles.iterator(); iter.hasNext();) {
			File nextFile = iter.next();
			String name = nextFile.getName();
			String mimeType = this.getMimeTypes().getMimeType(name);
		    // First, add a ContentTransfer object.
		    ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
		    FileInputStream fileIS = new FileInputStream(nextFile.getAbsolutePath());
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

	/**
	 * @return
	 */
	
	public MimeTypesUtil getMimeTypes() throws Exception {
		if (MimeTypes == null) {
			MimeTypes = new MimeTypesUtil();
		}
		return MimeTypes;
	}
	

	private void fileInFolder(Document doc, String parentFolderUri, String title) {
		Folder parentFolder = null;
		if (parentFolderUri == null) {
			return;
		}
		if (getShell().isId(parentFolderUri)) {
			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					new Id(parentFolderUri), null);
		   
		} else {
			String decodedUri = getShell().urlDecode(parentFolderUri);
			String fullPath = this.getShell().getCWD().relativePathToFullPath(decodedUri);
			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
					fullPath, null);
		}
		// File the document
		ReferentialContainmentRelationship rcr = parentFolder.file(doc,
		        AutoUniqueName.AUTO_UNIQUE, title,
		        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rcr.save(RefreshMode.NO_REFRESH);
	}
	
	public ClassDescription fetchClassDescription(String docClass) {
		ClassDescription cd = Factory.ClassDescription
				.fetchInstance(this.getShell().getObjectStore(), 
				docClass, null);
		return cd;
	}
	
	/**
	 * @param pdMap
	 * @param props
	 */
	public void applyProperty(String parentClassName, Properties props, PropertyDescription pd,
		String propName, String propValue) throws Exception {
				
		TypeID typeId = pd.get_DataType();
		
		switch(typeId.getValue()) {
			case TypeID.STRING_AS_INT:
				props.putValue(propName, propValue);
				break;
			case TypeID.BOOLEAN_AS_INT:
				Boolean bValue = parseBoolean(propValue);
				props.putValue(propName, bValue);
				break;
			case TypeID.DATE_AS_INT:
				Date dateValue = parseDate(propValue);
				props.putValue(propName, dateValue);
				break;
			case TypeID.DOUBLE_AS_INT:
				Double doubleValue = Double.parseDouble(propValue);
				props.putValue(propName, doubleValue);
				break;
			case TypeID.GUID_AS_INT:
				props.putValue(propName, propValue);
				break;
			case TypeID.LONG_AS_INT:
				Integer intValue = Integer.parseInt(propValue);
				props.putValue(propName, intValue);
				break;
			
			default:
				break;
//			case TypeID.BINARY_AS_INT:
//				break;	
			case TypeID.OBJECT_AS_INT:
				String objClasName = fetchObjectPropertyClassName(parentClassName, propName);
				IndependentObject obj = ceShell.getObjectStore().
						fetchObject(objClasName, propValue, null);
				props.putValue(propName, obj);
//				break;
				
		}
			
	}

	/**
	 * @param propName
	 * @return
	 */
	protected String fetchObjectPropertyClassName(String className, String propName) {
		ClassDefinition czd = this.getClassDefinition(className);
		String objClsSymbolicName = null;
		PropertyDefinitionList pdl = czd.get_PropertyDefinitions();
		
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDefinition pd = (PropertyDefinition) iterator.next();
			if (pd.get_SymbolicName().equals(propName)) {
				if (pd instanceof PropertyDefinitionObject) {
					PropertyDefinitionObject pdo = (PropertyDefinitionObject) pd;
					Id objClsId = pdo.get_RequiredClassId();
					ClassDescription ocd = 
							Factory.ClassDescription.fetchInstance(
									ceShell.getObjectStore(), 
									objClsId, 
									null);
					objClsSymbolicName = ocd.get_SymbolicName();	
				}
				break;
			}
		}
		return objClsSymbolicName;
	}
	
	protected ClassDefinition getClassDefinition(String className) {
		
		ClassDefinition clsDef = Factory.ClassDefinition.fetchInstance(
				ceShell.getObjectStore(), 
				className, 
				null);	
		
		return clsDef;
	}
	
	/**
	 * @param propValue
	 * @return
	 */
	protected Boolean parseBoolean(String propValue) {
		char firstChar = propValue.trim().toLowerCase().charAt(0);
		if ('t' == firstChar) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * @param propValue
	 * @return
	 */
	protected Date parseDate(String propValue) throws Exception {
		return StringUtil.parseDate(propValue);
	}

}

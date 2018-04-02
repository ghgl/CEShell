/**
 * 
 */
package com.ibm.bao.ceshell.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionObject;
import com.filenet.api.collection.BooleanList;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DateTimeList;
import com.filenet.api.collection.Float64List;
import com.filenet.api.collection.IdList;
import com.filenet.api.collection.Integer32List;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.PropertyDescriptionList;
import com.filenet.api.collection.StringList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.constants.TypeID;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.meta.PropertyDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyDateTimeList;
import com.filenet.api.property.PropertyFloat64List;
import com.filenet.api.property.PropertyIdList;
import com.filenet.api.property.PropertyInteger32List;
import com.filenet.api.property.PropertyStringList;
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
	
	public DocUtil(CEShell ceShell) {
		this.ceShell = ceShell;
	}

	public void setCEShell(CEShell shell) {
		this.ceShell = shell;
	}
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			java.util.Properties props) throws Exception {
				
		Document doc = createDocument(docClass, srcFiles, null, props);
		return doc;
	}
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			String documentTitle,
			File propertiesFile) throws Exception {
		
		Document doc = null;
		java.util.Properties props = null;
		
//		if (propertiesFile != null) {
//			PropertyUtil propUtil = new PropertyUtil();
//			props = propUtil.loadPropertiesFromFile(propertiesFile);
//		}
		DocAddDTO docInfo = new DocAddDTO(docClass, srcFiles, documentTitle, props);
		doc = doCreateDocument(docInfo);
		
		return doc;
	}
	
	
	public Document createDocument(
			String docClass,
			List<File> srcFiles,
			String documentTitle,
			java.util.Properties props) throws Exception {
		Document doc = null;
		
		
		DocAddDTO docInfo = new DocAddDTO(docClass, srcFiles, documentTitle, props);
		doc = doCreateDocument(docInfo);
		return doc;
	}
	
	public Document createDocumentAndFile(
			String docClass,
			List<File> srcFiles, 
			String documentTitle,
			File propertiesFile,
			String parentFolderUri) throws Exception {
		Document doc = null;
//		java.util.Properties props = null;
//		if (propertiesFile != null) {
//			PropertyUtil propUtil = new PropertyUtil();
//			props = propUtil.loadPropertiesFromFile(propertiesFile);
//		}
		DocAddDTO docInfo = new DocAddDTO(docClass, srcFiles, documentTitle, propertiesFile, parentFolderUri);
		doc = doCreateDocument(docInfo);
		//doc = doCreateDocument(docClass, srcFiles, documentTitle, props, parentFolderUri);
		
		return doc;
	}
	
	public Document fetchDocumentByUri(String docUri) throws Exception {
		Document doc = null;
		if (ceShell.isId(docUri)) {
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					new Id(docUri), null);
		   
		} else {
			String decodedUri = getShell().urlDecode(docUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
			doc = Factory.Document.fetchInstance(getShell().getObjectStore(), 
					fullPath, null);
		}
		
		return doc;
	}
	

	public Document fetchDocument(ObjectStore os, String docIdStr) throws Exception {
		Document doc = null;
		Id id = new Id(docIdStr);
		doc = Factory.Document.fetchInstance(os, id, null);
		if (doc == null) {
			throw new Exception(String.format("Document now found with id ", docIdStr));
		}
		
		Document currentVersion = (Document) doc.get_CurrentVersion();
		return currentVersion;
	}
	
	public boolean isLocked(Document doc) {
		VersionSeries vs = doc.get_VersionSeries();
		boolean actualLockedStatus = vs.get_IsReserved();
		return actualLockedStatus;
	}
	
	
	/**
	 * Pre:
	 * 		Document is not locked
	 * 
	 * Action:
	 * 		Document is locked for update (Reservation has been created)
	 * 
	 * Post:
	 * 		Document reservation can be updated with updateReservation 
	 * 		or with updateReservationAndCheckin.
	 * 		Or, the reservation can be cancelled with cancelCheckout
	 * 		
	 *
	 */
	public boolean checkout(DocCheckoutDTO docInfo) throws Exception {
		Document doc = fetchDocumentByUri(docInfo.getDocUri());
				
		if (doc == null) {
			getShell().getResponse().printErr("No document found at " + docInfo.getDocUri());
			return false;
		}
		
		if (isLocked(doc)) {
			throw new IllegalStateException("document is already locked");
		}
		
		VersionSeries verSeries = doc.get_VersionSeries();
		verSeries.checkout(docInfo.getReservationType(), null, null, null);
		verSeries.save(RefreshMode.REFRESH);
		
		return true;
	}
	
	
	/**
	 * Pre:
	 * 		Document is locked
	 * 
	 * Action:
	 * 		Lock has been removed:
	 * 
	 * Post:
	 * 		Document ready for a new lock
	 * 
	 * @param os
	 * @param docIdStr
	 * @throws Exception
	 */
	public boolean cancelCheckout(DocCheckoutDTO docInfo) throws Exception {
		Document doc = fetchDocumentByUri(docInfo.getDocUri());
		
		if (doc == null) {
			getShell().getResponse().printErr("No document found at " + docInfo.getDocUri());
			return false;
		}
		
		if (! isLocked(doc)) {
			throw new IllegalStateException("Document is not locked");
		}
		Document checkedOutDoc =  (Document) doc.get_Reservation();
		doc.cancelCheckout();
		checkedOutDoc.save(com.filenet.api.constants.RefreshMode.REFRESH);
		return true;
	}
	
//	/**
//	 * Pre:
//	 * 		document is locked
//	 * 
//	 * Actions:
//	 * 		document is checked in
//	 * 
//	 * Post:
//	 * 		new version of the document, with 
//	 * 
//	 * If you want to update properties at the same time as checkin, call updateReservationAndCheckin instead.
//	 * 
//	 * @param os
//	 * @param docIdStr
//	 * @param majorOrMinor
//	 * @throws Exception
//	 */
//	public void checkin(ObjectStore os, String docIdStr, CheckinType majorOrMinor) throws Exception {
//		Document doc = fetchDocument(os, docIdStr);
//		if (! isLocked(doc)) {
//			throw new IllegalStateException("expected the document to be locked");
//		}
//		Document checkedOutDoc  = (Document) doc.get_Reservation();
//		checkedOutDoc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, majorOrMinor);
//		checkedOutDoc.save(RefreshMode.REFRESH);
//	}
	
	/**
	 * Pre:
	 * 		document is locked
	 * 
	 * Actions:
	 * 		update the reserved version of the document with the new content
	 * 
	 * Post:
	 * 		document remains locked, ready for either a checkin or cancel checkout
	 * 
	 * @param os
	 * @param docIdStr
	 * @param updateInfo
	 * @throws Exception
	 */
	public void updateReservation(DocUpdateDTO updateInfo) throws Exception {
		Document doc = fetchDocumentByUri(updateInfo.getDocUri());
		VersionSeries verSeries = doc.get_VersionSeries();
		if (! isLocked(doc)) {
			verSeries.checkout(updateInfo.getReservationType(), null, null, null);
			verSeries.save(RefreshMode.REFRESH);
		}
		
		Document checkedOutDoc = (Document) verSeries.get_Reservation();
		//String docClassName = doc.get_ClassDescription().getClassName();
		String docClassName = doc.getClassName();
		
		if (updateInfo.hasProperties()) {
			doSetProps(docClassName, checkedOutDoc, updateInfo.getDocProps());
		}
		
		if (updateInfo.hasSrcFiles()) {
			addContentElements(checkedOutDoc, updateInfo.getSrcFiles());
		}
		
		if (updateInfo.isCheckInOnUpdate()) {
			checkedOutDoc.checkin(null, updateInfo.getCheckinType());
		}
		checkedOutDoc.save(RefreshMode.REFRESH);
	}
	
//	/**
//	 * Pre:
//	 * 		document is locked
//	 * 
//	 * Actions:
//	 * 		-- update reservation
//	 * 		-- checkin doc
//	 * 
//	 * Post:
//	 * 		-- document has been updated to a new version
//	 * 		-- new file content repalced the original file content
//	 * 
//	 * @param os
//	 * @param idstr
//	 * @param updateInfo
//	 * @throws Exception
//	 */
//	public void updateReservationAndCheckin(ObjectStore os, String docIdStr,  DocUpdateDTO updateInfo) throws Exception {
//		Document doc = fetchDocument(os, docIdStr);
//		VersionSeries verSeries = doc.get_VersionSeries();
//		Document checkedOutDoc = (Document) verSeries.get_Reservation();
//		String docClassName = doc.get_ClassDescription().getClassName();
//		
//		//doApplyProperties(os, checkedOutDoc, updateInfo);
//		doSetProps(docClassName, checkedOutDoc, updateInfo.getDocProps());
//		addContentElements(checkedOutDoc, updateInfo.getSrcFiles());
//		checkedOutDoc.checkin(null, updateInfo.getCheckinType());
//		checkedOutDoc.save(RefreshMode.REFRESH);
//	}
	

//	/**
//	 * Pre:  document is not checked out
//	 *  
//	 * 	-- checkout a document
//	 *  -- update the contents
//	 *  -- checkin as new version
//	 * 
//	 * Post: new version created with new content  
//	 * 
//	 * 
//	 * @param os
//	 * @param idstr
//	 * @param localFile
//	 * @throws Exception
//	 */
//	public void checkInNewVersion(ObjectStore os, String docIdStr, DocUpdateDTO updateInfo) throws Exception {
//		Document doc = fetchDocument(os, docIdStr);
//		if (isLocked(doc)) {
//			throw new IllegalStateException("document is already locked");
//		}
//		
//		VersionSeries verSeries = doc.get_VersionSeries();
//		verSeries.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
//		verSeries.save(RefreshMode.REFRESH);
//		String docClassName = doc.get_ClassDescription().getClassName();
//		Document checkedOutDoc = (Document) verSeries.get_Reservation();
//		
//		doSetProps(docClassName, checkedOutDoc, updateInfo.getDocProps());
//		addContentElements(checkedOutDoc, updateInfo.getSrcFiles());
//		
//		checkedOutDoc.checkin(null, updateInfo.getCheckinType());
//		checkedOutDoc.save(RefreshMode.REFRESH);
//	}
	
/* --------------------------------------------------------------------------
 *                     implementation
 *  -------------------------------------------------------------------------
 */
	
	
	/**
	 * @param docInfo
	 * @return
	 * @throws Exception 
	 */
	private Document doCreateDocument(DocAddDTO docInfo) throws Exception {
		Document doc = null;
		doc = Factory.Document.createInstance(
				getShell().getObjectStore(), 
				docInfo.getDocClass());
		
		String docTitle = docInfo.getDocumentTitle();
		if (docTitle != null) {
			doc.getProperties().putValue("DocumentTitle", docTitle);
		}
		if (docInfo.hasProperties()) {
			doSetProps(docInfo.getDocClass(), doc, docInfo.getDocProps());
		}
		
		if (docInfo.hasSrcFiles()) {
			addContentElements(doc, docInfo.getSrcFiles());
		}
		// Check in the document
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.save(RefreshMode.REFRESH);
		String docName = doc.get_Name();
		if (docInfo.isLinkedToFolder()) {
			String parentFolderUri = docInfo.getParentFolderUri();
			fileInFolder(doc, parentFolderUri, docName);
		}
		
		return doc;
	}
	/*
	 * Execute the command to create the document and file if required
	 * @deprecated
	 */
//	protected Document doCreateDocument(
//			String docClass,
//			List<File> srcFiles, 
//			String documentTitle,
//			java.util.Properties docProps,
//			String parentFolderUri) throws Exception {
//		
//		Document doc = null;
//		boolean linkDocToFolder = (parentFolderUri == null) ? false : true;
//		String docName = null;
//		
//		if (docClass == null) {
//			docClass = ClassNames.DOCUMENT;
//		}
//		
//		doc = Factory.Document.createInstance(getShell().getObjectStore(), docClass);
//		
//		
//		if (documentTitle != null) {
//			doc.getProperties().putValue("DocumentTitle", documentTitle);
//		}
//		// Set document properties
//		if (docProps != null) {
//			
//			doSetProps(docClass, doc, docProps);
//		}
//		
//		if (srcFiles != null) {
//			addContentElements(doc, srcFiles);
//		}
//
//		// Check in the document
//		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
//		doc.save(RefreshMode.REFRESH);
//		docName = doc.get_Name();
//		if (linkDocToFolder) {
//			fileInFolder(doc, parentFolderUri, docName);
//		}
//		
//		return doc;
//	}
	
	private void doSetProps(
			String className, 
			Document doc, 
			java.util.Properties props) throws Exception {
		Map<String, PropertyDescription> pdMap = new HashMap<String, PropertyDescription>();
		Map<String, ArrayList<String>> multiValueProps = new java.util.HashMap<String, java.util.ArrayList<String>>(); 
		java.util.Properties singleValueProps = new java.util.Properties();
		
		Properties docProps = doc.getProperties();
		
		
		ClassDescription cd = fetchClassDescription(className);
		PropertyDescriptionList pdl = cd.get_PropertyDescriptions();
		
		for (Iterator<?> iterator = pdl.iterator(); iterator.hasNext();) {
			PropertyDescription pd = (PropertyDescription) iterator.next();
			pd.get_DataType();
			String pdName = pd.get_SymbolicName();
			pdMap.put(pdName, pd);
		}
		/** 
		 * multi-valued properties are named like
		 * myprop[0] =  "first"
		 * myhprop[1] = "second"
		 * 
		 * Iterate through all properties and add to either the multiValuedProps
		 *  or single-valued props list
		 */
		
		for (Object propName: props.keySet()) {
			String name = (String) propName;
			String propValue = props.getProperty(name);
			int pos = name.indexOf('[');
			if (pos > 0) {
				String multiValueName = name.substring(0, pos);
				ArrayList<String> multiValues = null;
				if (! multiValueProps.containsKey(multiValueName)) {
					 multiValues = new ArrayList<String>();
					 multiValueProps.put(multiValueName, multiValues);
				} else {
					multiValues = multiValueProps.get(multiValueName);
				}
				multiValues.add(propValue);
			} else {
				singleValueProps.setProperty(name, propValue);
			}
		}
		
		/** Apply single-valued properties **/
		for (Object propName : singleValueProps.keySet()) {
			String propValue = props.getProperty(propName.toString());
			if (! pdMap.containsKey(propName))  {
				throw new IllegalArgumentException("property with name " + propName + " was not found.");
			}
			PropertyDescription pd = pdMap.get(propName);
			Cardinality card = pd.get_Cardinality();
			
			if (card.getValue() != Cardinality.SINGLE_AS_INT) {
				throw new IllegalArgumentException("property with name " + propName + " with unexpected cardinality (expected single).");
			}
			if (pd.get_IsReadOnly()) {
				throw new Exception(String.format("Property %s is read-only", propName));
			}
			applyProperty(className, docProps, pd, propName.toString(), propValue);
		}	
		
		/** apply multi-valued properties **/
		for (String propName: multiValueProps.keySet()) {
			ArrayList<String> multiValues = multiValueProps.get(propName);
			if (! pdMap.containsKey(propName)) {
				throw new IllegalArgumentException("property with name " + propName + " was not found.");
			}
			PropertyDescription pd = pdMap.get(propName);
			Cardinality card = pd.get_Cardinality();
			if (card.getValue() !=  Cardinality.LIST_AS_INT) {
				throw new IllegalArgumentException("property with name " + propName + " with unexpected cardinality (expected list).");
			}
			if (pd.get_IsReadOnly()) {
				throw new Exception(String.format("Property %s is read-only", propName));
			}
			applyMultiValueProperty(className, docProps, pd, propName, multiValues);
			
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
	 * @param docProps
	 */
	public void applyProperty(String parentClassName, Properties docProps, PropertyDescription pd,
		String propName, String propValue) throws Exception {
				
		TypeID typeId = pd.get_DataType();
		
		switch(typeId.getValue()) {
			case TypeID.STRING_AS_INT:
				docProps.putValue(propName, propValue);
				break;
			case TypeID.BOOLEAN_AS_INT:
				Boolean bValue = parseBoolean(propValue);
				docProps.putValue(propName, bValue);
				break;
			case TypeID.DATE_AS_INT:
				Date dateValue = parseDate(propValue);
				docProps.putValue(propName, dateValue);
				break;
			case TypeID.DOUBLE_AS_INT:
				Double doubleValue = Double.parseDouble(propValue);
				docProps.putValue(propName, doubleValue);
				break;
			case TypeID.GUID_AS_INT:
				docProps.putValue(propName, propValue);
				break;
			case TypeID.LONG_AS_INT:
				Integer intValue = Integer.parseInt(propValue);
				docProps.putValue(propName, intValue);
				break;
//			case TypeID.BINARY_AS_INT:
//				break;	
			case TypeID.OBJECT_AS_INT:
				String objClasName = fetchObjectPropertyClassName(parentClassName, propName);
				IndependentObject obj = ceShell.getObjectStore().
						fetchObject(objClasName, propValue, null);
				docProps.putValue(propName, obj);
//				break;
			default:
				break;
				
		}
			
	}
	
	
	/**
	 * @param className
	 * @param docProps
	 * @param pd
	 * @param string
	 * @param multiValues
	 * 
	 * TODO: THIS NEEDS A TEST
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void applyMultiValueProperty(String className, 
			Properties docProps,
			PropertyDescription pd, 
			String propName, 
			ArrayList<String> multiValues) throws Exception {
		TypeID typeId = pd.get_DataType();
		
		switch(typeId.getValue()) {
			case TypeID.STRING_AS_INT:
				StringList stringValueList = Factory.StringList.createList();
				for (String propValue : multiValues) {
					
					stringValueList.add(propValue);
				}
				
				/** 
				 * This is weird
				 * If I try to get the property docProps.get(propName) it returns an error
				 * If I just use docProps.putValue(...) it does not save
				 * If I remove the putValue, it gets an error that the property does not exist
				 * 
				 * however, if I do 
				 * 		docProps.putValue(propName, stringValueList);
				 * followed by 
				 * 		PropertyStringList pl = (PropertyStringList) docProps.get(propName);
				 *		pl.setValue(stringValueList);
				 * 
				 * Well, this seems to work.
				 */
				docProps.putValue(propName, stringValueList);
				
				PropertyStringList pl = (PropertyStringList) docProps.get(propName);
				pl.setValue(stringValueList);
				
				break;
			case TypeID.BOOLEAN_AS_INT:
				BooleanList boolValueList = Factory.BooleanList.createList();
				for (String propValue : multiValues) {
					Boolean nextBoolValue = parseBoolean(propValue);
					boolValueList.add(nextBoolValue);
				}
				docProps.putValue(propName, boolValueList);
				break;
			case TypeID.DATE_AS_INT:
				// TODO: TEST
				DateTimeList dateValueList = Factory.DateTimeList.createList();
				for (String propValue : multiValues) {
					Date dateValue = parseDate(propValue);
					dateValueList.add(dateValue);
				}
				
				docProps.putValue(propName, dateValueList);
				PropertyDateTimeList dtpl = (PropertyDateTimeList) docProps.get(propName);
				dtpl.setValue(dateValueList);
				break;
			case TypeID.DOUBLE_AS_INT:
				// TODO: test
				Float64List doubleValueList = Factory.Float64List.createList();
				for (String propValue: multiValues) {
					Double doubleValue = Double.parseDouble(propValue);
					doubleValueList.add(doubleValue);
				}
				docProps.putValue(propName, doubleValueList);
				PropertyFloat64List f64pl = (PropertyFloat64List) docProps.get(propName);
				f64pl.setValue(doubleValueList);
				break;
			case TypeID.GUID_AS_INT:
				// TODO: TEST
				IdList idList = Factory.IdList.createList();
				for (String propValue: multiValues) {
					idList.add(propValue);
				}
				docProps.putValue(propName, idList);
				PropertyIdList idpl = (PropertyIdList) docProps.get(propName);
				idpl.setValue(idList);
				break;
			case TypeID.LONG_AS_INT:
				// TODO: TEST
				Integer32List intList = Factory.Integer32List.createList();
				for (String propValue: multiValues) {
					Integer intValue = Integer.parseInt(propValue);
					intList.add(intValue);
				}
				
				docProps.putValue(propName, intList);
				PropertyInteger32List i32pl = (PropertyInteger32List) docProps.get(propName);
				i32pl.setValue(intList);
				break;
				// 
//			case TypeID.BINARY_AS_INT:
				// TODO
//				break;	
			case TypeID.OBJECT_AS_INT:
//				// TODO: Is there such a thing?
//				IndependentObjectSet indObjSet = Factory.I
//				String objClasName = fetchObjectPropertyClassName(parentClassName, propName);
//				IndependentObject obj = ceShell.getObjectStore().
//						fetchObject(objClasName, propValue, null);
//				docProps.putValue(propName, obj);
//				Prop
//				break;
				
			default:
				break;
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

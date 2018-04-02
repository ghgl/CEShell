package com;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.filenet.api.collection.ContentTransferList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.Properties;
import com.filenet.api.util.Id;

/**
 * 
 */

/**
 *  FileNetFacade
 *
 * @author regier
 * @date   Mar 21, 2015
 */
public class FileNetFacade {
	
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
	 * @param os
	 * @param docIdStr
	 * @throws Exception
	 */
	public void checkout(ObjectStore os, String docIdStr) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		if (isLocked(doc)) {
			throw new IllegalStateException("document is already locked");
		}
		
		VersionSeries verSeries = doc.get_VersionSeries();
		// version = verSeries.get_CurrentVersion();
		verSeries.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
		verSeries.save(RefreshMode.REFRESH);
		
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
	public void cancelCheckout(ObjectStore os, String docIdStr) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		if (! isLocked(doc)) {
			throw new IllegalStateException("Document is not locked");
		}
		Document checkedOutDoc =  (Document) doc.get_Reservation();
		doc.cancelCheckout();
		checkedOutDoc.save(com.filenet.api.constants.RefreshMode.REFRESH);
	}
	
	/**
	 * Pre:
	 * 		document is locked
	 * 
	 * Actions:
	 * 		document is checked in
	 * 
	 * Post:
	 * 		new version of the document, with 
	 * 
	 * If you want to update properties at the same time as checkin, call updateReservationAndCheckin instead.
	 * 
	 * @param os
	 * @param docIdStr
	 * @param majorOrMinor
	 * @throws Exception
	 */
	public void checkin(ObjectStore os, String docIdStr, CheckinType majorOrMinor) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		if (! isLocked(doc)) {
			throw new IllegalStateException("expected the document to be locked");
		}
		Document checkedOutDoc  = (Document) doc.get_Reservation();
		checkedOutDoc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, majorOrMinor);
		checkedOutDoc.save(RefreshMode.REFRESH);
	}
	
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
	public void updateReservation(ObjectStore os, String docIdStr,  UpdateDTO updateInfo) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		if (! isLocked(doc)) {
			throw new IllegalStateException("expected the document to be locked");
		}
		VersionSeries verSeries = doc.get_VersionSeries();
		Document checkedOutDoc = (Document) verSeries.get_Reservation();
		
		doApplyProperties(os, checkedOutDoc, updateInfo);
		doContentTransfer(checkedOutDoc, updateInfo);
		checkedOutDoc.save(RefreshMode.REFRESH);
	}
	
	/**
	 * Pre:
	 * 		document is locked
	 * 
	 * Actions:
	 * 		-- update reservation
	 * 		-- checkin doc
	 * 
	 * Post:
	 * 		-- document has been updated to a new version
	 * 		-- new file content repalced the original file content
	 * 
	 * @param os
	 * @param idstr
	 * @param updateInfo
	 * @throws Exception
	 */
	public void updateReservationAndCheckin(ObjectStore os, String docIdStr,  UpdateDTO updateInfo) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		VersionSeries verSeries = doc.get_VersionSeries();
		Document checkedOutDoc = (Document) verSeries.get_Reservation();
		
		doApplyProperties(os, checkedOutDoc, updateInfo);
		doContentTransfer(checkedOutDoc, updateInfo);
		checkedOutDoc.checkin(null, updateInfo.getCheckinType());
		checkedOutDoc.save(RefreshMode.REFRESH);
	}
	

	/**
	 * Pre:  document is not checked out
	 *  
	 * 	-- checkout a document
	 *  -- update the contents
	 *  -- checkin as new version
	 * 
	 * Post: new version created with new content  
	 * 
	 * 
	 * @param os
	 * @param idstr
	 * @param localFile
	 * @throws Exception
	 */
	public void checkInNewVersion(ObjectStore os, String docIdStr, UpdateDTO updateInfo) throws Exception {
		Document doc = fetchDocument(os, docIdStr);
		if (isLocked(doc)) {
			throw new IllegalStateException("document is already locked");
		}
		
		VersionSeries verSeries = doc.get_VersionSeries();
		verSeries.checkout(ReservationType.OBJECT_STORE_DEFAULT, null, null, null);
		verSeries.save(RefreshMode.REFRESH);
		
		Document checkedOutDoc = (Document) verSeries.get_Reservation();
		
		doApplyProperties(os, checkedOutDoc, updateInfo);
		doContentTransfer(checkedOutDoc, updateInfo);
		checkedOutDoc.checkin(null, updateInfo.getCheckinType());
		checkedOutDoc.save(RefreshMode.REFRESH);
	}

	
	/**
	 * @param os
	 * @param checkedOutDoc
	 * @param updateInfo
	 */
	protected void doApplyProperties(ObjectStore os, 
			Document checkedOutDoc,
			UpdateDTO updateInfo) {
		// TODO apply custom properties or other updates to the document
		
	}
	
	@SuppressWarnings("unchecked")
	private void doContentTransfer(Document doc, UpdateDTO updateInfo) 
			throws FileNotFoundException {
		
		ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
		ctObject.set_RetrievalName(updateInfo.getMimeType());
		ctObject.set_ContentType(updateInfo.getRetrievalFilename());
		byte[] raw_bytes = updateInfo.getData().getBytes();
		
		InputStream fileIS = new ByteArrayInputStream(raw_bytes);
		ContentTransferList contentList = Factory.ContentTransfer.createList();
		ctObject.setCaptureSource(fileIS);
		contentList.add(ctObject);
		doc.set_ContentElements(contentList);
	}
	
	private void readTeamplates(ArrayList<String> fileNames, ObjectStore os,
			IndependentObjectSet set) throws Exception {
			Document fDoc;
			int j = 0;
			InputStream inputStream = null;
			
		//** Is this an error condition or an edge case?
		if (set == null) {
//			LOG.info("readTemplates: set is null");
			return;
		}
		
		//** Sanity-check: make sure sizes matches
		verifySetSizeMatchesFilenamesSize(set, fileNames.size());
		
		try {
			Iterator i = set.iterator();
//			LOG.debug("readTemplates: number of filenemes is " + fileNames.size());
			while (i.hasNext() && j < fileNames.size()) {
				Document doc = (Document) i.next();
				String jsonTemplate = readTemplate(os, doc);
				loadLibrary(jsonTemplate, fileNames.get(j)); // sample code
															
				j++;

			} // end while
	
		} catch (Exception ex) {
//			LOG.error(IOUtils.ExceptionToStackTraceMsg(ex));
			throw ex;
		}
	}



	protected String readTemplate(ObjectStore os, Document doc) {
		Document fDoc;
		InputStream inputStream = null;
		
		try {
			String jsonTemplate;
			Properties props = doc.getProperties();
			String docId = ((Id) props.get(PropertyNames.ID).getIdValue())
					.toString();
//			LOG.info("Retrieving Field Library file from Filenet with DocID: "
//					+ docId);
			fDoc = Factory.Document.fetchInstance(os, docId, null);

			double fDocSize = props.getFloat64Value(PropertyNames.CONTENT_SIZE);
			inputStream = fDoc.accessContentStream(0);
//			jsonTemplate = IOUtils.toString(inputStream);
//			LOG.debug("readTemplate:  read filenet doc into jsonTemplate: " + fDoc.get_Name() );
//			LOG.debug("readTemplate: fDoc size: "+ fDocSize);
//			LOG.debug("readTemplate: jsonTemplate size: " + jsonTemplate.length());
			
//			return jsonTemplate;
			return null;
		} finally  {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					//** do-nothing
				}
				inputStream = null;
			}
			fDoc = null;
		}	
	}

  
	/**
	 * @param set
	 * @param size
	 */
	protected void verifySetSizeMatchesFilenamesSize(IndependentObjectSet set,
			int sizeOfDocsArray) throws Exception {
		Iterator iter = set.iterator();
		int cnt = 0;
		while (iter.hasNext()) {
			cnt++;
			iter.next();
		}
		if (cnt != sizeOfDocsArray) {
			StringBuffer msg = new StringBuffer();
			msg.append("IndependentObjectSet does not match the number of documents in array");
			msg.append("size of set: " + cnt);
			msg.append("size of docs array: " + sizeOfDocsArray);
					
			throw new Exception(msg.toString());
		}
		
	}
	


	/**
	 * @param jsonTemplate
	 * @param string
	 */
	private void loadLibrary(String jsonTemplate, String string) {
		// TODO Auto-generated method stub
		
	}

}
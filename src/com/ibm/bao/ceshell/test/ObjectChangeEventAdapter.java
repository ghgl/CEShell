/**
 * 
 */
package com.ibm.bao.ceshell.test;

import java.util.Date;

import com.filenet.api.action.PendingAction;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectReference;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.meta.ClassDescription;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;

/**
 * ObjectChangeEventAdapter
 * 
 * @author regier
 * @date Aug 23, 2011
 */
public class ObjectChangeEventAdapter implements ObjectChangeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1087396983764129555L;
	
	private Document sourceObject = null;
	private ObjectStore os = null;

	public ObjectChangeEventAdapter(ObjectStore os, Document sourceObject) {
		this.os = os;
		this.sourceObject = sourceObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.RepositoryObject#getObjectStore()
	 */
	public ObjectStore getObjectStore() {
		return os;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_Creator()
	 */
	public String get_Creator() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_DateCreated()
	 */
	public Date get_DateCreated() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_DateLastModified()
	 */
	public Date get_DateLastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_EventStatus()
	 */
	public Integer get_EventStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_Id()
	 */
	public Id get_Id() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_InitiatingUser()
	 */
	public String get_InitiatingUser() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_LastModifier()
	 */
	public String get_LastModifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_Name()
	 */
	public String get_Name() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_Owner()
	 */
	public String get_Owner() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#get_Permissions()
	 */
	public AccessPermissionList get_Permissions() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_Creator(java.lang.String)
	 */
	public void set_Creator(String arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_DateCreated(java.util.Date)
	 */
	public void set_DateCreated(Date arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_DateLastModified(java.util.Date)
	 */
	public void set_DateLastModified(Date arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_EventStatus(java.lang.Integer)
	 */
	public void set_EventStatus(Integer arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_LastModifier(java.lang.String)
	 */
	public void set_LastModifier(String arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.Event#set_Owner(java.lang.String)
	 */
	public void set_Owner(String arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.events.Event#set_Permissions(com.filenet.api.collection
	 * .AccessPermissionList)
	 */
	public void set_Permissions(AccessPermissionList arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#addPendingAction(
	 * com.filenet.api.action.PendingAction)
	 */
	public void addPendingAction(PendingAction arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#clearPendingActions()
	 */
	public void clearPendingActions() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.IndependentlyPersistableObject#delete()
	 */
	public void delete() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#getAccessAllowed()
	 */
	public Integer getAccessAllowed() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#getPendingActions()
	 */
	public PendingAction[] getPendingActions() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#getUpdateSequenceNumber
	 * ()
	 */
	public Integer getUpdateSequenceNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.IndependentlyPersistableObject#isCurrent()
	 */
	public Boolean isCurrent() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#save(com.filenet.
	 * api.constants.RefreshMode)
	 */
	public void save(RefreshMode arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#save(com.filenet.
	 * api.constants.RefreshMode, com.filenet.api.property.PropertyFilter)
	 */
	public void save(RefreshMode arg0, PropertyFilter arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentlyPersistableObject#setUpdateSequenceNumber
	 * (java.lang.Integer)
	 */
	public void setUpdateSequenceNumber(Integer arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentObject#fetchProperties(java.lang.String
	 * [])
	 */
	public void fetchProperties(String[] arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentObject#fetchProperties(com.filenet.api
	 * .property.PropertyFilter)
	 */
	public void fetchProperties(PropertyFilter arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentObject#fetchProperty(java.lang.String,
	 * com.filenet.api.property.PropertyFilter)
	 */
	public Property fetchProperty(String arg0, PropertyFilter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentObject#fetchProperty(java.lang.String,
	 * com.filenet.api.property.PropertyFilter, java.lang.Integer)
	 */
	public Property fetchProperty(String arg0, PropertyFilter arg1, Integer arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.IndependentObject#getObjectReference()
	 */
	public ObjectReference getObjectReference() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.IndependentObject#refresh()
	 */
	public void refresh() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.IndependentObject#refresh(java.lang.String[])
	 */
	public void refresh(String[] arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.filenet.api.core.IndependentObject#refresh(com.filenet.api.property
	 * .PropertyFilter)
	 */
	public void refresh(PropertyFilter arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.EngineObject#getClassName()
	 */
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.EngineObject#getConnection()
	 */
	public Connection getConnection() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.EngineObject#getProperties()
	 */
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.EngineObject#getSuperClasses()
	 */
	public String[] getSuperClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.core.EngineObject#get_ClassDescription()
	 */
	public ClassDescription get_ClassDescription() {
		return sourceObject.get_ClassDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.ObjectChangeEvent#get_SourceClassId()
	 */
	public Id get_SourceClassId() {
		return sourceObject.get_ClassDescription().get_Id();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.ObjectChangeEvent#get_SourceObject()
	 */
	public IndependentObject get_SourceObject() {
		return sourceObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.filenet.api.events.ObjectChangeEvent#get_SourceObjectId()
	 */
	public Id get_SourceObjectId() {
		return sourceObject.get_Id();
	}

}

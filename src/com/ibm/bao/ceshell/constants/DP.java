/**
 * 
 * 
 */
package com.ibm.bao.ceshell.constants;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *  DP
 *  Document Properties
 * @author GaryRegier
 * @date   Sep 19, 2010
 */
public class DP {
	
	//TODO: Replace with PropertyNames.xx
	public static final String
		ActiveMarkings = "ActiveMarkings",
		Annotations = "Annotations",
		AuditedEvents = "AuditedEvents",
		CanDeclare = "CanDeclare",
		ChildDocuments = "ChildDocuments",
		ChildRelationships = "ChildRelationships",
		ClassDescription = "ClassDescription",
		ClassificationStatus = "ClassificationStatus",
		Containers = "Containers",
		ComponentBindingLabel = "ComponentBindingLabel",
		CompoundDocumentState = "CompoundDocumentState",
		ContentElements = "ContentElements",
		ContentElementsPresent = "ContentElementsPresent",
		ContentRetentionDate = "ContentRetentionDate",
		ContentSize = "ContentSize",
		Creator = "Creator",
		CurrentState = "CurrentState",
		CurrentVersion = "CurrentVersion",
		DateCheckedIn = "DateCheckedIn",
		DateContentLastAccessed = "DateContentLastAccessed",
		DateCreated = "DateCreated",
		DateLastModified = "DateLastModified",
		DependentDocuments = "DependentDocuments",
		DestinationDocuments = "DestinationDocuments",
		DocumentLifecyclePolicy = "DocumentLifecyclePolicy",
		DocumentTitle = "DocumentTitle",
		EntryTemplateId = "EntryTemplateId",
		EntryTemplateLaunchedWorkflowNumber = "EntryTemplateLaunchedWorkflowNumber",
		EntryTemplateObjectStoreName = "EntryTemplateObjectStoreName",
		ExternalReplicaIdentities = "ExternalReplicaIdentities",
		FoldersFiledIn = "FoldersFiledIn",
		Id = "Id",
		IgnoreRedirect = "IgnoreRedirect",
		IndexationId = "IndexationId",
		IsCurrentVersion = "IsCurrentVersion",
		IsFrozenVersion = "IsFrozenVersion",
		IsInExceptionState = "IsInExceptionState",
		IsReserved = "IsReserved",
		IsVersioningEnabled = "IsVersioningEnabled",
		LastModifier = "LastModifier",
		LockOwner = "LockOwner",
		LockTimeout = "LockTimeout",
		LockToken = "LockToken",
		MajorVersionNumber = "MajorVersionNumber",
		MimeType = "MimeType",
		MinorVersionNumber = "MinorVersionNumber",
		Name = "Name",
		Owner = "Owner",
		OwnerDocument = "OwnerDocument",
		ParentDocuments = "ParentDocuments",
		ParentRelationships = "ParentRelationships",
		Permissions = "Permissions",
		PublicationInfo = "PublicationInfo",
		PublishingSubsidiaryFolder = "PublishingSubsidiaryFolder",
		RecordInformation = "RecordInformation",
		ReleasedVersion = "ReleasedVersion",
		ReplicationGroup = "ReplicationGroup",
		Reservation = "Reservation",
		ReservationType = "ReservationType",
		SecurityFolder = "SecurityFolder",
		SecurityParent = "SecurityParent",
		SecurityPolicy = "SecurityPolicy",
		SourceDocument = "SourceDocument",
		StorageArea = "StorageArea",
		StorageLocation = "StorageLocation",
		StoragePolicy = "StoragePolicy",
		This = "This",
		Versions = "Versions",
		VersionSeries = "VersionSeries",
		VersionStatus = "VersionStatus",
		WorkflowSubscriptions = "WorkflowSubscriptions";

	public static final String[] DOC_PROPS = new String[] {
		ActiveMarkings,
		Annotations,
		AuditedEvents,
		ChildDocuments,
		ChildRelationships,
		CanDeclare,
		ClassDescription,
		ClassificationStatus,
		ComponentBindingLabel,
		CompoundDocumentState,
		Containers,
		ContentElements,
		ContentElementsPresent,
		ContentRetentionDate,
		ContentSize,
		Creator,
		CurrentState,
		CurrentVersion,
		DateCheckedIn,
		DateContentLastAccessed,
		DateCreated,
		DateLastModified,
		DependentDocuments,
		DestinationDocuments,
		DocumentLifecyclePolicy,
		DocumentTitle,
		EntryTemplateId,
		EntryTemplateLaunchedWorkflowNumber,
		EntryTemplateObjectStoreName,
		ExternalReplicaIdentities,
		FoldersFiledIn,
		Id,
		IgnoreRedirect,
		IndexationId,
		IsCurrentVersion,
		IsFrozenVersion,
		IsInExceptionState,
		IsReserved,
		IsVersioningEnabled,
		LastModifier,
		LockOwner,
		LockTimeout,
		LockToken,
		MajorVersionNumber,
		MimeType,
		MinorVersionNumber,
		Name,
		Owner,
		OwnerDocument,
		ParentDocuments,
		ParentRelationships,
		Permissions,
		PublicationInfo,
		PublishingSubsidiaryFolder,
		RecordInformation,
		ReleasedVersion,
		ReplicationGroup,
		Reservation,
		ReservationType,
		SecurityFolder,
		SecurityParent,
		SecurityPolicy,
		SourceDocument,
		StorageArea,
		StorageLocation,
		StoragePolicy,
		This,
		Versions,
		VersionSeries,
		VersionStatus,
		WorkflowSubscriptions
	};
	
	public static final String[] GENERAL_PROPS = new String[] {
		Id,
		Name,
		ClassDescription,
		ClassificationStatus,
		CompoundDocumentState,
		ContentSize,
		IsReserved,
		IsVersioningEnabled,
		MajorVersionNumber,
		MinorVersionNumber,
		SecurityFolder,
		VersionStatus
	};
	/**
	 * These are standard properties that are not included in the custom
	 * properties view. Since custom properties on subtypes of <code>Document</code>
	 * are not known in advance, we must exclude properties at fetch time.
	 */
	public static final String[] CUSTOM_EXCLUDE_PROPS = new String[] {
		ActiveMarkings,
		Annotations,
		AuditedEvents,
		ChildDocuments,
		ChildRelationships,
		CanDeclare,
		ClassificationStatus,
		ComponentBindingLabel,
		CompoundDocumentState,
		Containers,
		ContentElements,
		ContentElementsPresent,
		ContentRetentionDate,
		ContentSize,
		Creator,
		CurrentState,
		CurrentVersion,
		DateCheckedIn,
		DateContentLastAccessed,
		DateCreated,
		DateLastModified,
		DependentDocuments,
		DestinationDocuments,
		DocumentLifecyclePolicy,
		EntryTemplateId,
		EntryTemplateLaunchedWorkflowNumber,
		EntryTemplateObjectStoreName,
		ExternalReplicaIdentities,
		FoldersFiledIn,
		IgnoreRedirect,
		IndexationId,
		IsCurrentVersion,
		IsFrozenVersion,
		IsInExceptionState,
		IsReserved,
		IsVersioningEnabled,
		LastModifier,
		LockOwner,
		LockTimeout,
		LockToken,
		MajorVersionNumber,
		MinorVersionNumber,
		Owner,
		OwnerDocument,
		ParentDocuments,
		ParentRelationships,
		Permissions,
		PublicationInfo,
		PublishingSubsidiaryFolder,
		ReleasedVersion,
		ReplicationGroup,
		Reservation,
		ReservationType,
		SecurityFolder,
		SecurityParent,
		SecurityPolicy,
		SourceDocument,
		StorageArea,
		StorageLocation,
		StoragePolicy,
		This,
		Versions,
		VersionSeries,
		VersionStatus,
		WorkflowSubscriptions
	};
	
	/**
	 * This property list is used to build a property filter that
	 * matches the FEM document properties worksheet with the custom and 
	 * system properties filter.
	 */
	public static final String[] CUSTOM_AND_SYSTEM_EXCLUDE_PROPS = new String[] {
		Annotations,
		AuditedEvents,
		CanDeclare,
		ChildDocuments,
		ChildRelationships,
		ClassificationStatus,
		ComponentBindingLabel,
		CompoundDocumentState,
		Containers,
		ContentElements,
		ContentElementsPresent,
		CurrentVersion,
		DependentDocuments,
		DestinationDocuments,
		DocumentLifecyclePolicy,
		EntryTemplateId,
		EntryTemplateLaunchedWorkflowNumber,
		EntryTemplateObjectStoreName,
		ExternalReplicaIdentities,
		FoldersFiledIn,
		IndexationId,
		LockOwner,
		LockTimeout,
		LockToken,
		Owner,
		OwnerDocument,
		ParentDocuments, 
		ParentRelationships,
		Permissions,
		PublicationInfo,
		PublishingSubsidiaryFolder,
		ReleasedVersion,
		ReplicationGroup,
		Reservation,
		SecurityFolder,
		SecurityParent,
		SecurityPolicy,
		SourceDocument,
		StorageArea,
		StorageLocation,
		StoragePolicy,
		This,
		Versions,
		VersionSeries,
		WorkflowSubscriptions
	};
	
	/**
	 * This list is used to build a property filter that matches
	 * the FEM document properties worksheet with the ALL properties filter. 
	 */
	public static final String[] ALL_PROPS_EXCLUDE_LIST = new String[] {
		ComponentBindingLabel
	};
	
	public static final String
		CAT_GENERAL = "general",
		CAT_CUSTOM = "custom",
		CAT_CUSTOM_AND_SYSTEM = "custsys",
		CAT_ALL	= "all",
		CAT_DIAGNOSTIC = "diagnostic";
	
	public static final String[] PropCategoriesList = new String[] {
		CAT_GENERAL,
		CAT_CUSTOM,
		CAT_CUSTOM_AND_SYSTEM,
		CAT_ALL,
		CAT_DIAGNOSTIC
	};
	
	public static Map<String, Boolean> PropTypes;
	
	static {
		// init PropTypes
		PropTypes = new HashMap<String, Boolean>();
		{
			PropTypes.put("PropertyBinaryImpl", Boolean.FALSE);
			PropTypes.put("PropertyBinaryListImpl", Boolean.FALSE);
			PropTypes.put("PropertyBooleanImpl", Boolean.TRUE);
			PropTypes.put("PropertyBooleanListImpl",Boolean.TRUE);
			PropTypes.put("PropertyContentImpl", Boolean.TRUE);
			PropTypes.put("PropertyDateTimeImpl", Boolean.TRUE);
			PropTypes.put("PropertyDateTimeListImpl", Boolean.TRUE);
			PropTypes.put("PropertyDependentObjectListImpl", Boolean.FALSE);
			PropTypes.put("PropertyEngineObjectImpl", Boolean.FALSE);
			PropTypes.put("PropertyFloat64Impl", Boolean.TRUE);
			PropTypes.put("PropertyFloat64ListImpl", Boolean.TRUE);
			PropTypes.put("PropertyIdImpl", Boolean.TRUE);
			PropTypes.put("PropertyIdListImpl", Boolean.TRUE);
			PropTypes.put("PropertyIndependentObjectSetImpl", Boolean.FALSE);
			PropTypes.put("PropertyInteger32Impl", Boolean.TRUE);
			PropTypes.put("PropertyInteger32ListImpl", Boolean.TRUE);
			PropTypes.put("PropertyStringImpl", Boolean.TRUE);
			PropTypes.put("PropertyStringListImpl", Boolean.TRUE);
		}	
	}
	
	/** Version Status Labels **/
	public static final String 
		VS_Label_Released = "1(Released)",
		VS_Label_InProcess = "2(In Process)",
		VS_Label_Reservation = "3(Reservation)",
		Vs_Label_Superseded = "4(Superseded)";
	
	public static Map VSLabels = new Hashtable();
	static {
		VSLabels.put(new Integer(com.filenet.api.constants.VersionStatus.IN_PROCESS.getValue()), 
					VS_Label_InProcess);
		VSLabels.put(new Integer(com.filenet.api.constants.VersionStatus.RELEASED.getValue()), 
				VS_Label_Released);
		VSLabels.put(new Integer(com.filenet.api.constants.VersionStatus.RESERVATION.getValue()), 
				VS_Label_Reservation);
		VSLabels.put(new Integer(com.filenet.api.constants.VersionStatus.SUPERSEDED.getValue()), 
				Vs_Label_Superseded);
	}	
}
	

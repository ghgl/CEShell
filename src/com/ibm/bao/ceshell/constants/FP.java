package com.ibm.bao.ceshell.constants;

import java.util.HashMap;
import java.util.Map;

import com.filenet.api.constants.PropertyNames;

public class FP {

	public static final String[] FOLDER_PROPS = new String[] { 
		PropertyNames.ACTIVE_MARKINGS,
		PropertyNames.ANNOTATIONS,
		PropertyNames.AUDITED_EVENTS,
		PropertyNames.CLASS_DESCRIPTION,
		PropertyNames.CM_HOLD_RELATIONSHIPS,
		PropertyNames.CM_INDEXING_FAILURE_CODE,
		PropertyNames.CM_IS_MARKED_FOR_DELETION,
		PropertyNames.CM_RETENTION_DATE,
		PropertyNames.CONTAINED_DOCUMENTS,
		PropertyNames.CONTAINEES,
		PropertyNames.CONTAINERS,
		PropertyNames.COORDINATED_TASKS,
		PropertyNames.CREATOR,
		PropertyNames.DATE_CREATED,
		PropertyNames.DATE_LAST_MODIFIED,
		PropertyNames.EXTERNAL_REPLICA_IDENTITIES,
		PropertyNames.FOLDER_NAME,
		PropertyNames.ID,
		PropertyNames.INDEXATION_ID,
		PropertyNames.INHERIT_PARENT_PERMISSIONS,
		PropertyNames.LAST_MODIFIER,
		PropertyNames.LOCK_TOKEN,
		PropertyNames.NAME,
		PropertyNames.OWNER,
		PropertyNames.PARENT,
		PropertyNames.PATH_NAME,
		PropertyNames.PERMISSIONS,
		PropertyNames.REPLICATION_GROUP,
		PropertyNames.SECURITY_POLICY,
		PropertyNames.SUB_FOLDERS,
		PropertyNames.WORKFLOW_SUBSCRIPTIONS
	};
	
	
	public static final String[] GENERAL_PROPS = new String[] {
		PropertyNames.ID,
		PropertyNames.FOLDER_NAME,
		PropertyNames.CLASS_DESCRIPTION,
		PropertyNames.CREATOR,
		PropertyNames.OWNER,
		PropertyNames.DATE_CREATED,
		PropertyNames.DATE_LAST_MODIFIED
	};
	

	/**
	 * These are standard properties that are not included in the custom
	 * properties view. Since custom properties on subtypes of <code>Document</code>
	 * are not known in advance, we must exclude properties at fetch time.
	 */
	public static final String[] CAT_GENERAL_EXCLUDE = new String[] { 
			PropertyNames.CLASS_DESCRIPTION,
			PropertyNames.CREATOR,
			PropertyNames.DATE_CREATED,
			PropertyNames.DATE_LAST_MODIFIED,
			PropertyNames.FOLDER_NAME,
			PropertyNames.ID,
			PropertyNames.INDEXATION_ID,
			PropertyNames.INHERIT_PARENT_PERMISSIONS,
			PropertyNames.LAST_MODIFIER,
			PropertyNames.LOCK_TOKEN,
			PropertyNames.NAME,
			PropertyNames.PATH_NAME
	};
	

	public static final String 
		CAT_GENERAL = "general", 
		CAT_CUSTOM_AND_GENERAL = "custom", 
		CAT_ALL = "all";

	public static final String[] PropCategoriesList = new String[] { 
			CAT_GENERAL, 
			CAT_CUSTOM_AND_GENERAL,
			CAT_ALL
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
}

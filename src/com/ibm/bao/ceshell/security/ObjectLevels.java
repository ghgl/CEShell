/**
 * 
 * @author GaryRegier
 * 
 */
package com.ibm.bao.ceshell.security;

import java.util.HashMap;
import java.util.Map;

import com.ibm.bao.ceshell.AbsAclCmd;
import com.ibm.bao.ceshell.AbsAclCmd.ACLTYPE;

/**
 *  ObjectLevels
 *
 * @author GaryRegier
 * @date   Oct 8, 2010
 */
public class ObjectLevels {
	
	public static final String 
		// TODO: Add support for property templates. Clean up the levels
		CLASS_DEFINITION_DEFAULT =  AbsAclCmd.ACLTYPE.CLASS_DEFINITION_DEFAULT ,
		CLASS_DEFINITION_KEY = AbsAclCmd.ACLTYPE.CLASS_DEFINITION, 
		DOC_KEY = AbsAclCmd.ACLTYPE.DOCUMENT,
		DOMAIN_KEY = AbsAclCmd.ACLTYPE.DOMAIN,
		FOLDER_KEY = AbsAclCmd.ACLTYPE.FOLDER,
		OS_KEY = AbsAclCmd.ACLTYPE.OBJECT_STORE,
		PROPERTY_TEMPLATE_KEY = AbsAclCmd.ACLTYPE.PROPERTY_TEMPLATE;
		
		
	
	/*
	    os,32899072,full,Full Control
		os,15728640,use,Use object store
		os,1048576,view, View object store
		f,999415,full,Full Control
		f,135155,modP,Modify properties
		f,131121,addTo,Add to Folder
		f,131073,view,View properties
		cd,983827,full,Full Control
		cd,131859,modP,Modify properties
		cd,131089,link,link
		cd,131073,viewP,View properties <Default>
		dom,459267,full,Full Control
		dom,1,use,Use stores and services <Default>
	 */

	private static final Level[] OS_LEVELS = new Level[] {
		new Level(32899072, "full","Full Control"),
		new Level(15728640,"use","Use object store"),
		new Level(1048576,"view", "View object store")
	};
	
	private static final Level[] FOLDER_LEVELS = new Level[] {
		new Level(999415,"full","Full Control"),
		new Level(135155,"modP","Modify properties"),
		new Level(131121,"addTo","Add to Folder"),
		new Level(131073,"view","View properties")
	};
	
	private static final Level[] DOC_CLASS_LEVELS = new Level[] {
		new Level(983827,"full","Full Control"),
		new Level(131859,"modP","Modify properties"),
		new Level(131089,"link","Link"),
		new Level(131073,"viewP","View properties <Default>")
	};
	
	private static final Level[] DOMAIN_LEVELS = new Level[] {
		new Level(459267,"full", "Full Control"),
		new Level(1,"use","Use stores and services <Default>")
	};
	
	private static final Level[] DOCUMENT_LEVELS = new Level[] {
		new Level(133297,"Pub","Publish"),
		new Level(131073,"VProp","View properties"),
		new Level(131201,"VCnt","View content <Default>"),
		new Level(132531,"ModP","Modify properties"),
		new Level(132595,"MiVer","Minor versioning"),
		new Level(132599,"MaVer","Major Versioning"),
		new Level(998871,"Full","Full control")
	};
	
	private static Map<Integer, Level> OSLevelsByMask = 
			new HashMap<Integer, Level>();
	private static Map<Integer, Level> FolderLevelsByMask = 
			new HashMap<Integer, Level>();
	private static Map<Integer, Level> DomainLevelsByMask = 
			new HashMap<Integer, Level>();
	private static Map<Integer, Level> DocClassLevelsByMask = 
			new HashMap<Integer, Level>();
	private static Map<Integer, Level> DocumentLevelsByMask = 
			new HashMap<Integer, Level>();
	private static Map<Integer, Level> DocClassDefaultSecurityLevelsByMask = 
			new HashMap<Integer, Level>();
	
	public static Map<String, Map<Integer, Level>> ObjectTypeLevelsByMask = 
			new HashMap<String, Map<Integer, Level>>();
	public static Map<String, Level[]> ObjectTypeLevelsListByType = 
			new HashMap<String, Level[]>();
	
	
	static {
		ObjectLevels.initLevels(DocumentLevelsByMask, DOCUMENT_LEVELS);
		//NOTE:  Document and Document Class Default Security are identical
		ObjectLevels.initLevels(DocClassDefaultSecurityLevelsByMask, DOCUMENT_LEVELS);
		ObjectLevels.initLevels(DocClassLevelsByMask, DOC_CLASS_LEVELS);
		ObjectLevels.initLevels(DomainLevelsByMask, DOMAIN_LEVELS);
		ObjectLevels.initLevels(FolderLevelsByMask, FOLDER_LEVELS);
		ObjectLevels.initLevels(OSLevelsByMask, OS_LEVELS);
		
		// init ObjectTypeLevelsByMask
		ObjectTypeLevelsByMask.put(DOC_KEY, DocumentLevelsByMask);
		ObjectTypeLevelsByMask.put(CLASS_DEFINITION_DEFAULT, DocClassDefaultSecurityLevelsByMask);
		ObjectTypeLevelsByMask.put(CLASS_DEFINITION_KEY, DocClassLevelsByMask);
		ObjectTypeLevelsByMask.put(DOMAIN_KEY, DomainLevelsByMask);
		ObjectTypeLevelsByMask.put(FOLDER_KEY, FolderLevelsByMask);
		ObjectTypeLevelsByMask.put(OS_KEY, OSLevelsByMask);
	
		// init ObjectTypeLevelsListByType
		ObjectTypeLevelsListByType.put(DOC_KEY, DOCUMENT_LEVELS);
		ObjectTypeLevelsListByType.put(CLASS_DEFINITION_DEFAULT, DOCUMENT_LEVELS);
		ObjectTypeLevelsListByType.put(CLASS_DEFINITION_KEY, DOC_CLASS_LEVELS);
		ObjectTypeLevelsListByType.put(DOMAIN_KEY, DOMAIN_LEVELS);
		ObjectTypeLevelsListByType.put(FOLDER_KEY, FOLDER_LEVELS);
		ObjectTypeLevelsListByType.put(OS_KEY, OS_LEVELS);
	}
	
	
	private static void initLevels(Map<Integer, Level> objectMapByMask, Level[] levels) {
		for(int i = 0; i < levels.length; i++) {
			Level nextLevel = levels[i];
			objectMapByMask.put(nextLevel.getMask(), nextLevel);
		}
	}
	
	/**
	 * Return the map based on the object type code
	 * @param objectTypeKey
	 * @return Map of named levels keyed by Integer value of the access mask
	 * @return null if not found
	 */
	public static Map<Integer, Level> getObjectLevelsByObjectType(String objectTypeKey) {
		return  ObjectTypeLevelsByMask.get(objectTypeKey);
	}
	
	/**
	 * Get the Level entries associated with the type
	 * 
	 * @param objecTypeKey default is d (document)
	 *  
	 * @return Level[] if found
	 * null if the key is not found.
	 */
	public static Level[] getObjectLevelsListByType(String objecTypeKey) {
		if (ObjectTypeLevelsListByType.containsKey(objecTypeKey)) {
			return (Level[]) ObjectTypeLevelsListByType.get(objecTypeKey);
		} 
		return null;
	}
	
}

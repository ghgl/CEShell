package com.ibm.bao.ceshell.security;

import java.util.HashMap;
import java.util.Map;

import com.ibm.bao.ceshell.util.StringUtil;

public class DocLevels {
	private static final int
	PUBLISH_MASK = 133297,
	VIEW_PROPERTIES_MASK = 131073,
	VIEW_CONTENT_MASK = 131201,
	MODIFY_PROPERTIES_MASK = 132531,
	MINOR_VERSIONING_MASK = 132595,
	MAJOR_VERSIONING_MASK = 132599,
	FULL_CONTROL_MASK = 998871;

///**
// * Ordered from least to highest privileges
// */
//private static final int[] DocPrivilegeLevels = new int[] {
//	PUBLISH_MASK,
//	VIEW_PROPERTIES_MASK,
//	VIEW_CONTENT_MASK,
//	MODIFY_PROPERTIES_MASK,
//	MINOR_VERSIONING_MASK,
//	MAJOR_VERSIONING_MASK,
//	FULL_CONTROL_MASK
//	};

// Level Name codes
private static final String 
	PUBLISH_KEY            = "Pub",
	VIEW_PROPERTIES_KEY         = "VProp",
	VIEW_CONTENT_KEY       = "VCnt",
	MODIFY_PROPERTIES_KEY  = "ModP",
	MINOR_VERSIONING_KEY      = "MiVer",
	MAJOR_VERSIONING_KEY      = "MaVer",
	FULL_KEY               = "Full";
private static final String 
	PUBLISH_DESC           = "Publish",	
	VIEW_PROPERTIES_DESC        = "View properties",
	VIEW_CONTENT_DESC      = "View content <Default>",	
	MODIFY_PROPERTIES_DESC = "Modify properties",
	MINOR_VERSIONING_DESC     = "Minor versioning",
	MAJOR_VERSIONIONING_DESC     = "Major Versioning",
	FULL_DESC              = "Full control";

	private Map<Integer, Level> docPrivilegeLevels;
	private Map<String, Level> docPrivilegesByKey;
	
	private Level
		publishLevel,
		viewPropertiesLevel,
		viewContentLevel,
		modifyPropertiesLevel,
		minorVersioningLevel,
		majorVersioningLevel,
		fullControl;
	
	private Level[] allLevels;
	
	public DocLevels() {
		init();
	}
	
	public Level getLevelByMask(Integer mask) {
		Level result = null;
		
		if (docPrivilegeLevels.containsKey(mask)) {
			result = (Level) docPrivilegeLevels.get(mask);
		}
		
		return result;
	}
	
	
	public Map<Integer, Level> getDocPrivilegeLevels() {
		return docPrivilegeLevels;
	}
	
	
	/**
	 * Fetch a named level by key
	 * @param key
	 * @return Level if found, return null if not found
	 */
	public Level getLevelByKey(String key) {
		String searchKey = key.toLowerCase();
		if (docPrivilegesByKey.containsKey(searchKey)) {
			return docPrivilegeLevels.get(searchKey);
		}
		return null;
	}
	
//	public String describeLevels(List acesToCompare) {
//		Level[] levels = new Level[acesToCompare.size()];
//		acesToCompare.toArray(levels);
//		
//		return null;
//	}

	public String describeLevels(boolean longListing) throws Exception {
		return describeLevels(longListing, allLevels);
	}
	
	public String describeLevels(boolean longListing, Level[] levels) 
	throws Exception {
		StringBuffer buf = new StringBuffer();
		getHeader(buf, levels);
		buf.append("\n");
		if (longListing) {
			buf.append(StringUtil.padRight("name", 20));
			for (int i = 0; i < levels.length; i++) {
				buf.append(levels[i].getKey()).append("\t");
			}
			buf.append("\n");
			buf.append("--------------------\t\t\t\t\t\t\t\n");
			for (int i = 0; i < MaskToAR.ARs.length; i++) {
				ARInfo nextInfo = MaskToAR.ARs[i];
				String name = nextInfo.getArName();
				buf.append(StringUtil.padRight(name, 20));
				for (int j = 0; j < levels.length; j++) {
					if (nextInfo.isSet(levels[j].getMask())) {
						buf.append("X");
					} else {
						buf.append("-");
					}
					buf.append("\t");
					
				}
				buf.append("\n");
			}
		}
		return buf.toString();
	}
	
	private void init() {
		publishLevel = new Level(PUBLISH_MASK, PUBLISH_KEY, PUBLISH_DESC);
		viewPropertiesLevel = new Level(VIEW_PROPERTIES_MASK, VIEW_PROPERTIES_KEY, VIEW_PROPERTIES_DESC);
		viewContentLevel = new Level(VIEW_CONTENT_MASK, VIEW_CONTENT_KEY, VIEW_CONTENT_DESC);
		modifyPropertiesLevel = new Level(MODIFY_PROPERTIES_MASK, MODIFY_PROPERTIES_KEY, MODIFY_PROPERTIES_DESC);
		minorVersioningLevel = new Level(MINOR_VERSIONING_MASK, MINOR_VERSIONING_KEY, MINOR_VERSIONING_DESC);
		majorVersioningLevel = new Level(MAJOR_VERSIONING_MASK, MAJOR_VERSIONING_KEY, MAJOR_VERSIONIONING_DESC);
		fullControl = new Level(FULL_CONTROL_MASK, FULL_KEY, FULL_DESC);
		
		// initialize levels
		allLevels = new Level[] {
				publishLevel,
				viewPropertiesLevel,
				viewContentLevel,
				modifyPropertiesLevel,
				minorVersioningLevel,
				majorVersioningLevel,
				fullControl
		};
		docPrivilegeLevels = new HashMap<Integer, Level>();
		for (int i = 0; i < allLevels.length; i++) {
			Level nextLevel = allLevels[i];
			Integer key = nextLevel.getMask();
			docPrivilegeLevels.put(key, nextLevel);
		}
		
		docPrivilegesByKey = new HashMap<String, Level>();
		for (int j = 0; j < allLevels.length; j++) {
			Level nextLevel = allLevels[j];
			String key = nextLevel.getKey().toLowerCase();
			docPrivilegesByKey.put(key,nextLevel);
		}
	}
	
	private void getHeader(StringBuffer buf, Level[] levelsToDescribe) {
		for (int i = 0; i < levelsToDescribe.length; i++) {
			Level nextLevel = levelsToDescribe[i];
			buf.append(nextLevel.getMask()).append("\t")
					.append(nextLevel.getKey()).append("\t")
					.append(nextLevel.getDescription())
					.append("\n");
		}
	}

}
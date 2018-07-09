package com.ibm.bao.ceshell;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.constants.AccessType;
import com.filenet.api.constants.SecurityPrincipalType;
import com.ibm.bao.ceshell.security.Level;
import com.ibm.bao.ceshell.security.MaskToAR;
import com.ibm.bao.ceshell.security.ObjectLevels;
import com.ibm.bao.ceshell.util.StringUtil;

public class ACLListCmd extends AbsAclCmd {
	
	public static final int MAX_GRANTEE_LEN = 90;
	
	
	public static final String 
		CMD = "aclls", 
		CMD_DESC = "Display the ACL list for an object",
		HELP_TEXT = "Display the ACL list for an object. It can be used for " +
		"documents, folders, object store, domain, document class, and the " +
		"default security on a document class." +
		"\nUsage:\n" +
		"\naclls ./gogo            list the acl on document gogo" +
		"\nalcls -d ../gogo        list the acl on document gogo in the parent folder" +
		"\naclls -dc MySubClass    list the acl on doc class MySubClass" +
		"\naclls -dom              list the acl on the default domain. If a domain " +
		" name is provided as an argument, then that value is used" +
		"\naclls -dcd MySubclass   list the default acl from doc class MySubClass" +
		"\naclls -f /foo/baz       list the acl on folder /foo/baz " +
		"\naclls -os  MyOS         list the acl on object store MyOS" +
		"\n\nPaths to folders can be given as relative paths or full paths. An  +" +
		"ID can also be used";
		
		
		
	
	protected MaskToAR maskToAR = new MaskToAR();
	
	
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		String parentType = "d"; // default to doc
		String pathUri = null;
		
		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue()
				.toLowerCase()
				.trim();
		}
		return aclList(parentType, pathUri);
	}


	public boolean aclList(String parentType, String pathUri) 
			throws Exception {
		AccessPermissionList accessPermissionList = null;
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			accessPermissionList = fetchDocumentPermissionList(pathUri);
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			accessPermissionList = fetchFolderPermissionList(pathUri);
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			accessPermissionList = fetchClassPermissionList(pathUri);
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			accessPermissionList = fetchDefaultClassPermissionList(pathUri);
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			accessPermissionList = fetchDomainPermissionList(pathUri);
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)){
			accessPermissionList = fetchObjectStorePermissionList(pathUri);
		} else if (ACLTYPE.PROPERTY_TEMPLATE.equals(parentType)) {
			accessPermissionList = fetchPropertyTemplatePermissionList(pathUri);
		} 
		
		if (accessPermissionList != null) {
			listPermissions(accessPermissionList, parentType, pathUri);
			return true;
		} 
		return false;
	}


	@SuppressWarnings("unchecked")
	protected void listPermissions(AccessPermissionList apl, 
			String objectType, 
			String pathUri) {
		Map<Integer, Level> namedLevels = ObjectLevels.getObjectLevelsByObjectType(objectType);
		SortedSet<String> permissionsSet = new TreeSet<String>();
		Iterator<com.filenet.api.security.AccessPermission> iter = 
			apl.iterator();
		int[] cols = {4, MAX_GRANTEE_LEN, 6, 15, 10, 25, 20};
		getResponse().printOut("ACL Entries for (type: " + objectType + "): " + (pathUri == null ? "<null>" : pathUri));
		formatRowHeader(cols, "a/d", "Grantee", "type", "source", "Level", "Description", "Depth");
		
		while (iter.hasNext()) {
			com.filenet.api.security.AccessPermission ap =  iter.next();
			AccessType grant_deny = ap.get_AccessType();
			String grant_deny_string = (AccessType.ALLOW.equals(grant_deny)) ? "a": "d";
			Integer mask = ap.get_AccessMask();
			String grantee = ap.get_GranteeName();
			SecurityPrincipalType spType = ap.get_GranteeType();
			PermissionSource src = ap.get_PermissionSource();
			Integer inheritableDepth = ap.get_InheritableDepth();
			String maskDescription = null;
			
			if (namedLevels == null) {
				maskDescription = "unknown";
			} else {
				if (namedLevels.containsKey(mask)) {
					Level level = namedLevels.get(mask);
					maskDescription = level.getDescription();
				} else {
					maskDescription = "custom";
				}
			}
			
			int id = inheritableDepth.intValue();
			String idDesc;
			if (id == 0) {
				idDesc = "This object only";
			} else if (id == -1) {
				idDesc = "This object and all children";
			} else if (id == -2) {
				idDesc = "All children but not this object";
			} else {
				idDesc = "This object and immediate children only";
			}
			permissionsSet.add(formatRow(cols, grant_deny_string, grantee, spType.toString(), src.toString(), mask, maskDescription, idDesc));
		}
		
		formatPermissions(permissionsSet);
	}


	private void formatPermissions(SortedSet<String> permissionsSet) {
		Iterator<String> permissionsIter = permissionsSet.iterator();
		while (permissionsIter.hasNext()) {
			String nextFormattedRow = permissionsIter.next();
			getResponse().printOut(nextFormattedRow);
		}
	}
	
	/**
	 * @param cols
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 */
	protected void formatRowHeader(int[] cols, String col0, String col1, String col2,String col3, String col4, String col5, String col6) {
		StringBuffer buf = new StringBuffer();
		int headerLen = 0;
		for(int i = 0; i < cols.length; i++) {
			headerLen += cols[i];
		}
		buf.append(StringUtil.padLeft(col0, " ", cols[0]));
		buf.append(StringUtil.padLeft(col1, " ", cols[1]));
		buf.append(StringUtil.padLeft(col2, " ", cols[2]));
		buf.append(StringUtil.padLeft(col3, " ", cols[3]));
		buf.append(StringUtil.padLeft(col4, " ", cols[4]));
		buf.append(StringUtil.padLeft(col5, " ", cols[5]));
		buf.append(StringUtil.padLeft(col6, " ", cols[6]));
		buf.append("\n" + StringUtil.padLeft("", "-", headerLen));
		getResponse().printOut(buf.toString());
	}

	/**
	 * @param grantee
	 * @param string
	 * @param mask
	 * @param idDesc
	 */
	protected String formatRow(int[] cols, 
			String access_type,
			String grantee, 
			String granteeType,
			String src, 
			Integer mask,
			String maskDescription,
			String idDesc) {
		StringBuffer buf = new StringBuffer();
		if (grantee.length() >= MAX_GRANTEE_LEN) {
			grantee = grantee.substring(0, 85) + "...";
		}
		buf.append(StringUtil.padLeft(access_type, " ",       cols[0]));
		buf.append(StringUtil.padLeft(grantee, " ",           cols[1]));
		buf.append(StringUtil.padLeft(granteeType, " ",       cols[2]));
		buf.append(StringUtil.padLeft(src, " ",               cols[3]));
		buf.append(StringUtil.padLeft(mask.toString(), " ",   cols[4]));
		buf.append(StringUtil.padLeft(maskDescription, " ",   cols[5]));
		buf.append(StringUtil.padLeft(idDesc, " ",            cols[6]));
		
		return buf.toString();
	}
	

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam aclParentTypeOpt = null;
		StringParam pathUriArg = null;
		
		// options
		aclParentTypeOpt= getAclParentTypeOpt();
		
		// cmd args
		pathUriArg = getPathUriArg();

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {aclParentTypeOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

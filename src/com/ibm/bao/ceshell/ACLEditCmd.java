/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.constants.PermissionSource;
import com.filenet.api.core.Factory;
import com.filenet.api.security.AccessPermission;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  acledit
 *
 * @author GaryRegier
 * @date   Jun 7, 2011
 */
public class ACLEditCmd extends AbsAclCmd {
	
	// Options:
	// NOTE: some options are inherited from parent class
	public static final String
		IMPORT_OPT = "file",
		REMOVE_OPT = "remove";
	
	public static final String 
		CMD = "acledit", 
		CMD_DESC = "Edit an object's ACL. You can add, remove, or import entries",
		HELP_TEXT = CMD_DESC + 
		"\nACE entries can be imported from a properties file. " +
		"\nUse the aclexp command to create the export file and then edit it as needed." +
		"An export file is handy if you have multiple ace entries to grant at " +
		"the same time or if the ACE entries are comlex" +
		
		"\nUsage:" +
		"\nacledit -t f -r #AUTHENTICATED-USERS fldra" +
		"\n\tremoves #AUTHENTICATED-USERS from the ACL on folder fldra" +
		"\n" +
		"\n";
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		FileParam addOpt = (FileParam) cl.getOption(IMPORT_OPT);
		StringParam removeOpt = (StringParam) cl.getOption(REMOVE_OPT);
		File addAces = null;
		List<String> removeAces = new ArrayList<String>();
		
		String parentType = ACLTYPE.DOCUMENT; // default to doc
		String pathUri = null;

		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue().toLowerCase()
					.trim();
		}
		if (addOpt.isSet()) {
			addAces = addOpt.getValue();
		}
		if (removeOpt.isSet()) {
			removeAces = removeOpt.getValues();
		} 
		
		return aclEdit(parentType, pathUri, addAces, removeAces);
	}
	
	/**
	 * @param parentType
	 * @param pathUri
	 * @param exportFile
	 */
	public boolean aclEdit(
			String parentType, 
			String pathUri,
			File addAccesSrcFile,
			List<String> granteesToRemoveRaw) 	throws Exception {
		AccessPermissionList addPermissionList = null;
		AccessPermissionList srcPermissionList = null;
		AccessPermissionList finalList = null;
		String description = null;
		List<String> granteesToRemove = null; 
		if (granteesToRemoveRaw.size() > 0) {
			granteesToRemove =  new ArrayList<String>();
			for (String granteeRaw : granteesToRemoveRaw) {
				String grantee = this.getShell().urlDecode(granteeRaw);
				granteesToRemove.add(grantee);
			}
		}
		
		if (addAccesSrcFile != null) {
			addPermissionList = loadAcls(addAccesSrcFile);
		}
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			srcPermissionList = fetchDocumentPermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToDoc(pathUri, finalList);
			description = "Document permissions";
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			srcPermissionList = fetchFolderPermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToFolder(pathUri, finalList);
			description = "folder permissions";
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			srcPermissionList = fetchClassPermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToClassDefinition(pathUri, finalList);
			description = "class definition";
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			srcPermissionList = fetchDomainPermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToDomain(pathUri, finalList);
			description = "Domain permissions";
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			srcPermissionList = fetchDefaultClassPermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToClassDefaultInstance(pathUri, finalList);
			description = "default class definition permissions";
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)){
			srcPermissionList = fetchObjectStorePermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToObjectStore(pathUri, finalList);
			description = "Object store permissions";
		} else if (ACLTYPE.PROPERTY_TEMPLATE.equals(parentType)) {
			srcPermissionList = fetchPropertyTemplatePermissionList(pathUri);
			finalList = mergeAceEntries(srcPermissionList, addPermissionList, granteesToRemove);
			applyPermissionsToPropertyTemplate(pathUri, finalList);
			description = "PropertyTemplate permissions";
		}
		
		getResponse().printOut("applied ace to " + pathUri + " " + description);
		return true;
	}
	
	/**
	 * @param srcPermissionList
	 * @param addPermissionList
	 * @param granteesToRemove
	 */
	private AccessPermissionList mergeAceEntries(
			AccessPermissionList srcPermissionList,
			AccessPermissionList addPermissionList,
			List<String> granteesToRemove) {
		AccessPermissionList directList = null;
		AccessPermissionList finalList = null;
		
		directList = removeInheritedSecurity(srcPermissionList);
		finalList = removeGrantees(directList, granteesToRemove);
		addDirect(finalList, addPermissionList);
		
		return finalList;
	}

	/**
	 * @param directMinusRemoved
	 * @param addPermissionList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void addDirect(
			AccessPermissionList finalList,
			AccessPermissionList addPermissionList) {
		if (addPermissionList != null) {
			@SuppressWarnings("rawtypes")
			Iterator addACLIter = addPermissionList.iterator();
			while (addACLIter.hasNext()) {
				AccessPermission nextPermission = 
					(AccessPermission) addACLIter.next();
				finalList.add(nextPermission);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private AccessPermissionList removeGrantees(
			AccessPermissionList srcPermissionList, 
			List<String> granteesToRemove) {
		@SuppressWarnings("rawtypes")
		Iterator srcACLIter = srcPermissionList.iterator();
		Set<String> granteesToRemoveSet = null; 
		AccessPermissionList finalList = Factory.AccessPermission.createList();
		
		granteesToRemoveSet = convertListToSet(granteesToRemove);
		
		// now, check for matches to remove
		while (srcACLIter.hasNext()) {
			AccessPermission nextPermission = (AccessPermission) srcACLIter.next();
			String grantee = nextPermission.get_GranteeName();
			PermissionSource nextPermSrc = nextPermission.get_PermissionSource();
			if (granteesToRemoveSet.contains(grantee)) {
				continue;
			}
			if (PermissionSource.SOURCE_DIRECT.equals(nextPermSrc) ||
						PermissionSource.SOURCE_DEFAULT.equals(nextPermSrc) ) {
				finalList.add(nextPermission);
			}
		}
		
		return finalList;
	}
	
	private Set<String> convertListToSet(List<String> granteesToRemove) {
		Set<String> granteesToRemoveSet = new HashSet<String>();
		if (granteesToRemove != null) {
			Iterator<String> granteesToRemoveIter = granteesToRemove.iterator();
			// convert to set to make it easier to work with
			while (granteesToRemoveIter.hasNext()) {
				granteesToRemoveSet.add(granteesToRemoveIter.next());
			}
		}
		return granteesToRemoveSet;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam aclParentTypeOpt = null;
		StringParam pathUriArg = null;
		FileParam addOpt = null;
		StringParam removeOpt = null;
		
		
		// options
		aclParentTypeOpt= getAclParentTypeOpt();
		
		addOpt = new FileParam(IMPORT_OPT,
				"file containing ace entries to import onto the acl");
		
		addOpt.setMultiValued(false);
		
		removeOpt = new StringParam(REMOVE_OPT, "grantee names to remove");
		removeOpt.setMultiValued(true);
		
		// cmd args
		pathUriArg = getPathUriArg();

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {aclParentTypeOpt, addOpt, removeOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

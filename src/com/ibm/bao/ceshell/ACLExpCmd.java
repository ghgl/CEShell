/**
 * 
 */
package com.ibm.bao.ceshell;

import java.io.File;

import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.AccessPermissionList;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.security.ACLSerializer;

/**
 *  ACLExpCmd
 *
 * @author GaryRegier
 * @date   Oct 10, 2010
 */
public class ACLExpCmd extends AbsAclCmd {
	

	public static final String 
			CMD = "aclexp", 
			CMD_DESC = "export acl entries",
			HELP_TEXT = "Usage:" +

		 "\n\n\taclexp -t cd -file e:/data/Document.cd.props Document";
	
	// Options:
	// NOTE: some options are inherited from parent class
	public static final String
		EXP_FILE_OPT = "file";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		FileParam expFileParam = (FileParam) cl.getOption(EXP_FILE_OPT);
		String parentType = ACLTYPE.DOCUMENT; // default to doc
		File exportFile = null;
		String pathUri = null;

		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue().toLowerCase()
					.trim();
		}
		
		if (expFileParam.isSet()) {
				exportFile = expFileParam.getValue();
		}
		return aclExport(parentType, pathUri, exportFile);
	}

	

	/**
	 * @param parentType
	 * @param pathUri
	 * @param exportFile
	 */
	public boolean aclExport(
			String parentType, 
			String pathUri,
			File exportFile) 	throws Exception {
		AccessPermissionList accessPermissionList = null;
		String description = null;
		
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			accessPermissionList = fetchDocumentPermissionList(pathUri);
			description = "Document permissions";
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			accessPermissionList = fetchFolderPermissionList(pathUri);
			description = "folder permissions";
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			accessPermissionList = fetchClassPermissionList(pathUri);
			description = "class definition";
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			accessPermissionList = fetchDomainPermissionList(pathUri);
			description = "Domain permissions";
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			accessPermissionList = fetchDefaultClassPermissionList(pathUri);
			description = "default class definition permissions";
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)){
			accessPermissionList = fetchObjectStorePermissionList(pathUri);
			description = "Object store permissions";
		} else if (ACLTYPE.PROPERTY_TEMPLATE.equals(parentType)) {
			accessPermissionList = fetchPropertyTemplatePermissionList(pathUri);
			description = "PropertyTemplate permissions";
		}
		
		if (accessPermissionList != null) {
			return storeAcl(accessPermissionList, exportFile, parentType, pathUri, description);
		} else {
			return false;
		}
	}



	/**
	 * @param docUri
	 * @param accessListFile
	 * @param string
	 */
	@SuppressWarnings("unchecked")
	public boolean storeAcl(
			AccessPermissionList list, 
			File accessListFile, 
			String type, 
			String objectUri, 
			String description)	throws Exception {
		ACLSerializer serializer = new ACLSerializer();
		String msg = null;
		
		if (accessListFile == null) {
			msg = serializer.permissionsToString(list, true, objectUri);
		} else {
			serializer.storeAcls(accessListFile, list, objectUri);
			msg = "stored acls for " + objectUri + " in " + accessListFile.toString();
		}
		getResponse().printOut(msg);
		return true;
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam aclParentTypeOpt = null;
		FileParam expFileOpt = null;
		StringParam pathUriArg = null;
		
		// options
		aclParentTypeOpt = getAclParentTypeOpt();
		expFileOpt = new FileParam(EXP_FILE_OPT, 
				"Ouput file",
				FileParam.OPTIONAL);
		expFileOpt.setMultiValued(false);
		expFileOpt.setOptionLabel("<exp-file>");
		
		// cmd args
		pathUriArg = getPathUriArg();

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {aclParentTypeOpt, expFileOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

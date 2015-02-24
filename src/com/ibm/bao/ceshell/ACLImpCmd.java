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

/**
 *  ACLSetCmd
 *
 * @author GaryRegier
 * @date   Oct 9, 2010
 */
public class ACLImpCmd extends AbsAclCmd {
	
	public static final String 
		CMD = "aclimp", 
		CMD_DESC = "import acl entries and assigned to entites in CE",
		HELP_TEXT = "Usage:" +
		 "\n\taclimp -file e:/data/Document.cd.props -t cd Document";

	public static final String
		// URI_ARG in superclass
		SRCFILE_OPT = "file";
		
	
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam parentTypeParam = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		FileParam srcFileParam = (FileParam) cl.getOption(SRCFILE_OPT);
		StringParam pathUriParam = (StringParam) cl.getArg(URI_ARG);
		File srcFile = null;
		String pathUri = null;
		String parentType = "d"; // default to doc

		if (pathUriParam.isSet()) {
			pathUri = pathUriParam.getValue();
		}
		if (parentTypeParam.isSet()) {
			parentType = parentTypeParam.getValue()
					.toLowerCase()
					.trim();
		}
		srcFile = srcFileParam.getValue();
		
		return aclImport(parentType, pathUri, srcFile);
	}

	
	/**
	 * @param parentType
	 * @param pathUri
	 * @param exportFile
	 */
	public boolean aclImport(
			String parentType, 
			String pathUri,
			File srcFile) 	throws Exception {
		AccessPermissionList accessPermissionList = null;
		String description = null;
		
		accessPermissionList = loadAcls(srcFile);
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			applyPermissionsToDoc(pathUri, accessPermissionList);
			description = "Document permissions";
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			applyPermissionsToFolder(pathUri, 
					accessPermissionList);
			description = "folder permissions";
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			applyPermissionsToClassDefinition(pathUri,
					accessPermissionList);
			description = "class definition";
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			applyPermissionsToClassDefaultInstance(pathUri, accessPermissionList);
			description = "Default class definition permissions";
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			applyPermissionsToDomain(pathUri, accessPermissionList);
			description = "Domain permissions";
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)){
			applyPermissionsToObjectStore(pathUri, accessPermissionList);
			description = "Object store permissions";
		} else if (ACLTYPE.PROPERTY_TEMPLATE.equals(parentType)) {
			applyPermissionsToPropertyTemplate(pathUri, accessPermissionList);
			description = "Property template permissions";
		} else {
			return false;
		}
		getResponse().printOut("Applied " + 
				description + " from " + 
				srcFile.toString() + " to "  + pathUri);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		FileParam srcFileOpt = null;
		StringParam aclParentTypeOpt = null;
		StringParam pathUriArg = null;
		
		// options
		srcFileOpt = new FileParam(SRCFILE_OPT,
				"Src files for import)",
				FileParam.IS_FILE & FileParam.IS_READABLE,
				FileParam.REQUIRED);
		srcFileOpt.setOptionLabel("<srcfile>");
		srcFileOpt.setMultiValued(false);
		
		aclParentTypeOpt = getAclParentTypeOpt();
		
		// args
		pathUriArg = getPathUriArg();
		
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { srcFileOpt, aclParentTypeOpt }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

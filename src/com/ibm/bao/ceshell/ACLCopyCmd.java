/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.ArrayList;
import java.util.List;
import com.filenet.api.collection.AccessPermissionList;

import jcmdline.CmdLineHandler;
import jcmdline.HelpCmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  ACLCopyCmd
 *
 * @author GaryRegier
 * @date   Jul 1, 2011
 */
public class ACLCopyCmd extends AbsAclCmd {
	
	// Options:
	// NOTE: some options are inherited from parent class
	public static final String
		SRC_URI_OPT = "src",
		DEST_URI_ARG = "dest";
	
	
	public static final String 
		CMD = "aclcopy", 
		CMD_DESC = "Copy an ACL from one object to another.",
		HELP_TEXT = CMD_DESC +
		
		"\nUsage:" +
		"\naclcopy -type dc -src Foo BAZ" + 
		"\n\tcopies the acl from class FOO to class BAZ" +
		"\n" + 
		"\naclcopy /TestFolder/test1.txt /TestFolder/doc2.pdf" +
		"\n\tcopies the acl from the test1.txt document to doc2.pdf" +
		"\n\naclcopy -t f -s .. flda fldb fldc" +
		"\n\tcopies the acl from the parent folder to folder flda, fldb, fldc";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam aclParentTypeOpt = (StringParam) cl.getOption(ACL_PARENT_TYPE_OPT);
		String aclParentType = ACLTYPE.DOCUMENT; // default to doc
		String srcPathUri = cl.getOption(SRC_URI_OPT).getValue().toString();
		StringParam destPathUriArg = (StringParam) cl.getArg(DEST_URI_ARG);
		List<String> destPathUris = destPathUriArg.getValues();
		
		if (aclParentTypeOpt.isSet()) {
			aclParentType = aclParentTypeOpt.getValue().toString();
		}
		return aclParentType(aclParentType, srcPathUri, destPathUris);
	}

	/**
	 * @param aclParentTypeOpt
	 * @param srcPathUri
	 * @param destPathUriArg
	 */
	public boolean aclParentType(String parentType, 
			String srcPathUri,
			List<String> destPathUris) throws Exception {
		AccessPermissionList srcPermissionList = null;
		List<ACLCopyResults> results = new ArrayList<ACLCopyResults>();
		
		if (ACLTYPE.DOCUMENT.equals(parentType)) {
			srcPermissionList = fetchDocumentPermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToDoc(destPathUri, srcPermissionList);
					copyResult.success("successfully added to document");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
			
		} else if (ACLTYPE.FOLDER.equals(parentType)) {
			srcPermissionList = fetchFolderPermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToFolder(destPathUri, srcPermissionList);
					copyResult.success("successfully added to folder");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
		} else if (ACLTYPE.CLASS_DEFINITION.equals(parentType)) {
			srcPermissionList = fetchClassPermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToClassDefinition(destPathUri, srcPermissionList);
					copyResult.success("successfully added to class definition");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
		} else if (ACLTYPE.DOMAIN.equals(parentType)) {
			// can't copy to a domain
			ACLCopyResults copyResult = new ACLCopyResults(parentType, "");
			copyResult.fail("Can not copy permissions on a domain");
			results.add(copyResult);
			
		} else if (ACLTYPE.CLASS_DEFINITION_DEFAULT.equals(parentType)) {
			srcPermissionList = fetchDefaultClassPermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToClassDefaultInstance(destPathUri, srcPermissionList);
					copyResult.success("successfully added to class default instance");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
		} else if (ACLTYPE.OBJECT_STORE.equals(parentType)) {
			srcPermissionList = fetchObjectStorePermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToObjectStore(destPathUri, srcPermissionList);
					copyResult.success("successfully added to object store");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
		} else if (ACLTYPE.PROPERTY_TEMPLATE.equals(parentType)) {
			srcPermissionList = fetchPropertyTemplatePermissionList(srcPathUri);
			for (String destPathUri : destPathUris) {
				ACLCopyResults copyResult = new ACLCopyResults(parentType, destPathUri);
				try {
					applyPermissionsToPropertyTemplate(destPathUri, srcPermissionList);
					copyResult.success("successfully added to property permission");
				} catch (Exception e) {
					copyResult.fail(e.getMessage());
				}
				results.add(copyResult);
			}
		}
		
		formatResults(srcPathUri, results);
		return evaluateResults(results);
		
	}

	/**
	 * @param results
	 * @return
	 */
	private boolean evaluateResults(List<ACLCopyResults> results) {
		
		for (ACLCopyResults aclCopyResult : results) {
			if (! aclCopyResult.isSuccess()) {
				return false; 
			}
		}
		return true;
	}

	/**
	 * @param results
	 */
	private void formatResults(String srcPathUri, List<ACLCopyResults> results) {
		StringBuffer buf = new StringBuffer();
		buf.append("Copy permissions from " + srcPathUri + "\n");
		for (ACLCopyResults nextResult : results) {
			buf.append(nextResult.toString()).append("\n");
		}
		getResponse().printOut(buf.toString());
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam aclParentTypeOpt = null;
		StringParam srcPathUriOpt = null;
		StringParam destPathUriArg = null;
		
		// options
		aclParentTypeOpt = getAclParentTypeOpt();
		
		srcPathUriOpt = new StringParam(SRC_URI_OPT,
				"source acl to copy from",
				StringParam.REQUIRED);
		
		
		destPathUriArg = new StringParam(DEST_URI_ARG, 
				"destination object to copy to",
				StringParam.REQUIRED);
		destPathUriArg.setMultiValued(true);
		// TODO: Can this be multi-valued?
		// could be changed to args
		
		// cmd args
	
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {aclParentTypeOpt, srcPathUriOpt }, 
					new Parameter[] { destPathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

class ACLCopyResults {
	private boolean success = false;
	private String aclType;
	private String destUri;
	private String result = "";
	
	public ACLCopyResults() {
		super();
	}

	public ACLCopyResults(String aclType, String destUri) {
		super();
		this.aclType = aclType;
		this.destUri = destUri;
	}

	public void success(String msg) {
		this.result = msg;
		success = true;
	}
	public boolean isSuccess() {
		return success;
	}
	
	public void fail(String msg) {
		this.result = msg;
		this.success = false;
	}
	
	@Override
	public String toString() {
		String str;
		
		str = destUri + "\t(" + aclType + ")";
		if(success) {
			str += "\tsuccess";
		} else {
			str += "\tfail " + result;
		}
		return str;
	}
}

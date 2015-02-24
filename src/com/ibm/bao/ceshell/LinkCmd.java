/**
 * 
 */
package com.ibm.bao.ceshell;

import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  LinkCmd
 *
 * @author GaryRegier
 * @date   Jul 21, 2011
 */
public class LinkCmd extends BaseCommand {
	
	private static final String 
		CMD = "ln", 
		CMD_DESC = "link a document to a target directory",
		HELP_TEXT = CMD_DESC +
		"By default, the link name is the Name property of the document. If the " +
		"name propert is unset, then then document id is used for the link name." +
		"Usage:" +
		  	"\n\tdocadd -target-directory /TestFolder /foo/baz.txt\n" + 
		  	"\n\t link document /foo/baz.txt to folder /TestFolder";
	
	private static final String
		TARGET_DIRECTORY_OPT = "target-directory",
		DOC_URI_ARG = "docUri";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam targetDirectoryOpt = (StringParam) cl.getOption(TARGET_DIRECTORY_OPT);
		StringParam docUriArg = (StringParam) cl.getArg(DOC_URI_ARG);
		String targetDirectoryUri = targetDirectoryOpt.getValue();
		String docUri = docUriArg.getValue();
		
		return link(targetDirectoryUri, docUri);
	}

	/**
	 * @param targetDirectoryUri
	 * @param docUri
	 */
	public boolean link(String targetDirectoryUri, String docUri) {
		String title = null;
		Document doc = null;
		Folder folder = null;
		String containmentName = null;
		
		folder = fetchFolder(targetDirectoryUri);
		if (folder == null) {
			getResponse().printErr("No folder with " + targetDirectoryUri + " exists");
			return false;
		}
		doc = fetchDoc(docUri);
		if (doc == null) {
			getResponse().printErr("No document with " + docUri + " was found.");
			return false;
		}
		title = getDocName(doc);
		containmentName = doLinkDocToFolder(folder, doc, title);
		getResponse().printOut("link created: " + containmentName);
		return true;
	}

	public String getDocName(Document doc) {
		String title;
		title = doc.get_Name();
		if (title == null || "".equals(title)) {
			title = doc.get_Id().toString();
		}
		return title;
	}

//	private void fileInFolder(Document doc, String parentFolderUri, String title) {
//		Folder parentFolder = null;
//		if (parentFolderUri == null) {
//			//TODO does the doc class have a default folder?
//			return;
//		}
//		if (getShell().isId(parentFolderUri)) {
//			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
//					new Id(parentFolderUri), null);
//		   
//		} else {
//			String decodedUri = getShell().urlDecode(parentFolderUri);
//			String fullPath = ceShell.getCWD().relativePathToFullPath(decodedUri);
//			parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), 
//					fullPath, null);
//		}
//		// File the document
//		ReferentialContainmentRelationship rcr = parentFolder.file(doc,
//		        AutoUniqueName.AUTO_UNIQUE, title,
//		        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
//		rcr.save(RefreshMode.NO_REFRESH);
//	}

	/**
	 * @param folder
	 * @param doc
	 */
	private String doLinkDocToFolder(Folder folder, Document doc, String title) {
		ReferentialContainmentRelationship rcr = folder.file(doc,
		        AutoUniqueName.AUTO_UNIQUE, title,
		        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rcr.save(RefreshMode.REFRESH);
		return rcr.get_ContainmentName();
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam targetDirectoryOpt = null;
		StringParam docUriArg = null;

		// options
		targetDirectoryOpt = new StringParam(TARGET_DIRECTORY_OPT, 
				"target-directory");
		targetDirectoryOpt.setOptional(StringParam.REQUIRED);
		targetDirectoryOpt.setMultiValued(StringParam.SINGLE_VALUED);
		
		// cmd args
		docUriArg = new StringParam(DOC_URI_ARG, "document to link", StringParam.REQUIRED);
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {targetDirectoryOpt}, 
					new Parameter[] {docUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

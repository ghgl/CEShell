/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  RMCmd
 *  Remove items in a folder similar to the UNIX rm command
 *
 * @author GaryRegier
 * @date   Apr 29, 2011
 */
public class RMCmd extends BaseCommand {
	
	private static final String 
		CMD = "rm", 
		CMD_DESC = "Remove items in a folder similar to the UNIX rm command.",
		HELP_TEXT = "Delete a folder. Optionally, if the -r switch is passed, then \n" +
		            "recursively remove all files and folders underneath the directory\n." +
					"Example: \n" +
					"\trm /TestFolder/test1	: remove folder\n" +
					 "\trm -r /TestFolder/tesst1 : remove folder and all documents linked";

	// param names
	private static final String 
		RECURSE_OPT = "recurse",
		URI_ARG = "URI";
	
	/* TODO:  Support for rm of a doc, custom object,
	 * (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam recurseOpt = (BooleanParam) cl.getOption(RECURSE_OPT);
		StringParam pathURIArg = (StringParam) cl.getArg(URI_ARG);
		Boolean recurse = recurseOpt.getValue();
		String pathUri = pathURIArg.getValue();
		
		return rm(pathUri, recurse);
	}

	/**
	 * @param pathUri
	 * @param recurse
	 */
	public boolean rm(String pathUri, Boolean recurse) throws Exception {
		String fullPath = this.pathUriToFullPath(pathUri);
		if (getShell().getCWD().isRootDir(fullPath)) {
			throw new IllegalArgumentException("Can not delete the root directory");
		}
		Folder parentFolder = Factory.Folder.fetchInstance(getShell().getObjectStore(), fullPath, null);
		doRMFolder(fullPath, parentFolder, recurse);
		return true;
	}

	/**
	 * @param fullPath
	 * @param parentFolder
	 */
	private void doRMFolder(String fullPath, Folder parentFolder, Boolean recurse) {
		if (recurse) {
			// delete child folders
			{
				Iterator<?> iter = parentFolder.get_SubFolders().iterator();
				while (iter.hasNext()) {
					Folder childFolder = (Folder) iter.next();
					String childPath = fullPath + "/" + childFolder.get_FolderName();
					doRMFolder(childPath, childFolder, recurse);
				}
			}
			
			// delete child docs
			{
				
				Iterator<?> iter = parentFolder.get_ContainedDocuments().iterator();
				while (iter.hasNext()) {
					Document nextDoc = (Document) iter.next();
					Id docId = nextDoc.get_Id();
					doDelDoc(docId, fullPath);
				}
			}
		}
		doDelFolder(parentFolder, fullPath);
	}

	/**
	 * @param parentFolder
	 */
	private void doDelFolder(Folder folder, String fullPath) {
		folder.delete();
		folder.save(RefreshMode.REFRESH);
		getResponse().printOut("Deleted folder" + fullPath);
	}

	/**
	 * @param docId
	 * @param fullPath
	 */
	private void doDelDoc(Id docId, String fullPath) {
		Document doc = Factory.Document.getInstance(
				getShell().getObjectStore(), 
				"Document", 
				docId);
		doc.delete();
		doc.save(RefreshMode.NO_REFRESH);
		getResponse().printOut("Deleted " + docId + " in " + fullPath);
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		BooleanParam recurseOpt = null;
		StringParam pathURIArg = null;

		// params
		recurseOpt = new BooleanParam(RECURSE_OPT, "recurse through the directory");
		recurseOpt.setOptional(true);
		recurseOpt.setMultiValued(false);
		
		// cmd args
		pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { recurseOpt }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

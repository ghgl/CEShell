/**
 * 
 */
package com.ibm.bao.ceshell;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  TreeCmd
 *
 * @author regier
 * @date   Oct 29, 2014
 */
public class TreeCmd extends BaseCommand {
	
	private static final String 
		CMD = "tree", 
		CMD_DESC = "list the directory tree",
		HELP_TEXT = CMD_DESC;

	private static final String
		URI_ARG = "URI";

	private static final String DEFAULT_FOLDER = ".";
	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam folderUriParam = (StringParam) cl.getArg(URI_ARG);
		String folderUri = folderUriParam.getValue();
		
		return doTree(folderUri);
	}

	/**
	 * @param folderUri
	 * @return
	 */
	private boolean doTree(String folderUri) {
		String decodedPath = readPathFromArg(folderUri);
		String fullFolderPath = this.getShell().getCWD().relativePathToFullPath(decodedPath);
		
		// select PathName from Folder where Folder.This InSubFolder('/Some/Path')
		String query = "select FolderPath from Folder Where Folder.This  InSubFolder('" + fullFolderPath + "')";
		
		
		return false;
	}
	
	
	
	private String readPathFromArg(String encodedPath) {
		String decodedPath = null;
	
		if (encodedPath != null) {
			decodedPath = this.decodePath(encodedPath);
		}
		
		if (decodedPath == null) {
			decodedPath = DEFAULT_FOLDER;
		}
		
		return decodedPath;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		StringParam pathURIArg = null;

		// options
		

		// cmd args
		pathURIArg = new StringParam(URI_ARG, "URI indicating a folder",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

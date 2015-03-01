/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

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
	 *
	 * @param folderUri
	 * @return
	 * @throws Exception 
	 */
	private boolean doTree(String folderUri) throws Exception {
		String decodedPath = readPathFromArg(folderUri);
		String fullFolderPath = this.getShell().getCWD().relativePathToFullPath(decodedPath);
		
		SortedSet<String> results = new TreeSet<String>();
		String query = "select PathName from Folder Where Folder.This  InSubFolder('" + fullFolderPath + "')";
		SearchSQL sqlObject = new SearchSQL();
	    
	    sqlObject.setQueryString(query);
	  
	    SearchScope searchScope = new SearchScope(ceShell.getObjectStore());
	    RepositoryRowSet rowSet = searchScope.fetchRows(sqlObject, null, null, new Boolean(true));
	    
	    Iterator<?> iter = rowSet.iterator();
	    /** Add full folder path as first path, then add children **/
	     
	    results.add(fullFolderPath);
	    while ( iter.hasNext()) {
	    	RepositoryRow row = (RepositoryRow) iter.next();
	    	String nextPath = row.getProperties().get("PathName").getStringValue();
	    	results.add(nextPath);
	    }
	    for (String nextFolder : results) {
			System.out.println(nextFolder);
		}
		return true;
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

/**
 * 
 */
package com.ibm.bao.ceshell;


import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.util.Id;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  DocDelCmd
 *
 * @author GaryRegier
 * @date   Apr 29, 2011
 */
public class DocDelCmd extends BaseCommand {
	
	private static final String 
		CMD = "docdel", 
		CMD_DESC = "delete files",
		HELP_TEXT = "Delete a file\n" +
		"Example: docdel /TestFolder/foo.pdf";
	
	// param names
	private static final String 
			URI_ARG = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam remoteDocArg = (StringParam) cl.getArg(URI_ARG);
		String docUri = null;
		
		docUri = remoteDocArg.getValue();
		return docDel(docUri);
	}
	
	public boolean docDel(String docUri)throws Exception {
		Document doc = null;
		ObjectStore os = getShell().getObjectStore();
		if (getShell().isId(docUri)) {
//			doc = Factory.Document.getInstance(getShell().getObjectStore(), 
//					"Document", 
//					new Id(docUri));
			doc = Factory.Document.fetchInstance(os, new Id(docUri), null);
		} else {
			String decodedUri = getShell().urlDecode(docUri);
			String fullPath = getShell().getCWD().relativePathToFullPath(decodedUri);
//			doc = Factory.Document.getInstance(getShell().getObjectStore(), 
//					"Document", 
//					fullPath);
			doc = Factory.Document.fetchInstance(os, fullPath, null);
		} 
		//doc.delete();
		VersionSeries vs = doc.get_VersionSeries();
		vs.delete();
		vs.save(RefreshMode.REFRESH);
		getResponse().printOut("Deleted " + docUri);
		
		return true;
	}
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam pathURIArg = null;

		// params
		
		
		// cmd args
		pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
				StringParam.REQUIRED);
		pathURIArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

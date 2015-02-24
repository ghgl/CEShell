/**
 * 
 */
package com.ibm.bao.ceshell;

import com.filenet.api.core.Document;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.impl.DocEditInfo;
import com.ibm.bao.ceshell.impl.EditInfo;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  EditCmd
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class EditCmd extends BaseCommand {
	
	private static final String 
		CMD = "edit", 
		CMD_DESC = "edit a document properties",
		HELP_TEXT=CMD_DESC;

	// param names
	private static final String 
		URI_ARG = "uri";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam pathUriArg = (StringParam) cl.getArg(URI_ARG);
		String uri = pathUriArg.getValue();
		return edit(uri);
	}

	/**
	 * @param uri
	 */
	public boolean edit(String uri) {
		Document doc = this.fetchDoc(uri);
		EditInfo currentEditInfo = new DocEditInfo(this.getShell(), doc, doc.getClassName());
		getShell().setCurrentEditInfo(currentEditInfo);
		getShell().setMode(CEShell.MODE_EDIT);
		getResponse().printOut("edit...(use \'set\', \'save\', and \'cancel\' commmands");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
//		StringParam parentTypeOpt = null;
		StringParam pathUriArg = null;
		
		// options
//		parentTypeOpt = getAclParentTypeOpt();
		
		// cmd args
		{
		
			String pathURIDesc = "URI indicating a file or folder. It can also be "+
				"the ID of a document or folder. If the type is a document class, " +
				"the value can be the name of the document class";
				
				
				 pathUriArg = new StringParam(URI_ARG, 
						pathURIDesc,
						StringParam.REQUIRED);
				pathUriArg.setMultiValued(false);
				
		}
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { pathUriArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

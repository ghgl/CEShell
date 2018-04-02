/**
 * 
 */
package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.DocCheckoutDTO;
import com.ibm.bao.ceshell.util.DocUtil;

/**
 *  DocUnlockCmd
 *
 * @author regier
 * @date   Mar 26, 2015
 */
public class DocUnlockCmd extends BaseCommand {
	
	
	private static final String 
	CMD = "docunlock", 
	CMD_DESC = "Cancel the lock on a checked-out document",
	HELP_TEXT = "example:  \n" + 
				"\t  docckout <uri> -- checkout a document with default object store lock\n" +
				"\t  docunlock <uri>   -- cancel the lock on a document\n";

private static final String

	URI_ARG = "URI";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam docUriParam = (StringParam) cl.getArg(URI_ARG);
		String docUri = null;
		
		docUri = docUriParam.getValue();
		
		return doUnlock(docUri);
	}
	
	/**
	 * 
	 * 
	 * @param docUri
	 * @return
	 * @throws Exception 
	 */
	private boolean doUnlock(String docUri) throws Exception {
		DocUtil docUtil = new DocUtil(this.getShell());
		DocCheckoutDTO docCheckoutInfo = new DocCheckoutDTO(docUri);
		
		return docUtil.cancelCheckout(docCheckoutInfo);
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
		{
			pathURIArg = new StringParam(URI_ARG, "URI indicating a document",
					StringParam.REQUIRED);
			pathURIArg.setMultiValued(false);
		}
		{
			
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] { pathURIArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

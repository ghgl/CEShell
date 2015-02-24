/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.StringUtil;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  WorkflowNamesLsCmd
 *
 * @author GaryRegier
 * @date   Jun 26, 2011
 */
public class WorkflowNamesLsCmd extends BasePECommand {
	
	private static final String 
		CMD = "pe.wfnamesls", 
		CMD_DESC = "fetch the workflow names that have been transferred into the isolated region",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"pe.wfnamesls";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return fetchWorkflowNames();
	}

	/**
	 * 
	 */
	public boolean fetchWorkflowNames()throws Exception {
		String[] names = getShell().getPEConnection()
			.fetchWorkClassNames(true,null);
		java.util.Arrays.sort(names);
		
		getResponse().printOut(
				"Transferred workflow definitions:\n")
				.printOut(StringUtil.appendArrayToString("", "\n\t", names));
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		
		// options
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}

/**
 * 
 */
package com.ibm.bao.ceshell;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  CancelCmd
 *
 * @author regier
 * @date   Sep 29, 2011
 */
public class CancelCmd extends BaseCommand {
	
	private static final String 
		CMD = "cancel", 
		CMD_DESC = "Cancel the current edit.",
		HELP_TEXT = CMD_DESC +
			"The mode must be the Edit mode before the cancel it executed" +
			"Usage: \n" +
			"cancel";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return cancel();

	}

	/**
	 * 
	 */
	public boolean cancel() {
		this.getShell().setCurrentEditInfo(null);
		String mode = getShell().getMode();;
		if (! CEShell.MODE_EDIT.equals(mode)) {
			throw new IllegalStateException("Not in edit mode");
		}
		getShell().setMode(CEShell.MODE_CMD);
		getResponse().printOut("canceled current edit");
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
					new Parameter[] {}, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}


}

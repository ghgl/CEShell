/**
 * 
 */
package com.ibm.bao.ceshell;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  SaveCmd
 *
 * @author regier
 * @date   Sep 8, 2011
 */
public class SaveCmd extends BaseCommand {
	
	public static final String 
		CMD = "save", 
		CMD_DESC = "Save an object that has been fetched for editing",
		HELP_TEXT = CMD_DESC;

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return save();
	}
	
	

	/**
	 * 
	 */
	public boolean save() {
		try {
			this.getShell().getCurrentEditInfo().save();
			getResponse().printOut("saved");
			return true;
		} finally {
			this.getShell().setCurrentEditInfo(null);
			getShell().setMode(CEShell.MODE_CMD);
		}
	}



	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		
		
		// options
		
		// cmd args
		

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}

}

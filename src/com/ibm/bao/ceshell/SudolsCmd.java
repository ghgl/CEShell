/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Enumeration;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  SudolsCmd
 *
 * @author GaryRegier
 * @date   Oct 16, 2010
 */
public class SudolsCmd extends BaseCommand {
	
	private static final String 
		CMD = "sudols", 
		CMD_DESC = "list sudo users",
		HELP_TEXT = "\nList the users in the sudo properties file" +
			"\nUsage:" +
			"\n\tsudols";
		

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return sudols();
	}

	/**
	 * 
	 */
	public boolean sudols() throws Exception {
		Enumeration<Object> iter = getShell().getSudoList().keys();
		getResponse().printOut("Sudo users for " + 
				getShell().getConnectionDescription());
		while (iter.hasMoreElements()) {
			String nextUser = iter.nextElement().toString();
			getResponse().printOut("\t" + nextUser);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;

		// params
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {  }, 
					new Parameter[] {  });
		cl.setDieOnParseError(false);

		return cl;
	}

}

/**
 * 
 */
package com.ibm.bao.ceshell;

import com.filenet.api.core.Factory;
import com.filenet.api.security.User;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  WhoAmICmd
 *
 * @author GaryRegier
 * @date   Jun 25, 2011
 */
public class WhoAmICmd extends BaseCommand {
	
	private static final String 
		CMD = "whoami", 
		CMD_DESC = "Print the dn associated  with the current effective credentials.",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"whomai";


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return whoAmI();

	}

	/**
	 * 
	 */
	public boolean whoAmI() throws Exception {
		User user = null;
		String msg = null;
		user = Factory.User.fetchCurrent(
				getShell().getCEConnection(), null);
		
		msg = user.get_DisplayName() + " (" + user.get_DistinguishedName() + ")";
		getResponse().printOut(msg);
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

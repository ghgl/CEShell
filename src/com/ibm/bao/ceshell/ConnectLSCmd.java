/**
 * 
 */
package com.ibm.bao.ceshell;

import java.util.Iterator;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

/**
 *  ConnectLSCmd
 *  List the connection alias information
 *
 * @author GaryRegier
 * @date   Nov 14, 2010
 */
public class ConnectLSCmd extends BaseCommand {
	private static final String 
		CMD = "connectls", 
		CMD_DESC = "list connection aliases",
		HELP_TEXT = "List connection aliases";

// param names
private static final String
	LONG_OPT = "long";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam listLongParam = (BooleanParam) cl.getOption(LONG_OPT);
		Boolean listLong = Boolean.FALSE;
		
		if (listLongParam.isSet()) {
			listLong = listLongParam.getValue();
		}
		return connectLs(listLong);
	}

	/**
	 * @param listLong
	 */
	public boolean connectLs(Boolean listLong) {
		Iterator<String> iter = getShell().getConnectionMgr()
				.getConnectionAliasNames().iterator();
		while (iter.hasNext()) {
			String nextAlias = iter.next();
			getResponse().printOut(nextAlias);
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
		BooleanParam longOpt = null;
		
		// params
		{
			longOpt = new BooleanParam(LONG_OPT,
					"list the connections in long format",
					BooleanParam.OPTIONAL);
			longOpt.setMultiValued(false);
		}
		// cmd args
		{
			// none
		}
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {longOpt }, 
					new Parameter[] { });
		cl.setDieOnParseError(false);

		return cl;
	}
}

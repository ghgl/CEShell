package com.ibm.bao.ceshell.pe;

import java.util.Arrays;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWRoster;
import filenet.vw.api.VWSession;
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

public class RostersListCmd extends BasePECommand {

	// private static final String
	// NON_ZERO_OPT = "non-zero";

	private static final String CMD = "pe.rsls", CMD_DESC = "list the rosters",
			HELP_TEXT = CMD_DESC + "Usage: \n" + "pe.rsls";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return rostersList();
	}

	public boolean rostersList() throws Exception {
		VWSession session = getShell().getPEConnection();
		String[] rosters = null;
		boolean ignoreSecurity = true;

		rosters = session.fetchRosterNames(ignoreSecurity);
		Arrays.sort(rosters);

		getResponse().printOut("Rosters:");
		for (int i = 0; i < rosters.length; i++) {
			VWRoster roster = session.getRoster(rosters[i]); 
			int depth = roster.fetchCount();
			getResponse().printOut("\t" +depth + "\t" + rosters[i]);
		}
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam nonZeroOpt = null;

		// options

		// cmd args

		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC, new Parameter[] {}, new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}

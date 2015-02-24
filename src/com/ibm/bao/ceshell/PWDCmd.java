package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

public class PWDCmd extends BaseCommand {

	private static final String 
		CMD = "pwd", 
		CMD_DESC = "print working directory",
		HELP_TEXT = "pwd";
	

	
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return printWorkingDirectory();
	}
	
	public boolean printWorkingDirectory() {
		getResponse().printOut(getShell().getCWD().pwd());
		return true;
	}

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

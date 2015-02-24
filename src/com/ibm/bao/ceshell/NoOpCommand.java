package com.ibm.bao.ceshell;

import jcmdline.CmdLineHandler;

public class NoOpCommand extends BaseCommand {

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		
		return true;
	}

	@Override
	protected CmdLineHandler getCommandLine() {
		
		return null;
	}

	

}

package com.ibm.ucm.ecm.ceshell;

import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.CEConnectInfo;
import com.ibm.bao.ceshell.CEShell;
import com.ibm.bao.ceshell.cm.SolutionLockCmd;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

public class SolutionLocksCmd extends BaseCommand {
	
	public static final String 
		DESIGN_OBJECTSTORE_NAME = "DOS",
		SOLUTION_NAME = "Unified Case Manager";
	
	private static final String 
		CMD = "ucm.slocks", 
		CMD_DESC = "List the Solution locks",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"ucm.slocks";

	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		return solutionLocks();
	}

	public boolean solutionLocks() throws Exception {
		CEConnectInfo currentConectionInfo = getShell().getCEConnectInfo();
		CEShell shell = this.getShell();
		shell.execute("use DOS");
		shell.execute("cm.slocks 'Unified Case Manager'");
		shell.execute("use " + currentConectionInfo.getObjectStore());
		return true;
//		try {
//			getShell().useOjectStore(SolutionLocksCmd.DESIGN_OBJECTSTORE_NAME);
//			SolutionLockCmd slc = new SolutionLockCmd();
//			
//			return slc.solutionLocksProps(SolutionLocksCmd.SOLUTION_NAME);
//		} finally {
//			getShell().useOjectStore(currentConectionInfo.getObjectStore());
//		}
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

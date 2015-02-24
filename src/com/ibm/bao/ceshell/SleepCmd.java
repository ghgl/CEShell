/**
 * 
 */
package com.ibm.bao.ceshell;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import jcmdline.CmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;

/**
 *  SleepCmd
 *
 * @author regier
 * @date   Nov 6, 2011
 */
public class SleepCmd extends BaseCommand {
	
	// param names
	private static final String 
		SECONDS_TO_SLEEP_ARG = "seconds-to-sleep";
	
	public static final String 
		CMD = "sleep", 
		CMD_DESC = "Sleep for a number of seconds",
		HELP_TEXT = "Usage:" +
		"sleep <##> : sleep for a number of secords";
			

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		IntParam sleepSecondsArg = (IntParam) cl.getArg(SECONDS_TO_SLEEP_ARG);
		int sleepSeconds = sleepSecondsArg.getValue();
		
		return sleep(sleepSeconds);
	}

	/**
	 * @param sleepSeconds
	 */
	private boolean sleep(int sleepSeconds) {

		try {
			Thread.sleep(sleepSeconds * 1000);
		} catch (InterruptedException e) {
			// no-op
		}
		getResponse().printOut("Sleep for " + sleepSeconds + " seconds");
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		jcmdline.IntParam sleepSecondsArg = null;
		
		// options
		
		
		// cmd args
		sleepSecondsArg = new IntParam(
				SECONDS_TO_SLEEP_ARG, 
				"seconds to sleep", 
				IntParam.REQUIRED, 
				IntParam.SINGLE_VALUED);
		sleepSecondsArg.setMin(1);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { sleepSecondsArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

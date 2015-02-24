/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import java.util.Arrays;

import com.ibm.bao.ceshell.BaseCommand;
import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWQueue;
import filenet.vw.api.VWSession;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.Parameter;

/**
 *  PEQueueLsCmd
 *
 * @author GaryRegier
 * @date   Jun 25, 2011
 */
public class QueuesLsCmd extends BaseCommand {
	
	//private static final int ALL_FLAGS = (VWSession.QUEUE_PROCESS | VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
	private static final int  SYS_FLAGS= (VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
	private static final int  PROC_FLAGS = (VWSession.QUEUE_PROCESS | VWSession.QUEUE_IGNORE_SECURITY);

	private static final String 
		NON_ZERO_OPT = "non-zero";
	
	private static final String 
		CMD = "pe.qsls", 
		CMD_DESC = "list the work queues",
		HELP_TEXT = CMD_DESC +
			"Usage: \n" +
			"pe.qsls" +
			"pe.qsls -non-zero";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		BooleanParam nonZeroOpt = (BooleanParam) cl.getOption(NON_ZERO_OPT);
		Boolean nonZero = Boolean.FALSE;
		
		if (nonZeroOpt.isSet()) {
			nonZero = nonZeroOpt.getValue();
		}
		return qeuesLs(nonZero);

	}

	/**
	 * 
	 */
	public boolean qeuesLs(Boolean nonZeroOnly) throws Exception {
		VWSession session = getShell().getPEConnection();
		String[] queues = null;
	
		queues = session.fetchQueueNames(PROC_FLAGS);
		Arrays.sort(queues);
		
		getResponse().printOut("Work queues:");
		for (int i = 0; i < queues.length; i++) {
			VWQueue queue = session.getQueue(queues[i]);
			int depth = queue.fetchCount();
			if ( (nonZeroOnly.equals(Boolean.FALSE)) ||
					(depth > 0) ) {
				getResponse().printOut("\t" + depth + "\t" + queues[i]);
			}
		}
		
		getResponse().printOut("-----------------------------");
		getResponse().printOut("System queues");
		queues = session.fetchQueueNames(SYS_FLAGS);
		Arrays.sort(queues);
		for (int i = 0; i < queues.length; i++) {
			VWQueue queue = session.getQueue(queues[i]);
			int depth = queue.fetchCount();
			if ( (nonZeroOnly.equals(Boolean.FALSE)) ||
					(depth > 0) ) {
					getResponse().printOut("\t" + depth + "\t" + queues[i]);
				}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam nonZeroOpt = null;
		
		// options
		nonZeroOpt = new BooleanParam(NON_ZERO_OPT, "list queues with one or more queue items.");
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {nonZeroOpt}, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}

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
public class QueuesLsCmd extends BasePECommand {
	
	//private static final int ALL_FLAGS = (VWSession.QUEUE_PROCESS | VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
	private static final int  SYS_FLAGS= (VWSession.QUEUE_SYSTEM | VWSession.QUEUE_IGNORE_SECURITY);
	private static final int QUEUE_INBOX = VWSession.QUEUE_USER_CENTRIC;
	private static final int QUEUE_CURRENT_USER = VWSession.QUEUE_USER_CENTRIC_FOR_USER_ONLY;
	
	private static final int  PROC_FLAGS = (VWSession.QUEUE_USER_CENTRIC_FOR_USER_ONLY |
			                                VWSession.QUEUE_PROCESS | 
			                                VWSession.QUEUE_IGNORE_SECURITY);
	
	private static final String 
		NON_ZERO_OPT = "non-zero",
		LONG_OPT = "long";
	
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
		BooleanParam longOpt = (BooleanParam) cl.getOption(LONG_OPT);
		Boolean listLong = Boolean.FALSE;
		BooleanParam nonZeroOpt = (BooleanParam) cl.getOption(NON_ZERO_OPT);
		Boolean nonZero = Boolean.FALSE;
		
		if (longOpt.isSet()) {
			listLong = longOpt.getValue();
		}
		
		if (nonZeroOpt.isSet()) {
			nonZero = nonZeroOpt.getValue();
		}
		return qeuesLs(listLong, nonZero);

	}

	/**
	 * 
	 */
	public boolean qeuesLs(Boolean listLong, Boolean nonZeroOnly) throws Exception {
		VWSession session = getPEConnection();
		String[] queuesNames = null;
	
		queuesNames = session.fetchQueueNames(PROC_FLAGS);
		Arrays.sort(queuesNames);
		
		getResponse().printOut("Work queues:");
		if (listLong == Boolean.FALSE) {
			listShort(queuesNames);
			return true;
		}
		
		for (int i = 0; i < queuesNames.length; i++) {
			VWQueue queue = session.getQueue(queuesNames[i]);
			int depth = queue.fetchCount();
			if ( (nonZeroOnly.equals(Boolean.FALSE)) ||
					(depth > 0) ) {
				getResponse().printOut("\t" + depth + "\t" + queuesNames[i]);
			}
		}
		
		getResponse().printOut("-----------------------------");
		getResponse().printOut("System queues");
		queuesNames = session.fetchQueueNames(SYS_FLAGS);
		Arrays.sort(queuesNames);
		for (int i = 0; i < queuesNames.length; i++) {
			VWQueue queue = session.getQueue(queuesNames[i]);
			int depth = queue.fetchCount();
			if ( (nonZeroOnly.equals(Boolean.FALSE)) ||
					(depth > 0) ) {
					getResponse().printOut("\t" + depth + "\t" + queuesNames[i]);
				}
		}
		return true;
	}

	private void listShort(String[] queueNames) {
		for(String queueName: queueNames) {
			getResponse().printOut(queueName);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		CmdLineHandler cl = null;
		BooleanParam longOpt = null;
		BooleanParam nonZeroOpt = null;
		
		// options
		longOpt = new BooleanParam(LONG_OPT, "list long (include counts in queues)");
		longOpt.setOptional(BooleanParam.OPTIONAL);
		nonZeroOpt = new BooleanParam(NON_ZERO_OPT, "list queues with one or more queue items. (only applicable of -long option");
		nonZeroOpt.setOptional(BooleanParam.OPTIONAL);
		
		// cmd args
		
		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {longOpt, nonZeroOpt}, 
					new Parameter[] {});
		cl.setDieOnParseError(false);

		return cl;
	}

}

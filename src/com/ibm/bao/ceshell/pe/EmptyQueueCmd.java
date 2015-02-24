/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;
import filenet.vw.api.VWWorkObject;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  EmptyQueueCmd
 *
 * @author regier
 * @date   Nov 6, 2011
 */
public class EmptyQueueCmd extends BasePECommand {
	
	public static final String 
			CMD = "pe.emptyqueue", 
			CMD_DESC = "delete work in a work queue",
			HELP_TEXT = "Usage:" +
				"\npe.emptyqueue <queue-name>";

	// param names
	private static final String 
		QUEUE_NAME_ARG = "queue-name";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String queueName = cl.getArg(QUEUE_NAME_ARG).getValue().toString();
		
		return emptyQueue(queueName);
	}

	/**
	 * @param queueName
	 */
	private boolean emptyQueue(String queueName) throws Exception {
		VWQueueQuery queueQuery = executeQuery(queueName);
		int count = queueQuery.fetchCount();
		int deleted = 0;
		
		if (count != 0) {
			while( queueQuery.hasNext()) {
				try {
					VWQueueElement elem = (VWQueueElement) queueQuery.next();
					VWWorkObject wob = elem.fetchWorkObject(false, true);
					wob.doDelete(true, true);
					deleted++;
				} catch (Exception e) {
					getResponse().printErr("failed to deleted item: " + e.getMessage());
				}
			}
		}
		getResponse().printOut("Deleted " + deleted + " of " + count + 
				" work item in queue " + queueName);
		
		return true;
	}
	
	private VWQueueQuery executeQuery(String queueName) throws VWException,
			Exception {
		VWQueue queue = getShell().getPEConnection().getQueue(queueName);
		
		Integer fetchType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
		Object[] minValues = null;
		Object[] maxValues = null;
		int queryFlags = VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_LOCKED;
		Object[] substitutionVars = null;
		String filter = null;
		
		// TODO. Verify this works if an item is locked.
		VWQueueQuery queueQuery = queue.createQuery(
				null, 
				minValues, 
				maxValues, 
				queryFlags,		
				filter, 
				substitutionVars, 
				fetchType);
		return queueQuery;
	}


	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam queueNameArg = null;
		
		// options
		
		// cmd args
		queueNameArg = new StringParam(QUEUE_NAME_ARG, 
				"queue to empty",
				StringParam.REQUIRED);
		queueNameArg.setMultiValued(false);
		queueNameArg.setOptionLabel("<queue-name>");

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { queueNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}
}

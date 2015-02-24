/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;
import com.ibm.bao.ceshell.util.ColDef;
import com.ibm.bao.ceshell.util.StringUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueElement;
import filenet.vw.api.VWQueueQuery;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;
/**
 *  PEQLsCmd
 *
 * @author GaryRegier
 * @date   Jun 25, 2011
 */
public class QueueLsCmd extends com.ibm.bao.ceshell.pe.BasePECommand {
	
	public static final String 
		CMD = "pe.qls", 
		CMD_DESC = "list work items in a PE queue",
		HELP_TEXT = "Usage:" +
			"\npe.qls <queue-name>";

	// param names
	private static final String 
		QUEUE_NAME_ARG = "queue-name";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam queueNameArg = (StringParam) cl.getArg(QUEUE_NAME_ARG);
		String queueName = queueNameArg.getValue();
		
		return queueLs(queueName);
	}

	/**
	 * @param queueName
	 */
	public boolean queueLs(String queueName) throws Exception {
		VWQueueQuery queueQuery = executeQuery(queueName);
		displayResults(queueQuery);
		return true;
	}

	private void displayResults(VWQueueQuery queueQuery) throws Exception {
		ColDef[] defs = new ColDef[] {
				new ColDef("WobNum", 32, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Step Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Status", 14, StringUtil.ALIGN_LEFT),
				new ColDef("Received On", 28, StringUtil.ALIGN_LEFT),
				new ColDef("Workflow name", 33, StringUtil.ALIGN_LEFT),
				new ColDef("LockOwner", 20, StringUtil.ALIGN_LEFT)
			};
			getResponse().printOut(StringUtil.formatHeader(defs, " "));
		
			while( queueQuery.hasNext()) {
				VWQueueElement elem = (VWQueueElement) queueQuery.next();
				String lockOwner = elem.getLockedUser();
				String wobNum = elem.getWorkObjectNumber().toString();
				String lockStatusDesc = "In Progress";
				String queueDate = null;
				
				String name = (elem.getSubject() == null ? "[undefined value]" : elem.getSubject());
				String stepName = elem.getStepName();
				int lockStatus = elem.getLockedStatus();
				if (VWQueueElement.LOCKED_BY_NONE != lockStatus) {
					lockStatusDesc = "Locked";
				}
				String workflowName = elem.getWorkflowName();
				queueDate = fetchQueueDate(elem);
				String[] row = new String[] {
						wobNum,
						name, 
						stepName,
						lockStatusDesc,
						queueDate,
						workflowName,
						lockOwner
				};
			
			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
		}
	}

	/**
	 * @param elem
	 * @return
	 */
	private String fetchQueueDate(VWQueueElement elem) throws Exception {
		return elem.getFieldValue("F_EnqueueTime").toString();
	}

	private VWQueueQuery executeQuery(String queueName) throws VWException,
			Exception {
		VWQueue queue = getShell().getPEConnection().getQueue(queueName);
		String queryIndex = "F_Fifo";
		Integer fetchType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
		Object[] minValues = null;
		Object[] maxValues = null;
		int queryFlags = VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_LOCKED;
		Object[] substitutionVars = null;
		String filter = null;
		
		// TODO. Verify this works if an item is locked.
		VWQueueQuery queueQuery = queue.createQuery(
				queryIndex, 
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
				"queue to list",
				StringParam.REQUIRED);
		queueNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] { }, 
					new Parameter[] { queueNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

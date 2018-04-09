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
import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 * PEQLsCmd
 *
 * @author GaryRegier
 * @date Jun 25, 2011
 */
public class QueueLsCmd extends com.ibm.bao.ceshell.pe.BasePECommand {

	public static final String CMD = "pe.qls", CMD_DESC = "list work items in a PE queue",
			HELP_TEXT = "Usage:" + "\npe.qls <queue-name>";

	// param names
	private static final String QUEUE_NAME_ARG = "queue-name", MAX_OPT = "max";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String queueName = null;
		Integer maxItems = 0;
		IntParam maxOpt = (IntParam) cl.getOption(MAX_OPT);
		if (maxOpt.isSet()) {
			maxItems = maxOpt.getValue();
		}
		StringParam queueNameArg = (StringParam) cl.getArg(QUEUE_NAME_ARG);
		queueName = queueNameArg.getValue();

		return queueLs(queueName, maxItems);
	}

	/**
	 * @param queueName
	 */
	public boolean queueLs(String queueName, Integer maxItems) throws Exception {
		VWQueueQuery queueQuery = executeQuery(queueName);
		displayResults(queueQuery, maxItems);
		return true;
	}

	private void displayResults(VWQueueQuery queueQuery, Integer maxItems) throws Exception {
		ColDef[] defs = new ColDef[] { new ColDef("WobNum", 32, StringUtil.ALIGN_LEFT),
				new ColDef("Name", 30, StringUtil.ALIGN_LEFT), new ColDef("Step Name", 30, StringUtil.ALIGN_LEFT),
				new ColDef("Status", 14, StringUtil.ALIGN_LEFT), new ColDef("Received On", 28, StringUtil.ALIGN_LEFT),
				new ColDef("Workflow name", 33, StringUtil.ALIGN_LEFT),
				new ColDef("LockOwner", 20, StringUtil.ALIGN_LEFT) };
		getResponse().printOut(StringUtil.formatHeader(defs, " "));
		int cnt = 0;
		
		while (queueQuery.hasNext()) {
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
			String[] row = new String[] { wobNum, name, stepName, lockStatusDesc, queueDate, workflowName, lockOwner };

			getResponse().printOut(StringUtil.formatRow(defs, row, " "));
			cnt++;
			if ((maxItems > 0) && (cnt > maxItems)) {
				break;
			}
		}
	}

	/**
	 * @param elem
	 * @return
	 */
	private String fetchQueueDate(VWQueueElement elem) throws Exception {
		return elem.getFieldValue("F_EnqueueTime").toString();
	}

	private VWQueueQuery executeQuery(String queueName) throws VWException, Exception {
		VWQueue queue = getShell().getPEConnection().getQueue(queueName);
		String queryIndex = "F_Fifo";
		Integer fetchType = VWFetchType.FETCH_TYPE_QUEUE_ELEMENT;
		Object[] minValues = null;
		Object[] maxValues = null;
		int queryFlags = VWQueue.QUERY_READ_BOUND + VWQueue.QUERY_READ_LOCKED;
		Object[] substitutionVars = null;
		String filter = null;

		// TODO. Verify this works if an item is locked.
		VWQueueQuery queueQuery = queue.createQuery(queryIndex, minValues, maxValues, queryFlags, filter,
				substitutionVars, fetchType);
		return queueQuery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam queueNameArg = null;
		IntParam maxOpt = null;

		// options
		maxOpt = new IntParam(MAX_OPT, "maximum nuber of items to list");
		maxOpt.setOptional(IntParam.OPTIONAL);
		maxOpt.setMin(1);

		// cmd args
		queueNameArg = new StringParam(QUEUE_NAME_ARG, "queue to list", StringParam.REQUIRED);
		queueNameArg.setMultiValued(false);

		// create command line handler
		cl = new HelpCmdLineHandler(HELP_TEXT, CMD, CMD_DESC, new Parameter[] { maxOpt },
				new Parameter[] { queueNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}

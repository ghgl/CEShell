/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWException;
import filenet.vw.api.VWQueue;
import filenet.vw.api.VWQueueDefinition;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  QueueDefinitionCmd
 *
 * @author GaryRegier
 * @date   Jun 30, 2011
 */
public class QueueDefinitionCmd extends BasePECommand {
	
	public static final String 
		CMD = "pe.qdef", 
		CMD_DESC = "Display the queue definition",
		HELP_TEXT = "Usage:" +
			"\npe.qdef <queue-name>";
	
	// param names
	protected static final String 
		QUEUE_NAME_ARG = "queue-name";
	

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		String queueName = cl.getArg(QUEUE_NAME_ARG).getValue().toString();
		
		return queueDefinition(queueName);

	}

	/**
	 * @param queueName
	 */
	public boolean queueDefinition(String queueName) throws Exception {
		VWQueue q = getPEConnection().getQueue(queueName);
		VWQueueDefinition qd = null;
		
		if (q == null) {
			throw new IllegalArgumentException("Queue with name " + queueName + "not found");
		}
		qd = q.fetchQueueDefinition();

		formatResults(qd);
		return true;
	}

	private void formatResults(VWQueueDefinition qd) throws VWException {
		StringBuffer buf = new StringBuffer();
		qd.toXML(buf);
		getResponse().printOut(buf.toString());
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

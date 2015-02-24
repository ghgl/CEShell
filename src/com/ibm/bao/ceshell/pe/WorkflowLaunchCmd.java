/**
 * 
 */
package com.ibm.bao.ceshell.pe;

import com.ibm.bao.ceshell.cmdline.HelpCmdLineHandler;

import filenet.vw.api.VWStepElement;

import jcmdline.CmdLineHandler;
import jcmdline.Parameter;
import jcmdline.StringParam;

/**
 *  WorkflowLaunchCmd
 *
 * @author GaryRegier
 * @date   Jun 28, 2011
 */
public class WorkflowLaunchCmd extends BasePECommand {
	
	// param names
	private static final String 
		WORKFLOW_NAME_ARG = "workflow-name",
		RESPONSE_OPT = "response";
	
	public static final String 
		CMD = "pe.wflaunch", 
		CMD_DESC = "Launch a workflow",
		HELP_TEXT = "Usage:" +
			"\npe.wflaunch <workflow-name>" +
			"\npe.wflaunch -r <response> <wobnum>";

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#doRun(jcmdline.CmdLineHandler)
	 */
	@Override
	protected boolean doRun(CmdLineHandler cl) throws Exception {
		StringParam responseOpt = (StringParam) cl.getOption(RESPONSE_OPT);
		String response = null;
		String workflowName = cl.getArg(WORKFLOW_NAME_ARG).getValue().toString();
		
		if (responseOpt.isSet()) {
			response = responseOpt.getValue();
		}
		return launchWorkflow(workflowName, response);
	}

	/**
	 * @param workflowName
	 * @param response
	 */
	public boolean launchWorkflow(String workflowName, String response) 
			throws Exception {
		String decodedName = this.decodePath(workflowName);
		VWStepElement launchStep = getPEConnection().createWorkflow(decodedName);
		if (response != null) {
			String decodedResponse = this.decodePath(response);
			launchStep.setSelectedResponse(decodedResponse);
			
		}
		launchStep.doDispatch();
		String msg = "Launched workflow " + workflowName;
		if (response != null) {
			msg += " with response " + response;
		}
		getResponse().printOut(msg);
		return true;
	}

	/* (non-Javadoc)
	 * @see com.ibm.bao.ceshell.BaseCommand#getCommandLine()
	 */
	@Override
	protected CmdLineHandler getCommandLine() {
		// create command line handler
		CmdLineHandler cl = null;
		StringParam workflowNameArg = null;
		StringParam responseOpt = null;
		
		// options
		responseOpt = new StringParam(RESPONSE_OPT, 
				"step response",
				StringParam.OPTIONAL);
		// cmd args
		workflowNameArg = new StringParam(
				WORKFLOW_NAME_ARG,
				"workflow to launch",
				StringParam.REQUIRED);
		


		// create command line handler
		cl = new HelpCmdLineHandler(
						HELP_TEXT, CMD, CMD_DESC, 
					new Parameter[] {responseOpt }, 
					new Parameter[] {workflowNameArg });
		cl.setDieOnParseError(false);

		return cl;
	}

}
